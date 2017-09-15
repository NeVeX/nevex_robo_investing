package com.nevex.investing.config;

import com.nevex.investing.api.tiingo.TiingoApiClient;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.HistoricalStockPriceLoader;
import com.nevex.investing.service.StockPriceAdminService;
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
@ConditionalOnProperty(name = StockPricesHistoricalLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class StockPricesHistoricalLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockPricesHistoricalLoaderConfiguration.class);

    private static final String CONFIGURATION_PREFIX_KEY = DataLoaderProperties.PREFIX + ".stock-historical-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Autowired
    private DataLoaderService dataLoaderService;
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private TiingoApiClient tiingoApiClient;
    @Autowired
    private StockPriceAdminService stockPriceAdminService;
    @Autowired
    private DataLoaderProperties dataLoaderProperties;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    HistoricalStockPriceLoader historicalStockPriceLoader() {
        return new HistoricalStockPriceLoader(tickersRepository, tiingoApiClient, stockPriceAdminService, dataLoaderService, dataLoaderProperties.getStockHistoricalLoader());
    }

}
