package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
import com.nevex.investing.event.DailyStockPriceEventProcessor;
import com.nevex.investing.event.type.DailyStockPriceUpdateConsumer;
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
@ConditionalOnProperty(value = PropertyNames.NEVEX_INVESTING+".events.configuration-enabled", havingValue = "true")
class EventConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConfiguration.class);

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    DailyStockPriceEventProcessor dailyStockPriceEventProcessor(
            @Autowired(required = false) Set<DailyStockPriceUpdateConsumer> dailyStockPriceUpdateConsumers) {
        return new DailyStockPriceEventProcessor(dailyStockPriceUpdateConsumers);
    }

}
