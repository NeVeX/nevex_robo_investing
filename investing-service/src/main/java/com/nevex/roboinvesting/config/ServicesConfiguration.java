package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.service.StockPriceService;
import com.nevex.roboinvesting.service.TickerService;
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
