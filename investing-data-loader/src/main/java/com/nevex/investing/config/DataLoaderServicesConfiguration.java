package com.nevex.investing.config;

import com.nevex.investing.database.*;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerAdminService;
import com.nevex.investing.service.TickerFundamentalsService;
import com.nevex.investing.service.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.Basic;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
@Configuration
public class DataLoaderServicesConfiguration {

    @Autowired
    private TickerFundamentalsRepository tickerFundamentalsRepository;
    @Autowired
    private TickerFundamentalsSyncRepository tickerFundamentalsSyncRepository;
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

    @Bean
    TickerFundamentalsService tickerFundamentalsService() {
        return new TickerFundamentalsService(tickerFundamentalsRepository, tickerFundamentalsSyncRepository);
    }

}
