package com.nevex.roboinvesting.config;

import com.nevex.roboinvesting.PropertyNames;
import com.nevex.roboinvesting.database.DataLoaderErrorsRepository;
import com.nevex.roboinvesting.database.DataLoaderRunsRepository;
import com.nevex.roboinvesting.ws.DataLoaderAdminEndpoint;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

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

    @Bean
    DataLoaderAdminEndpoint dataLoaderAdminEndpoint() {
        return new DataLoaderAdminEndpoint(dataLoaderRunsRepository, dataLoaderErrorsRepository, apiAdminKey);
    }

    public void setApiAdminKey(String apiAdminKey) {
        this.apiAdminKey = apiAdminKey;
    }
}
