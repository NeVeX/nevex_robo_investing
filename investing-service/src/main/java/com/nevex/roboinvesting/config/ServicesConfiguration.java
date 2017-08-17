package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.service.StockPriceService;
import com.nevex.roboinvesting.service.TickerSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Configuration
public class ServicesConfiguration {

    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private StockPricesRepository stockPricesRepository;
    @Autowired
    private StockPricesHistoricalRepository stockPricesHistoricalRepository;

    @Bean
    TickerSymbolService tickerSymbolService() {
        return new TickerSymbolService(tickersRepository);
    }

    @Bean
    StockPriceService stockPriceService() {
        return new StockPriceService(stockPricesRepository, stockPricesHistoricalRepository);
    }

}
