package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.database.StockExchangesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.dataloader.TickerSymbolLoader;
import com.nevex.roboinvesting.model.StockExchange;
import org.apache.commons.lang3.StringUtils;
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

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = DataLoaderConfiguration.CONFIGURATION_PREFIX_KEY)
@ConditionalOnProperty(name = DataLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
public class DataLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoaderConfiguration.class);

    static final String CONFIGURATION_PREFIX_KEY = "robo-investing.data-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Autowired
    private StockExchangesRepository stockExchangesRepository;
    @Autowired
    private TickersRepository tickersRepository;

    @Valid
    @NotNull(message = "The 'enabled' property cannot be null")
    private Boolean enabled;
    private String nasdaqFile; // can be empty
    private String nyseFile; // can be empty

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The data load configuration has been activated. Configurations [{}]", this);

        if (StringUtils.isNotBlank(nasdaqFile)) {
            tickerSymbolLoader().loadTickers(StockExchange.Nasdaq, nasdaqFile);
        }

        if (StringUtils.isNotBlank(nyseFile)) {
            tickerSymbolLoader().loadTickers(StockExchange.Nyse, nyseFile);
        }

    }

    @Bean
    TickerSymbolLoader tickerSymbolLoader() {
        return new TickerSymbolLoader(stockExchangesRepository, tickersRepository);
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
        return "DataLoaderConfiguration{" +
                "enabled=" + enabled +
                ", nasdaqFile='" + nasdaqFile + '\'' +
                ", nyseFile='" + nyseFile + '\'' +
                '}';
    }
}
