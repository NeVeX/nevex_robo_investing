package com.nevex.investing.config;

import com.nevex.investing.api.yahoo.YahooApiClient;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.YahooStockInfoLoader;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.YahooStockInfoService;
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
@ConditionalOnProperty(name = YahooStockInfoDataLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class YahooStockInfoDataLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(YahooStockInfoDataLoaderConfiguration.class);
    private static final String CONFIGURATION_PREFIX_KEY = DataLoaderProperties.PREFIX + ".yahoo-stock-info-data-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Autowired
    private DataLoaderService dataLoaderService;
    @Autowired
    private YahooApiClient yahooApiClient;
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private YahooStockInfoService yahooStockInfoService;
    @Autowired
    private TickerService tickerService;
    @Autowired
    private DataLoaderProperties dataLoaderProperties;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    YahooStockInfoLoader yahooStockInfoLoader() {
        return new YahooStockInfoLoader(dataLoaderService, tickersRepository, yahooApiClient, yahooStockInfoService, tickerService, dataLoaderProperties.getYahooStockInfoDataLoader());
    }

}
