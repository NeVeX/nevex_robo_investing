package com.nevex.investing.config;

import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.StockExchangesRepository;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.dataloader.DataLoaderService;
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
@ConditionalOnProperty(name = TickerDataLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class TickerDataLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerDataLoaderConfiguration.class);

    private static final String CONFIGURATION_PREFIX_KEY = DataLoaderProperties.PREFIX + ".ticker-data-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

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

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    TickerSymbolLoader tickerSymbolLoader() {
        TickerSymbolLoader loader = new TickerSymbolLoader(tickerAdminService, stockExchangesRepository, tickersRepository, dataLoaderService);
        loader.addTickerFileToLoad(StockExchange.Nasdaq, dataLoaderProperties.getTickerDataLoader().getNasdaqFile());
        loader.addTickerFileToLoad(StockExchange.Nyse, dataLoaderProperties.getTickerDataLoader().getNyseFile());
        return loader;
    }
}
