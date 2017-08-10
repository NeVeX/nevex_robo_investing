package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.dataloader.CurrentStockPriceLoader;
import com.nevex.roboinvesting.dataloader.HistoricalStockPriceLoader;
import com.nevex.roboinvesting.service.StockPriceAdminService;
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

import static com.nevex.roboinvesting.config.PropertyNames.ROBO_INVESTING;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = StockPricesLoaderConfiguration.CONFIGURATION_PREFIX_KEY)
@ConditionalOnProperty(name = StockPricesLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class StockPricesLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockPricesLoaderConfiguration.class);

    static final String CONFIGURATION_PREFIX_KEY = ROBO_INVESTING + ".stock-historical-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private StockPricesHistoricalRepository stockPricesHistoricalRepository;
    @Autowired
    private TiingoApiClient tiingoApiClient;
    @Autowired
    private StockPriceAdminService stockPriceAdminService;

    @Valid
    @NotNull(message = "The 'enabled' property cannot be null")
    private Boolean enabled;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The data load configuration has been activated. Configurations [{}]", this);
    }

    @Bean
    HistoricalStockPriceLoader historicalStockPriceLoader() {
        return new HistoricalStockPriceLoader(tickersRepository, tiingoApiClient, stockPriceAdminService);
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "StockHistoricalPriceLoaderConfiguration{" +
                "enabled=" + enabled +
                '}';
    }
}
