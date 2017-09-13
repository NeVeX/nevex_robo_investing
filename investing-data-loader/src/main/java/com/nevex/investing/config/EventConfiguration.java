package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
import com.nevex.investing.event.DailyStockPriceEventProcessor;
import com.nevex.investing.event.type.DailyStockPriceUpdateConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
@Configuration
@ConditionalOnProperty(value = PropertyNames.NEVEX_INVESTING+".events.configuration-enabled", havingValue = "true")
class EventConfiguration {

    @Bean
    DailyStockPriceEventProcessor dailyStockPriceEventProcessor(
            @Autowired(required = false) Set<DailyStockPriceUpdateConsumer> dailyStockPriceUpdateConsumers) {

//        Set<DailyStockPriceUpdateConsumer> dailyStockPriceProcessors = setOf(applicationContext, DailyStockPriceUpdateConsumer.class);

        return new DailyStockPriceEventProcessor(dailyStockPriceUpdateConsumers);
    }

//    private <T> Set<T> setOf(ApplicationContext applicationContext, Class<T> clazz) throws BeansException {
//        Map<String, T> mapOfClasses = applicationContext.getBeansOfType(clazz);
//        if ( mapOfClasses != null && !mapOfClasses.isEmpty()) {
//            return mapOfClasses.values().stream().filter(c -> c != null).collect(Collectors.toSet());
//        }
//        return new HashSet<>();
//    }
}
