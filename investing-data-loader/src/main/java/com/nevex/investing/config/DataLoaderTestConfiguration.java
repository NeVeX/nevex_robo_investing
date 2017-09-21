package com.nevex.investing.config;

import com.nevex.investing.config.property.ApplicationProperties;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.StockFinancialsUpdatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

import static com.nevex.investing.config.TestingConfiguration.TESTING_PREFIX;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = TESTING_PREFIX)
@ConditionalOnProperty(name = ApplicationProperties.Testing.ENABLED, havingValue = "true")
public class DataLoaderTestConfiguration {

    @Autowired
    private EventManager eventManager;

    @PostConstruct
    void init() {
        // send fake events

        eventManager.sendEvent(new StockFinancialsUpdatedEvent(6379));

    }

}
