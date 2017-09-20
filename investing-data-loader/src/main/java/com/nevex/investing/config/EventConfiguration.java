package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
import com.nevex.investing.config.property.EventProperties;
import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.Event;
import com.nevex.investing.processor.StockPriceChangeSummaryProcessor;
import com.nevex.investing.service.StockPriceAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
@Configuration
@ConditionalOnProperty(value = EventProperties.PREFIX+".configuration-enabled", havingValue = "true")
class EventConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConfiguration.class);

    @Autowired
    private EventProperties eventProperties;
    @Autowired
    private StockPriceAdminService stockPriceAdminService;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    EventManager eventManager(
            @Autowired(required = false) Set<EventConsumer<? extends Event>> eventConsumers) {
        return new EventManager(eventConsumers, eventProperties.getEventQueueSize());
    }

    @Bean
    StockPriceChangeSummaryProcessor stockPriceChangeSummaryProcessor() {
        return new StockPriceChangeSummaryProcessor(stockPriceAdminService);
    }

}
