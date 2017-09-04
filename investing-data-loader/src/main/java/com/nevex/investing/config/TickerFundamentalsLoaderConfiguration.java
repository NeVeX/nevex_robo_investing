package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.TickerFundamentalsLoader;
import com.nevex.investing.usfundamentals.UsFundamentalsApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

import static com.nevex.investing.PropertyNames.NEVEX_INVESTING;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
@Configuration
@ConfigurationProperties(prefix = TickerFundamentalsLoaderConfiguration.CONFIGURATION_PREFIX_KEY)
@ConditionalOnProperty(name = TickerFundamentalsLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
public class TickerFundamentalsLoaderConfiguration {

    static final String CONFIGURATION_PREFIX_KEY = NEVEX_INVESTING + ".ticker-fundamentals-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @NotNull
    private String host;
    @NotNull
    private String apiKey;

    @Autowired
    private DataLoaderService dataLoaderService;

    @Bean
    TickerFundamentalsLoader tickerFundamentalsLoader() {
        return new TickerFundamentalsLoader(dataLoaderService);
    }

    @Bean
    UsFundamentalsApiClient usFundamentalsApiClient() {
        return new UsFundamentalsApiClient(host, apiKey);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
