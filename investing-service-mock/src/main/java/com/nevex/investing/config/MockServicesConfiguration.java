package com.nevex.investing.config;

import com.nevex.investing.database.StockPricesHistoricalRepository;
import com.nevex.investing.database.StockPricesRepository;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.service.MockStockPriceService;
import com.nevex.investing.service.MockTickerService;
import com.nevex.investing.service.StockPriceService;
import com.nevex.investing.service.TickerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
