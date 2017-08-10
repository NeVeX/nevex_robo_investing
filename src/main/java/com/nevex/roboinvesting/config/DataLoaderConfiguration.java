package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.dataloader.CurrentStockPriceLoader;
import com.nevex.roboinvesting.dataloader.DataLoaderManager;
import com.nevex.roboinvesting.dataloader.DataLoaderWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
@Configuration
public class DataLoaderConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    void init() {
        try {
            Map<String, DataLoaderWorker> beans = applicationContext.getBeansOfType(DataLoaderWorker.class);
            beans.values().stream().forEach(w -> dataLoaderManager().addDataWorker(w));
        } catch (Exception e ) {
            throw new IllegalStateException("Could not find any beans of type ["+DataLoaderWorker.class+"]", e);
        }
    }

    @Bean
    DataLoaderManager dataLoaderManager() {
        return new DataLoaderManager();
    }

    @Bean
    CurrentStockPriceLoader currentStockPriceLoader() {
        return new CurrentStockPriceLoader();
    }

}
