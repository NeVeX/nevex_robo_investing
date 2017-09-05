package com.nevex.investing.dataloader.loader;

import com.nevex.investing.TestingControlUtil;
import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.usfundamentals.UsFundamentalsApiClient;
import com.nevex.investing.api.usfundamentals.model.UsFundamentalsResponse;
import com.nevex.investing.database.TickerToCikRepository;
import com.nevex.investing.database.entity.TickerToCikEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.service.ServiceException;
import com.nevex.investing.service.TickerFundamentalsAdminService;
import com.nevex.investing.service.TickerService;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.nevex.investing.dataloader.loader.DataLoaderOrder.TICKER_HISTORICAL_FUNDAMENTALS_LOADER;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class TickerHistoricalFundamentalsLoader extends DataLoaderWorker {

    private final UsFundamentalsApiClient apiClient;
    private final TickerToCikRepository tickerToCikRepository;
    private final TickerService tickerService;
    private final TickerFundamentalsAdminService tickerFundamentalsAdminService;

    public TickerHistoricalFundamentalsLoader(DataLoaderService dataLoaderService,
                                              TickerToCikRepository tickerToCikRepository,
                                              TickerFundamentalsAdminService tickerFundamentalsAdminService,
                                              TickerService tickerService,
                                              UsFundamentalsApiClient apiClient) {
        super(dataLoaderService);
        if ( apiClient == null ) { throw new IllegalArgumentException("Provided apiClient is null"); }
        if ( tickerToCikRepository == null ) { throw new IllegalArgumentException("Provided tickerToCikRepository is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        if ( tickerFundamentalsAdminService == null ) { throw new IllegalArgumentException("Provided tickerFundamentalsAdminService is null"); }
        this.apiClient = apiClient;
        this.tickerToCikRepository = tickerToCikRepository;
        this.tickerService = tickerService;
        this.tickerFundamentalsAdminService = tickerFundamentalsAdminService;
    }

    @Override
    public int getOrderNumber() {
        return TICKER_HISTORICAL_FUNDAMENTALS_LOADER;
    }

    @Override
    public String getName() {
        return "ticker-historical-fundamentals-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        int amountProcessed = super.processAllPagesInIterable(tickerToCikRepository::findAll, this::processCik, 500);
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

        Long cik = parseRawCik(tickerToCikEntity.getCik());
        if ( cik == null ) {
            return;
        }

        try {
            UsFundamentalsResponse fundamentalsResponse = apiClient.getAllFundamentalsForCik(cik);
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

    private Long parseRawCik(String cik) {
        String strippedCik = StringUtils.stripStart(cik, "0");
        try {
            return Long.valueOf(strippedCik);
        } catch (Exception e) {
            saveExceptionToDatabase("Could not convert raw cik ["+cik+"] into a long number. Reason: "+e.getMessage());
        }
        return null;
    }
}
