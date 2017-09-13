package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
import com.nevex.investing.processor.DailyStockPriceChangeProcessor;
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
@ConditionalOnProperty(value = PropertyNames.NEVEX_INVESTING+".processors.configuration-enabled", havingValue = "true")
class AnalyzerConfiguration {

    @Autowired
    private StockPriceAdminService stockPriceAdminService;
    @Autowired
    private TickerService tickerService;

    @Bean
    @ConditionalOnProperty(value = PropertyNames.NEVEX_INVESTING+".processors.daily-stock-price-processor.enabled", havingValue = "true")
    DailyStockPriceChangeProcessor dailyStockPriceProcessor() {
        return new DailyStockPriceChangeProcessor(stockPriceAdminService, tickerService);
    }

}
