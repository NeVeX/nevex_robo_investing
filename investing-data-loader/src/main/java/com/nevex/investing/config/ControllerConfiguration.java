package com.nevex.investing.config;

import com.nevex.investing.PropertyNames;
import com.nevex.investing.config.property.ApplicationProperties;
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

    @Autowired
    private DataLoaderRunsRepository dataLoaderRunsRepository;
    @Autowired
    private DataLoaderErrorsRepository dataLoaderErrorsRepository;
    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    DataLoaderAdminEndpoint dataLoaderAdminEndpoint() {
        return new DataLoaderAdminEndpoint(dataLoaderRunsRepository, dataLoaderErrorsRepository, applicationProperties.getApiAdminKey());
    }

}
