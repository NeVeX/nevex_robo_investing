package com.nevex.investing.config;

import com.nevex.investing.database.StockPricesHistoricalRepository;
import com.nevex.investing.database.StockPricesRepository;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerAdminService;
import com.nevex.investing.service.TickerService;
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
