package com.nevex.investing.config.property;

import com.nevex.investing.PropertyNames;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = ApplicationProperties.PREFIX)
public class ApplicationProperties {
    public static final String PREFIX = PropertyNames.NEVEX_INVESTING;

    @NotBlank
    private String apiAdminKey;
    @NotNull
    @Valid
    private Testing testing;

    public static class Testing {

        public static final String PREFIX = ApplicationProperties.PREFIX + ".testing";
        public static final String ENABLED = PREFIX + ".enabled";

        @NotNull
        private Boolean enabled;
        @NotBlank
        private String allowedSymbols;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getAllowedSymbols() {
            return allowedSymbols;
        }

        public void setAllowedSymbols(String allowedSymbols) {
            this.allowedSymbols = allowedSymbols;
        }

        @Override
        public String toString() {
            return "Testing{" +
                    "enabled=" + enabled +
                    ", allowedSymbols='" + allowedSymbols + '\'' +
                    '}';
        }
    }

    public String getApiAdminKey() {
        return apiAdminKey;
    }

    public void setApiAdminKey(String apiAdminKey) {
        this.apiAdminKey = apiAdminKey;
    }

    public Testing getTesting() {
        return testing;
    }

    public void setTesting(Testing testing) {
        this.testing = testing;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "apiAdminKey='" + apiAdminKey + '\'' +
                ", testing=" + testing +
                '}';
    }
}
