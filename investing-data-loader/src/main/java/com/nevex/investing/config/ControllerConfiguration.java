package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
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
class ControllerConfiguration {

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
