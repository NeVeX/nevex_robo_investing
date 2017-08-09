package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.database.TickersRepository;
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

    @Bean
    TickerSymbolService tickerSymbolService() {
        return new TickerSymbolService(tickersRepository);
    }

}
