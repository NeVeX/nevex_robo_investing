package com.nevex.investing.dataloader.loader;

import com.nevex.investing.TestingControlUtil;
import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.usfundamentals.UsFundamentalsApiClient;
import com.nevex.investing.api.usfundamentals.model.UsFundamentalsResponseDto;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.TickerToCikRepository;
import com.nevex.investing.database.entity.TickerToCikEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.service.ServiceException;
import com.nevex.investing.service.TickerFundamentalsAdminService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.util.CikUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Optional;

import static com.nevex.investing.dataloader.loader.DataLoaderOrder.TICKER_HISTORICAL_FUNDAMENTALS_LOADER;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class TickerHistoricalFundamentalsLoader extends DataLoaderSchedulingSingleWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerHistoricalFundamentalsLoader.class);
    private final UsFundamentalsApiClient apiClient;
    private final TickerToCikRepository tickerToCikRepository;
    private final TickerService tickerService;
    private final TickerFundamentalsAdminService tickerFundamentalsAdminService;
    private final long waitTimeBetweenTickersMs;

    public TickerHistoricalFundamentalsLoader(DataLoaderService dataLoaderService,
                                              TickerToCikRepository tickerToCikRepository,
                                              TickerFundamentalsAdminService tickerFundamentalsAdminService,
                                              TickerService tickerService,
                                              UsFundamentalsApiClient apiClient,
                                              DataLoaderProperties.HistoricalTickerFundamentalsLoaderProperties properties) {
        super(dataLoaderService, properties.getForceStartOnAppStartup());
        if ( apiClient == null ) { throw new IllegalArgumentException("Provided apiClient is null"); }
        if ( tickerToCikRepository == null ) { throw new IllegalArgumentException("Provided tickerToCikRepository is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        if ( tickerFundamentalsAdminService == null ) { throw new IllegalArgumentException("Provided tickerFundamentalsAdminService is null"); }
        this.apiClient = apiClient;
        this.tickerToCikRepository = tickerToCikRepository;
        this.tickerService = tickerService;
        this.tickerFundamentalsAdminService = tickerFundamentalsAdminService;
        this.waitTimeBetweenTickersMs = properties.getWaitTimeBetweenTickersMs();
    }

    @Override
    public int getOrderNumber() {
        return TICKER_HISTORICAL_FUNDAMENTALS_LOADER;
    }

    @Override
    public String getName() {
        return "ticker-historical-fundamentals-loader";
    }

    @Scheduled(initialDelay = 86400000L, fixedDelay = 86400000L) // 24 hours
    @Override
    void onScheduleStartInvoked() {
        super.scheduleStart();
    }

    @Override
    DataLoaderWorkerResult onWorkerStartedAtAppStartup() throws DataLoaderWorkerException {
        return DataLoaderWorkerResult.nothingDone(); // nothing to do at startup
    }

    @Override
    DataLoaderWorkerResult doScheduledWork() throws DataLoaderWorkerException {
        int amountProcessed = super.processAllPagesIndividuallyForIterable(tickerToCikRepository::findAll, this::processCik, waitTimeBetweenTickersMs);
        return new DataLoaderWorkerResult(amountProcessed);
    }

    private void processCik(TickerToCikEntity tickerToCikEntity) {
        Optional<String> symbolOptional = tickerService.tryGetSymbolForId(tickerToCikEntity.getTickerId());
        if ( !symbolOptional.isPresent()) {
            return; // nothing to do
        }
        String symbol = symbolOptional.get();

        if ( !TestingControlUtil.isTickerAllowed(symbol)) {
            return; // testing control in place - we cannot proceed
        }

        Optional<Long> parsedCik = CikUtils.parseCik(tickerToCikEntity.getCik());
        if ( !parsedCik.isPresent() ) {
            saveExceptionToDatabase("Could not parse raw cik ["+tickerToCikEntity.getCik()+"] into a long");
            return;
        }
        long cik = parsedCik.get();
        try {
            UsFundamentalsResponseDto fundamentalsResponse = apiClient.getAllFundamentalsForCik(cik);
            if ( fundamentalsResponse != null ) {
                tickerFundamentalsAdminService.saveHistoricalFundamentals(tickerToCikEntity.getTickerId(), fundamentalsResponse);
            } else {
                saveExceptionToDatabase("No data was returned from us-fundamentals for ticker ["+symbol+"]");
            }
        } catch (ApiException apiEx) {
            saveExceptionToDatabase("Could not get fundamental data for ticker ["+symbol+"]. Reason: "+apiEx.getMessage());
        } catch (ServiceException serviceEx) {
            saveExceptionToDatabase("Could not save fundamental data for ticker ["+symbol+"]. Reason: "+serviceEx.getMessage());
        }

    }

}
