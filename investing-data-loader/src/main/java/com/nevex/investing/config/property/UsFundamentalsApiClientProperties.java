package com.nevex.investing.config.property;

import com.nevex.investing.PropertyNames;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

import static com.nevex.investing.PropertyNames.NEVEX_INVESTING;

/**
 * Created by Mark Cunningham on 9/14/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = UsFundamentalsApiClientProperties.CONFIGURATION_PREFIX_KEY)
public class UsFundamentalsApiClientProperties {

    static final String CONFIGURATION_PREFIX_KEY = NEVEX_INVESTING + ".api-clients.us-fundamentals";

    @NotBlank
    private String host;
    @NotBlank
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

    @Override
    public String toString() {
        return "UsFundamentalsApiClientProperties{" +
                "host='" + host + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
