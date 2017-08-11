package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

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
    @NotBlank
    private String host;

//    @PostConstruct
//    void init() throws Exception {
//        tiingoApiClient().getCurrentPriceForSymbol("LC");
//        tiingoApiClient().getHistoricalPricesForSymbol("LC", 365);
//    }

    @Bean
    TiingoApiClient tiingoApiClient() {
        return new TiingoApiClient(apiKey, host);
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "TiingoApiConfiguration{" +
                "apiKey='" + apiKey + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
