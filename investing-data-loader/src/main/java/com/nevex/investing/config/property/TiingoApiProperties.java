package com.nevex.investing.config.property;

import com.nevex.investing.PropertyNames;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Created by Mark Cunningham on 9/14/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = TiingoApiProperties.TIINGO_PREFIX)
public class TiingoApiProperties {

    static final String TIINGO_PREFIX = PropertyNames.NEVEX_INVESTING+".api-clients.tiingo";

    @NotBlank
    private String apiKey;
    @NotBlank
    private String host;

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String toString() {
        return "TiingoApiProperties{" +
                "apiKey='" + apiKey + '\'' +
                ", host='" + host + '\'' +
                '}';
    }

}
