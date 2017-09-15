package com.nevex.investing.config;

import com.nevex.investing.database.*;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.DataLoaderStarter;
import com.nevex.investing.dataloader.loader.DataLoaderWorker;
import com.nevex.investing.dataloader.loader.ReferenceDataLoader;
import com.nevex.investing.dataloader.loader.TickerCacheLoader;
import com.nevex.investing.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
@Configuration
class DefaultConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfiguration.class);

    @Autowired
    private DataLoaderRunsRepository dataLoaderRunsRepository;
    @Autowired
    private DataLoaderErrorsRepository dataLoaderErrorsRepository;
    @Autowired
    private TickerFundamentalsRepository tickerFundamentalsRepository;
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private StockPricesRepository stockPricesRepository;
    @Autowired
    private TickerService tickerService;
    @Autowired
    private StockPricesHistoricalRepository stockPricesHistoricalRepository;
    @Autowired
    private TickerToCikRepository tickerToCikRepository;
    @Autowired
    private StockExchangesRepository stockExchangesRepository;
    @Autowired
    private YahooStockInfoRepository yahooStockInfoRepository;
    @Autowired
    private StockPriceChangeTrackerRepository stockPriceChangeTrackerRepository;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    DataLoaderService dataLoaderService() {
        return new DataLoaderService(dataLoaderRunsRepository, dataLoaderErrorsRepository);
    }

    @Bean
    DataLoaderStarter dataLoaderStarter(@Autowired Set<DataLoaderWorker> workers) {
        DataLoaderStarter starter = new DataLoaderStarter();
        starter.addDataWorkers(workers);
        return starter;
    }

    @Bean
    ReferenceDataLoader referenceDataLoader() {
        return new ReferenceDataLoader(stockExchangeAdminService(), dataLoaderService());
    }

    @Bean
    TickerCacheLoader tickerCacheLoader() { return new TickerCacheLoader(tickerAdminService(), dataLoaderService()); }

    @Bean
    StockPriceAdminService stockPriceAdminService() {
        return new StockPriceAdminService(tickerService, stockPricesRepository, stockPricesHistoricalRepository, stockPriceChangeTrackerRepository);
    }

    @Bean
    TickerAdminService tickerAdminService() {
        return new TickerAdminService(tickersRepository);
    }

    @Bean
    TickerFundamentalsAdminService tickerFundamentalsService() {
        return new TickerFundamentalsAdminService(tickerFundamentalsRepository);
    }

    @Bean
    EdgarAdminService edgarAdminService() {
        return new EdgarAdminService(tickerToCikRepository);
    }

    @Bean
    StockExchangeAdminService stockExchangeAdminService() {
        return new StockExchangeAdminService(stockExchangesRepository);
    }

    @Bean
    YahooStockInfoService yahooStockInfoService() {
        return new YahooStockInfoService(yahooStockInfoRepository);
    }
}
