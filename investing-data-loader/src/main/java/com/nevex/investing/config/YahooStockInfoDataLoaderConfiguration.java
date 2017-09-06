package com.nevex.investing.config;

import com.nevex.investing.api.yahoo.YahooApiClient;
import com.nevex.investing.database.StockExchangesRepository;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.TickerSymbolLoader;
import com.nevex.investing.dataloader.loader.YahooStockInfoLoader;
import com.nevex.investing.service.TickerAdminService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.YahooStockInfoService;
import com.nevex.investing.service.model.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.nevex.investing.PropertyNames.NEVEX_INVESTING;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = YahooStockInfoDataLoaderConfiguration.CONFIGURATION_PREFIX_KEY)
@ConditionalOnProperty(name = YahooStockInfoDataLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class YahooStockInfoDataLoaderConfiguration {

    static final String CONFIGURATION_PREFIX_KEY = NEVEX_INVESTING + ".yahoo-stock-info-data-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Valid
    @NotNull
    private Boolean forceStartOnAppStartup;

    @Autowired
    private DataLoaderService dataLoaderService;
    @Autowired
    private YahooApiClient yahooApiClient;
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private YahooStockInfoService yahooStockInfoService;
    @Autowired
    private TickerService tickerService;

    @Bean
    YahooStockInfoLoader yahooStockInfoLoader() {
        return new YahooStockInfoLoader(dataLoaderService, tickersRepository, yahooApiClient, yahooStockInfoService, tickerService, forceStartOnAppStartup);
    }

    public void setForceStartOnAppStartup(Boolean forceStartOnAppStartup) {
        this.forceStartOnAppStartup = forceStartOnAppStartup;
    }
}
