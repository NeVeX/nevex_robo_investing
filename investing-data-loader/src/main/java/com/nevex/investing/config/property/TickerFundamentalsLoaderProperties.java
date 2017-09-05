package com.nevex.investing.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

import static com.nevex.investing.PropertyNames.NEVEX_INVESTING;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
@Configuration
@ConfigurationProperties(prefix = TickerFundamentalsLoaderProperties.CONFIGURATION_PREFIX_KEY)
public class TickerFundamentalsLoaderProperties {

    static final String CONFIGURATION_PREFIX_KEY = NEVEX_INVESTING + ".ticker-fundamentals-loader";

    @NotNull
    private String host;
    @NotNull
    private String apiKey;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
