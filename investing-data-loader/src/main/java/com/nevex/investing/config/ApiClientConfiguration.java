package com.nevex.investing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nevex.investing.api.iextrading.IEXTradingClient;
import com.nevex.investing.api.tiingo.TiingoApiClient;
import com.nevex.investing.api.usfundamentals.UsFundamentalsApiClient;
import com.nevex.investing.api.yahoo.YahooApiClient;
import com.nevex.investing.config.property.TiingoApiProperties;
import com.nevex.investing.config.property.UsFundamentalsApiClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * Created by Mark Cunningham on 9/14/2017.
 */
@Validated
@Configuration
class ApiClientConfiguration {

    @Autowired
    @Valid
    private TiingoApiProperties tiingoApiProperties;
    @Autowired
    @Valid
    private UsFundamentalsApiClientProperties usFundamentalsApiClientProperties;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    IEXTradingClient iexTradingClient() {
        return new IEXTradingClient();
    }

    @Bean
    TiingoApiClient tiingoApiClient() {
        return new TiingoApiClient(tiingoApiProperties.getApiKey(), tiingoApiProperties.getHost());
    }

    @Bean
    YahooApiClient yahooApiClient() {
        return new YahooApiClient();
    }

    @Bean
    UsFundamentalsApiClient usFundamentalsApiClient() {
        return new UsFundamentalsApiClient(
                usFundamentalsApiClientProperties.getHost(),
                usFundamentalsApiClientProperties.getApiKey(),
                objectMapper
        );
    }

}

