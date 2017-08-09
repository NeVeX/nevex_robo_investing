package com.nevex.roboinvesting.config;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import static com.nevex.roboinvesting.config.PropertyNames.ROBO_INVESTING;
import static com.nevex.roboinvesting.config.TiingoProperties.TIINGO_PREFIX;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = ROBO_INVESTING + TIINGO_PREFIX)
public class TiingoProperties {

    static final String TIINGO_PREFIX = ".tiingo";

    @NotBlank
    private String apiKey;

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
