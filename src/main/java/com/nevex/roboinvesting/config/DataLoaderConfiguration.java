package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import com.nevex.roboinvesting.database.StockExchangesRepository;
import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.dataloader.CurrentStockPriceLoader;
import com.nevex.roboinvesting.dataloader.DataLoaderManager;
import com.nevex.roboinvesting.dataloader.DataLoaderWorker;
import com.nevex.roboinvesting.dataloader.ReferenceDataLoader;
import com.nevex.roboinvesting.service.StockPriceAdminService;
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
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private StockExchangesRepository stockExchangesRepository;
    @Autowired
    private TiingoApiClient tiingoApiClient;
    @Autowired
    private StockPriceAdminService stockPriceAdminService;

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
        return new CurrentStockPriceLoader(tickersRepository, tiingoApiClient, stockPriceAdminService);
    }

    @Bean
    ReferenceDataLoader referenceDataLoader() {
        return new ReferenceDataLoader(stockExchangesRepository);
    }

}
