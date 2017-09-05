package com.nevex.investing.config;

import com.nevex.investing.database.TickerToCikRepository;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.TickerHistoricalFundamentalsLoader;
import com.nevex.investing.api.usfundamentals.UsFundamentalsApiClient;
import com.nevex.investing.service.TickerFundamentalsService;
import com.nevex.investing.service.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.nevex.investing.PropertyNames.NEVEX_INVESTING;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
@Configuration
@ConfigurationProperties(prefix = TickerHistoricalFundamentalsLoaderConfiguration.CONFIGURATION_PREFIX_KEY)
@ConditionalOnProperty(name = TickerHistoricalFundamentalsLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
public class TickerHistoricalFundamentalsLoaderConfiguration {

    static final String CONFIGURATION_PREFIX_KEY = NEVEX_INVESTING + ".ticker-historical-fundamentals-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Autowired
    private DataLoaderService dataLoaderService;
    @Autowired
    private TickerService tickerService;
    @Autowired
    private TickerToCikRepository tickerToCikRepository;
    @Autowired
    private TickerFundamentalsService tickerFundamentalsService;
    @Autowired
    private UsFundamentalsApiClient usFundamentalsApiClient;

    @Bean
    TickerHistoricalFundamentalsLoader tickerHistoricalFundamentalsLoader() {
        return new TickerHistoricalFundamentalsLoader(dataLoaderService, tickerToCikRepository, tickerFundamentalsService,
                tickerService, usFundamentalsApiClient);
    }
}
