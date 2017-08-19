package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.PropertyNames;
import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.service.MockStockPriceService;
import com.nevex.roboinvesting.service.MockTickerService;
import com.nevex.roboinvesting.service.StockPriceService;
import com.nevex.roboinvesting.service.TickerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Configuration
@Profile("mock")
public class MockServicesConfiguration {

    private final static Logger LOGGER = LoggerFactory.getLogger(MockServicesConfiguration.class);

    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private StockPricesRepository stockPricesRepository;
    @Autowired
    private StockPricesHistoricalRepository stockPricesHistoricalRepository;

    @PostConstruct
    void init() {
        LOGGER.warn("\n\n\n*********** Mock profile enabled \n\n\n");
    }

    @Bean
    TickerService mockTickerService() {
        return new MockTickerService(tickersRepository);
    }

    @Bean
    StockPriceService mockStockPriceService() {
        return new MockStockPriceService(mockTickerService(), stockPricesRepository, stockPricesHistoricalRepository);
    }

}
