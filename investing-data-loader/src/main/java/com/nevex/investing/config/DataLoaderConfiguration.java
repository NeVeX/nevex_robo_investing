package com.nevex.investing.config;

import com.nevex.investing.database.DataLoaderErrorsRepository;
import com.nevex.investing.database.DataLoaderRunsRepository;
import com.nevex.investing.database.StockExchangesRepository;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.DataLoaderStarter;
import com.nevex.investing.dataloader.loader.DataLoaderWorker;
import com.nevex.investing.dataloader.loader.ReferenceDataLoader;
import com.nevex.investing.dataloader.loader.TickerCacheLoader;
import com.nevex.investing.service.StockExchangeAdminService;
import com.nevex.investing.service.TickerAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    private DataLoaderRunsRepository dataLoaderRunsRepository;
    @Autowired
    private DataLoaderErrorsRepository dataLoaderErrorsRepository;
    @Autowired
    private TickerAdminService tickerAdminService;
    @Autowired
    private StockExchangeAdminService stockExchangeAdminService;

    @Valid
    @Min(value = 0, message = "Invalid wait time in ms specified")
    private Long waitTimeBetweenTickersMs;

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
    DataLoaderService dataLoaderService() {
        return new DataLoaderService(dataLoaderRunsRepository, dataLoaderErrorsRepository);
    }

    @Bean
    DataLoaderStarter dataLoaderManager() {
        return new DataLoaderStarter();
    }

    @Bean
    ReferenceDataLoader referenceDataLoader() {
        return new ReferenceDataLoader(stockExchangeAdminService, dataLoaderService());
    }

    @Bean
    TickerCacheLoader tickerCacheLoader() { return new TickerCacheLoader(tickerAdminService, dataLoaderService()); }
}
