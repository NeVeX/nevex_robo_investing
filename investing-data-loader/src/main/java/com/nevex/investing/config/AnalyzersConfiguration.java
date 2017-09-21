package com.nevex.investing.config;

import com.nevex.investing.config.property.AnalyzerProperties;
import com.nevex.investing.config.property.ApplicationProperties;
import com.nevex.investing.database.TickerAnalyzersRepository;
import com.nevex.investing.processor.StockFinancialsAnalyzer;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.YahooStockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import static com.nevex.investing.config.TestingConfiguration.TESTING_PREFIX;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = TESTING_PREFIX)
@ConditionalOnProperty(name = AnalyzerProperties.CONFIGURATION_ENABLED, havingValue = "true")
public class AnalyzersConfiguration {

    @Autowired
    private TickerAnalyzersRepository tickerAnalyzersRepository;
    @Autowired
    private YahooStockInfoService yahooStockInfoService;

    @Bean
    TickerAnalyzersService tickerAnalyzersService() {
        return new TickerAnalyzersService(tickerAnalyzersRepository);
    }

    @Bean
    @ConditionalOnProperty(name = AnalyzerProperties.StockFinancialsAnalyzerProperties.ENABLED, havingValue = "true")
    StockFinancialsAnalyzer stockFinancialsAnalyzer() {
        return new StockFinancialsAnalyzer(yahooStockInfoService, tickerAnalyzersService());
    }
}
