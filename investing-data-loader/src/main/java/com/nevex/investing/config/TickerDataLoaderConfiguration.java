package com.nevex.investing.config;

import com.nevex.investing.api.iextrading.IEXTradingClient;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.StockExchangesRepository;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.TickerSymbolChecker;
import com.nevex.investing.dataloader.loader.TickerSymbolLoader;
import com.nevex.investing.service.TickerAdminService;
import com.nevex.investing.service.model.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Validated
@Configuration
class TickerDataLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerDataLoaderConfiguration.class);

    private static final String LOADER_PREFIX_KEY = DataLoaderProperties.PREFIX + ".ticker-data-loader";
    private static final String CHECKER_PREFIX_KEY = DataLoaderProperties.PREFIX + ".ticker-data-checker";
    private static final String LOADER_ENABLED_KEY = LOADER_PREFIX_KEY + ".enabled";
    private static final String CHECKER_ENABLED_KEY = CHECKER_PREFIX_KEY + ".enabled";

    @Autowired
    private DataLoaderService dataLoaderService;
    @Autowired
    private StockExchangesRepository stockExchangesRepository;
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private TickerAdminService tickerAdminService;
    @Autowired
    private DataLoaderProperties dataLoaderProperties;
    @Autowired
    private IEXTradingClient iexTradingClient;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    @ConditionalOnProperty(name = LOADER_ENABLED_KEY, havingValue = "true")
    TickerSymbolLoader tickerSymbolLoader() {
        TickerSymbolLoader loader = new TickerSymbolLoader(tickerAdminService, stockExchangesRepository, dataLoaderService);
        loader.addTickerFileToLoad(StockExchange.Nasdaq, dataLoaderProperties.getTickerDataLoader().getNasdaqFile());
        loader.addTickerFileToLoad(StockExchange.Nyse, dataLoaderProperties.getTickerDataLoader().getNyseFile());
        return loader;
    }

    @Bean
    @ConditionalOnProperty(name = CHECKER_ENABLED_KEY, havingValue = "true")
    TickerSymbolChecker tickerSymbolChecker() {
        return new TickerSymbolChecker(tickerAdminService, dataLoaderService, iexTradingClient);
    }
}
