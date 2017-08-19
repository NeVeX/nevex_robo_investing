package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.service.StockPriceAdminService;
import com.nevex.roboinvesting.service.TickerAdminService;
import com.nevex.roboinvesting.service.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Configuration
public class DataLoaderServicesConfiguration {

    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private StockPricesRepository stockPricesRepository;
    @Autowired
    private TickerService tickerService;
    @Autowired
    private StockPricesHistoricalRepository stockPricesHistoricalRepository;

    @Bean
    StockPriceAdminService stockPriceAdminService() {
        return new StockPriceAdminService(tickerService, stockPricesRepository, stockPricesHistoricalRepository);
    }

    @Bean
    TickerAdminService tickerAdminService() {
        return new TickerAdminService(tickersRepository);
    }

}
