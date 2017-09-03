package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.database.DataLoaderErrorsRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.dataloader.DataLoaderService;
import com.nevex.roboinvesting.dataloader.loader.EdgarTickerToCIKLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Validated
@Configuration
//@ConfigurationProperties(prefix = EdgarTickerToCikLoaderConfiguration.CONFIGURATION_PREFIX_KEY)
//@ConditionalOnProperty(name = EdgarTickerToCikLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class EdgarTickerToCikLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgarTickerToCikLoaderConfiguration.class);

    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private DataLoaderService dataLoaderService;

    @Bean
    EdgarTickerToCIKLoader edgarTickerToCIKLoader() {
        return new EdgarTickerToCIKLoader(dataLoaderService, tickersRepository);
    }
}
