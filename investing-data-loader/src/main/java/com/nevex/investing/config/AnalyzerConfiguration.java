package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
import com.nevex.investing.analyze.DailyStockPriceAnalyzer;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
@Configuration
@ConditionalOnProperty(value = PropertyNames.NEVEX_INVESTING+".analyzers.configuration-enabled", havingValue = "true")
public class AnalyzerConfiguration {

    @Autowired
    private StockPriceAdminService stockPriceAdminService;
    @Autowired
    private TickerService tickerService;

    @Bean
    @ConditionalOnProperty(value = PropertyNames.NEVEX_INVESTING+".analyzers.daily-stock-price-analyzer.enabled", havingValue = "true")
    DailyStockPriceAnalyzer dailyStockPriceAnalyzer() {
        return new DailyStockPriceAnalyzer(stockPriceAdminService, tickerService);
    }

}
