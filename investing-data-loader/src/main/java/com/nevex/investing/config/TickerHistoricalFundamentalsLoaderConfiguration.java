package com.nevex.investing.config;

import com.nevex.investing.api.usfundamentals.UsFundamentalsApiClient;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.TickerToCikRepository;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.TickerHistoricalFundamentalsLoader;
import com.nevex.investing.service.TickerFundamentalsAdminService;
import com.nevex.investing.service.TickerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
@Configuration
@ConditionalOnProperty(name = TickerHistoricalFundamentalsLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class TickerHistoricalFundamentalsLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerHistoricalFundamentalsLoaderConfiguration.class);
    private static final String CONFIGURATION_PREFIX_KEY = DataLoaderProperties.PREFIX + ".ticker-historical-fundamentals-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Autowired
    private DataLoaderService dataLoaderService;
    @Autowired
    private TickerService tickerService;
    @Autowired
    private TickerToCikRepository tickerToCikRepository;
    @Autowired
    private TickerFundamentalsAdminService tickerFundamentalsAdminService;
    @Autowired
    private UsFundamentalsApiClient usFundamentalsApiClient;
    @Autowired
    private DataLoaderProperties dataLoaderProperties;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    TickerHistoricalFundamentalsLoader tickerHistoricalFundamentalsLoader() {
        return new TickerHistoricalFundamentalsLoader(dataLoaderService, tickerToCikRepository, tickerFundamentalsAdminService,
                tickerService, usFundamentalsApiClient, dataLoaderProperties.getTickerHistoricalFundamentalsLoader());
    }

}
