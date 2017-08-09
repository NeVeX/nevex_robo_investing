package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

import static com.nevex.roboinvesting.config.PropertyNames.ROBO_INVESTING;
import static com.nevex.roboinvesting.config.TiingoApiConfiguration.TIINGO_PREFIX;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = ROBO_INVESTING + TIINGO_PREFIX)
public class TiingoApiConfiguration {

    static final String TIINGO_PREFIX = ".tiingo";

    @NotBlank
    private String apiKey;

    @PostConstruct
    void init() throws Exception {
        tiingoApiClient().getCurrentPriceDataForSymbol("LC");
    }

    @Bean
    TiingoApiClient tiingoApiClient() {
        return new TiingoApiClient(apiKey);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String toString() {
        return "TiingoProperties{" +
                "apiKey='" + apiKey + '\'' +
                '}';
    }
}
