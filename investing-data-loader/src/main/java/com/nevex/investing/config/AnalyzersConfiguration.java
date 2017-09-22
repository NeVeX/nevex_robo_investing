package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
import com.nevex.investing.analyzer.AnalyzerService;
import com.nevex.investing.analyzer.StockFinancialsSummaryAnalyzer;
import com.nevex.investing.analyzer.StockPriceChangeAnalyzer;
import com.nevex.investing.config.property.AnalyzerProperties;
import com.nevex.investing.database.AnalyzerWeightsRepository;
import com.nevex.investing.database.TickerAnalyzersRepository;
import com.nevex.investing.database.TickerAnalyzersSummaryRepository;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.analyzer.StockFinancialsAnalyzer;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.YahooStockInfoService;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

import static com.nevex.investing.config.TestingConfiguration.TESTING_PREFIX;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = TESTING_PREFIX)
@ConditionalOnProperty(name = AnalyzerProperties.CONFIGURATION_ENABLED, havingValue = "true")
class AnalyzersConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzersConfiguration.class);
    @Autowired
    private AnalyzerWeightsRepository analyzerWeightsRepository;
    @Autowired
    private StockPriceAdminService stockPriceAdminService;
    @Autowired
    private TickerAnalyzersRepository tickerAnalyzersRepository;
    @Autowired
    private TickerAnalyzersSummaryRepository tickerAnalyzersSummaryRepository;
    @Autowired
    private YahooStockInfoService yahooStockInfoService;
    @Autowired
    private EventManager eventManager;
    @Autowired
    private AnalyzerProperties analyzerProperties;

    @PostConstruct
    void init() throws ServiceException {
        LOGGER.info("{} is setup and active with properties: [{}]", this.getClass().getSimpleName(), analyzerProperties);
        if ( stockFinancialsAnalyzer() != null ) {
            stockFinancialsAnalyzer().setEventManager(eventManager);
        }
        analyzerService().refresh();
    }

    @Bean
    AnalyzerService analyzerService() {
        return new AnalyzerService(analyzerWeightsRepository);
    }

    @Bean
    TickerAnalyzersService tickerAnalyzersService() {
        return new TickerAnalyzersService(tickerAnalyzersRepository, tickerAnalyzersSummaryRepository);
    }

    @Bean
    @ConditionalOnProperty(name = AnalyzerProperties.StockFinancialsAnalyzerProperties.ENABLED, havingValue = "true")
    StockFinancialsAnalyzer stockFinancialsAnalyzer() {
        return new StockFinancialsAnalyzer(yahooStockInfoService, tickerAnalyzersService(), analyzerService());
    }

    @Bean
    @ConditionalOnProperty(value = AnalyzerProperties.StockPriceChangeAnalyzerProperties.ENABLED, havingValue = "true")
    StockPriceChangeAnalyzer stockPriceChangeAnalyzer() {
        return new StockPriceChangeAnalyzer(stockPriceAdminService);
    }

    @Bean
    @ConditionalOnProperty(value = AnalyzerProperties.StockFinancialsSummaryAnalyzerProperties.ENABLED, havingValue = "true")
    StockFinancialsSummaryAnalyzer stockFinancialsSummaryAnalyzer() {
        return new StockFinancialsSummaryAnalyzer(tickerAnalyzersService(), analyzerService());
    }



}
