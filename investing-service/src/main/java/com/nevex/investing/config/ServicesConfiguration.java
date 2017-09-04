package com.nevex.investing.config;

import com.nevex.investing.database.StockPricesHistoricalRepository;
import com.nevex.investing.database.StockPricesRepository;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.service.StockPriceService;
import com.nevex.investing.service.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Configuration
@Profile("!mock")
public class ServicesConfiguration {

    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private StockPricesRepository stockPricesRepository;
    @Autowired
    private StockPricesHistoricalRepository stockPricesHistoricalRepository;

    @Bean
    TickerService tickerService() {
        return new TickerService(tickersRepository);
    }

    @Bean
    StockPriceService stockPriceService() {
        return new StockPriceService(tickerService(), stockPricesRepository, stockPricesHistoricalRepository);
    }

}