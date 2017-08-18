package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.database.StockExchangesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.dataloader.TickerSymbolLoader;
import com.nevex.roboinvesting.service.TickerService;
import com.nevex.roboinvesting.service.model.StockExchange;
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

import static com.nevex.roboinvesting.PropertyNames.NEVEX_INVESTING;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = TickerDataLoaderConfiguration.CONFIGURATION_PREFIX_KEY)
@ConditionalOnProperty(name = TickerDataLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class TickerDataLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerDataLoaderConfiguration.class);

    static final String CONFIGURATION_PREFIX_KEY = NEVEX_INVESTING + ".ticker-data-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Autowired
    private StockExchangesRepository stockExchangesRepository;
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private TickerService tickerService;

    @Valid
    @NotNull(message = "The 'enabled' property cannot be null")
    private Boolean enabled;
    private String nasdaqFile; // can be empty
    private String nyseFile; // can be empty

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The ticker data load configuration has been activated. Configurations [{}]", this);
    }

    @Bean
    TickerSymbolLoader tickerSymbolLoader() {
        TickerSymbolLoader loader = new TickerSymbolLoader(tickerService, stockExchangesRepository, tickersRepository);
        loader.addTickerFileToLoad(StockExchange.Nasdaq, nasdaqFile);
        loader.addTickerFileToLoad(StockExchange.Nyse, nyseFile);
        return loader;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setNasdaqFile(String nasdaqFile) {
        this.nasdaqFile = nasdaqFile;
    }

    public void setNyseFile(String nyseFile) {
        this.nyseFile = nyseFile;
    }

    @Override
    public String toString() {
        return "TickerDataLoaderConfiguration{" +
                "enabled=" + enabled +
                ", nasdaqFile='" + nasdaqFile + '\'' +
                ", nyseFile='" + nyseFile + '\'' +
                '}';
    }
}
