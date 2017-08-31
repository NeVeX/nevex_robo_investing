package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import com.nevex.roboinvesting.database.DataLoaderErrorsRepository;
import com.nevex.roboinvesting.database.DataLoaderRunsRepository;
import com.nevex.roboinvesting.database.StockExchangesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.dataloader.DataLoaderManager;
import com.nevex.roboinvesting.dataloader.DataLoaderWorker;
import com.nevex.roboinvesting.dataloader.ReferenceDataLoader;
import com.nevex.roboinvesting.dataloader.TickerCacheLoader;
import com.nevex.roboinvesting.service.StockPriceAdminService;
import com.nevex.roboinvesting.service.TickerAdminService;
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
    private TiingoApiClient tiingoApiClient;
    @Autowired
    private StockPriceAdminService stockPriceAdminService;
    @Autowired
    private TickerAdminService tickerAdminService;

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
    DataLoaderManager dataLoaderManager() {
        return new DataLoaderManager(dataLoaderRunsRepository);
    }

    @Bean
    ReferenceDataLoader referenceDataLoader() {
        return new ReferenceDataLoader(stockExchangesRepository, dataLoaderErrorsRepository);
    }

    @Bean
    TickerCacheLoader tickerCacheLoader() { return new TickerCacheLoader(tickerAdminService, dataLoaderErrorsRepository); }
}
