package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.service.StockPriceAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Configuration
public class DataLoaderServicesConfiguration {

    @Autowired
    private StockPricesRepository stockPricesRepository;
    @Autowired
    private StockPricesHistoricalRepository stockPricesHistoricalRepository;

    @Bean
    StockPriceAdminService stockPriceAdminService() {
        return new StockPriceAdminService(stockPricesRepository, stockPricesHistoricalRepository);
    }

}
