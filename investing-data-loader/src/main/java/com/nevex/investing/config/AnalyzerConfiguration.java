package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
import com.nevex.investing.processor.StockPriceChangeSummaryProcessor;
import com.nevex.investing.service.StockPriceAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
@Configuration
@ConditionalOnProperty(value = PropertyNames.NEVEX_INVESTING+".processors.configuration-enabled", havingValue = "true")
class AnalyzerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzerConfiguration.class);

    @Autowired
    private StockPriceAdminService stockPriceAdminService;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The [{}] has been activated", this.getClass().getSimpleName());
    }

    @Bean
    @ConditionalOnProperty(value = PropertyNames.NEVEX_INVESTING+".processors.daily-stock-price-processor.enabled", havingValue = "true")
    StockPriceChangeSummaryProcessor dailyStockPriceProcessor() {
        return new StockPriceChangeSummaryProcessor(stockPriceAdminService);
    }

}
