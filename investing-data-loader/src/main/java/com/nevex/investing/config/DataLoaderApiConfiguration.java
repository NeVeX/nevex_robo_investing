package com.nevex.investing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nevex.investing.PropertyNames;
import com.nevex.investing.api.usfundamentals.UsFundamentalsApiClient;
import com.nevex.investing.config.property.TickerFundamentalsLoaderProperties;
import com.nevex.investing.database.DataLoaderErrorsRepository;
import com.nevex.investing.database.DataLoaderRunsRepository;
import com.nevex.investing.ws.DataLoaderAdminEndpoint;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Created by Mark Cunningham on 8/30/2017.
 */
@Validated
@ConfigurationProperties(prefix = PropertyNames.NEVEX_INVESTING)
@Configuration
public class DataLoaderApiConfiguration {

    @NotBlank(message = "The api-admin-key is blank")
    private String apiAdminKey;
    @Autowired
    private DataLoaderRunsRepository dataLoaderRunsRepository;
    @Autowired
    private DataLoaderErrorsRepository dataLoaderErrorsRepository;
    @Autowired
    private TickerFundamentalsLoaderProperties tickerFundamentalsLoaderProperties;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    DataLoaderAdminEndpoint dataLoaderAdminEndpoint() {
        return new DataLoaderAdminEndpoint(dataLoaderRunsRepository, dataLoaderErrorsRepository, apiAdminKey);
    }

    public void setApiAdminKey(String apiAdminKey) {
        this.apiAdminKey = apiAdminKey;
    }

    @Bean
    UsFundamentalsApiClient usFundamentalsApiClient() {
        return new UsFundamentalsApiClient(
                tickerFundamentalsLoaderProperties.getHost(),
                tickerFundamentalsLoaderProperties.getApiKey(),
                objectMapper
        );
    }

}
