package com.nevex.investing.config;

import com.nevex.investing.analyzer.AnalyzerService;
import com.nevex.investing.analyzer.AllAnalyzersSummaryAnalyzer;
import com.nevex.investing.analyzer.StockPriceChangeAnalyzer;
import com.nevex.investing.config.property.AnalyzerProperties;
import com.nevex.investing.database.AnalyzerWeightsRepository;
import com.nevex.investing.database.TickerAnalyzersRepository;
import com.nevex.investing.database.TickerAnalyzersSummaryRepository;
import com.nevex.investing.analyzer.StockFinancialsAnalyzer;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.AnalyzerEventDataLoader;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.StockPriceUpdatedEvent;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerAnalyzersAdminService;
import com.nevex.investing.service.TickerService;
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

import java.time.LocalDate;

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
    private TickerService tickerService;
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
    private AnalyzerProperties analyzerProperties;
    @Autowired
    private DataLoaderService dataLoaderService;

    @PostConstruct
    void init() throws ServiceException {
        LOGGER.info("{} is setup and active with properties: [{}]", this.getClass().getSimpleName(), analyzerProperties);
        analyzerService().refresh();
    }

    @Bean
    AnalyzerEventDataLoader analyzerEventDataLoader() {
        return new AnalyzerEventDataLoader(tickerService, dataLoaderService, analyzerProperties);
    }

    @Bean
    AnalyzerService analyzerService() {
        return new AnalyzerService(analyzerWeightsRepository);
    }

    @Bean
    TickerAnalyzersAdminService tickerAnalyzersAdminService() {
        return new TickerAnalyzersAdminService(tickerAnalyzersRepository, tickerAnalyzersSummaryRepository);
    }

    @Bean
    @ConditionalOnProperty(name = AnalyzerProperties.StockFinancialsAnalyzerProperties.ENABLED, havingValue = "true")
    StockFinancialsAnalyzer stockFinancialsAnalyzer() {
        return new StockFinancialsAnalyzer(yahooStockInfoService, tickerAnalyzersAdminService(), analyzerService());
    }

    @Bean
    @ConditionalOnProperty(value = AnalyzerProperties.StockPriceChangeAnalyzerProperties.ENABLED, havingValue = "true")
    StockPriceChangeAnalyzer stockPriceChangeAnalyzer() {
        return new StockPriceChangeAnalyzer(stockPriceAdminService, analyzerService(), tickerAnalyzersAdminService());
    }

    @Bean
    @ConditionalOnProperty(value = AnalyzerProperties.AllAnalyzersSummaryAnalyzerProperties.ENABLED, havingValue = "true")
    AllAnalyzersSummaryAnalyzer allAnalyzersSummaryAnalyzer() {
        return new AllAnalyzersSummaryAnalyzer(tickerAnalyzersAdminService(), analyzerService());
    }

}
