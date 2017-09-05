package com.nevex.investing.config;

import com.nevex.investing.database.*;
import com.nevex.investing.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
@Configuration
public class DataLoaderServicesConfiguration {

    @Autowired
    private TickerFundamentalsRepository tickerFundamentalsRepository;
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private StockPricesRepository stockPricesRepository;
    @Autowired
    private TickerService tickerService;
    @Autowired
    private StockPricesHistoricalRepository stockPricesHistoricalRepository;
    @Autowired
    private TickerToCikRepository tickerToCikRepository;
    @Autowired
    private StockExchangesRepository stockExchangesRepository;


    @Bean
    StockPriceAdminService stockPriceAdminService() {
        return new StockPriceAdminService(tickerService, stockPricesRepository, stockPricesHistoricalRepository);
    }

    @Bean
    TickerAdminService tickerAdminService() {
        return new TickerAdminService(tickersRepository);
    }

    @Bean
    TickerFundamentalsAdminService tickerFundamentalsService() {
        return new TickerFundamentalsAdminService(tickerFundamentalsRepository);
    }

    @Bean
    EdgarAdminService edgarAdminService() {
        return new EdgarAdminService(tickerToCikRepository);
    }

    @Bean
    StockExchangeAdminService stockExchangeAdminService() {
        return new StockExchangeAdminService(stockExchangesRepository);
    }

}
