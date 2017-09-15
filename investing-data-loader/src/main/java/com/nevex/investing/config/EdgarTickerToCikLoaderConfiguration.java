package com.nevex.investing.config;

import com.nevex.investing.api.edgar.EdgarCikLookupClient;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.EdgarTickerToCIKLoader;
import com.nevex.investing.service.EdgarAdminService;
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
@ConditionalOnProperty(name = EdgarTickerToCikLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class EdgarTickerToCikLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgarTickerToCikLoaderConfiguration.class);

    private static final String CONFIGURATION_PREFIX_KEY = DataLoaderProperties.PREFIX + ".edgar-ticker-to-cik-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private DataLoaderService dataLoaderService;
    @Autowired
    private EdgarAdminService edgarAdminService;
    @Autowired
    private DataLoaderProperties dataLoaderProperties;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    EdgarTickerToCIKLoader edgarTickerToCIKLoader() {
        return new EdgarTickerToCIKLoader(dataLoaderService, tickersRepository, edgarCikLookupClient(), edgarAdminService, dataLoaderProperties.getEdgarTickerToCikLoader());
    }

    @Bean
    EdgarCikLookupClient edgarCikLookupClient() {
        return new EdgarCikLookupClient();
    }

}
