package com.nevex.investing.config;

import com.nevex.investing.database.*;
import com.nevex.investing.service.AnalyzerPricePerformanceService;
import com.nevex.investing.service.StockPriceService;
import com.nevex.investing.service.TickerAnalyzersService;
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
class ServicesConfiguration {

    @Autowired
    private AnalyzerPricePerformanceRepository analyzerPricePerformanceRepository;
    @Autowired
    private TickerAnalyzersSummaryRepository tickerAnalyzersSummaryRepository;
    @Autowired
    private TickerAnalyzersRepository tickerAnalyzersRepository;
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private StockPricesRepository stockPricesRepository;
    @Autowired
    private StockPricesHistoricalRepository stockPricesHistoricalRepository;

    @Bean
    TickerAnalyzersService tickerAnalyzersService() {
        return new TickerAnalyzersService(tickerAnalyzersRepository, tickerAnalyzersSummaryRepository);
    }

    @Bean
    TickerService tickerService() {
        return new TickerService(tickersRepository);
    }

    @Bean
    StockPriceService stockPriceService() {
        return new StockPriceService(tickerService(), stockPricesRepository, stockPricesHistoricalRepository);
    }

    @Bean
    AnalyzerPricePerformanceService analyzerPricePerformanceService() {
        return new AnalyzerPricePerformanceService(analyzerPricePerformanceRepository, tickerService());
    }


}
