package com.nevex.investing.dataloader.loader;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.yahoo.YahooApiClient;
import com.nevex.investing.api.yahoo.model.YahooStockInfo;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.service.ServiceException;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.YahooStockInfoService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/5/2017.
 */
public class YahooStockInfoLoader extends DataLoaderSchedulingSingleWorker {

    private final TickersRepository tickersRepository;
    private final YahooApiClient yahooApiClient;
    private final YahooStockInfoService yahooStockInfoService;
    private final TickerService tickerService;

    public YahooStockInfoLoader(DataLoaderService dataLoaderService,
                                TickersRepository tickersRepository,
                                YahooApiClient yahooApiClient,
                                YahooStockInfoService yahooStockInfoService,
                                TickerService tickerService,
                                DataLoaderProperties.YahooStockInfoDataLoaderProperties properties) {
        super(dataLoaderService, properties.getForceStartOnAppStartup());
        if ( tickersRepository == null ) { throw new IllegalArgumentException("Provided tickersRepository is null"); }
        if ( yahooApiClient == null ) { throw new IllegalArgumentException("Provided yahooApiClient is null"); }
        if ( yahooStockInfoService == null ) { throw new IllegalArgumentException("Provided yahooStockInfoService is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        this.tickersRepository = tickersRepository;
        this.yahooApiClient = yahooApiClient;
        this.yahooStockInfoService = yahooStockInfoService;
        this.tickerService = tickerService;
    }

    @Override
    public int getOrderNumber() {
        return DataLoaderOrder.YAHOO_STOCK_INFO_LOADER;
    }

    @Override
    public String getName() {
        return "yahoo-stock-info-loader";
    }

    @Scheduled(initialDelay = 86400000L, fixedDelay = 86400000L) // Every 24 hours
    @Override
    void onScheduleStartInvoked() throws DataLoaderWorkerException {
        super.scheduleStart();
    }

    @Override
    DataLoaderWorkerResult onWorkerStartedAtAppStartup() throws DataLoaderWorkerException {
        return DataLoaderWorkerResult.nothingDone();
    }

    @Override
    DataLoaderWorkerResult doScheduledWork() throws DataLoaderWorkerException {
        int tickersProcessed = super.processAllPagesInBulkForRepo(tickersRepository, this::processEntities, TimeUnit.SECONDS.toMillis(10), 100);
        return new DataLoaderWorkerResult(tickersProcessed);
    }

    private void processEntities(List<TickerEntity> tickerEntities) {

        List<String> symbols = tickerEntities.stream().map(TickerEntity::getSymbol).collect(Collectors.toList());

        try {
            List<YahooStockInfo> stockInfos = yahooApiClient.getYahooStockInfo(symbols);

            if ( stockInfos != null && !stockInfos.isEmpty()) {
                for ( YahooStockInfo stockInfo : stockInfos) {

                    Optional<Integer> tickerIdOpt = tickerService.tryGetIdForSymbol(stockInfo.getSymbol());
                    if ( tickerIdOpt.isPresent()) {
                        try {
                            yahooStockInfoService.saveYahooStockInfo(tickerIdOpt.get(), stockInfo);
                        } catch (ServiceException e) {
                            saveExceptionToDatabase("A exception occurred trying to save yahoo stock info for ticker ["+tickerIdOpt.get()+"]. Reason: "+e.getMessage());
                        }
                    } else {
                        saveExceptionToDatabase("Received stock info from yahoo for symbol ["+stockInfo.getSymbol()+"] but could not map to a ticker id");
                    }
                }
            } else {
                saveExceptionToDatabase("No stock data info was returned from yahoo for stock symbols ["+symbols+"]");
            }

        } catch (ApiException apiEx) {
            saveExceptionToDatabase("An exception occurred getting yahoo stock info for ["+symbols.size()+"] symbols. Reason: "+apiEx.getMessage());
        }

    }


}
