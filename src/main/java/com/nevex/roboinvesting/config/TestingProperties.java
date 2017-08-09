package com.nevex.roboinvesting.config;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.nevex.roboinvesting.config.PropertyNames.ROBO_INVESTING;
import static com.nevex.roboinvesting.config.TestingProperties.TESTING_PREFIX;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = ROBO_INVESTING + TESTING_PREFIX)
public class TestingProperties {

    static final String TESTING_PREFIX = ".testing";
    private final Set<String> allowableSymbols = new HashSet<>();

    @NotBlank
    private String allowedSymbols;

    public void setAllowedSymbols(String allowedSymbols) {
        this.allowedSymbols = allowedSymbols;
        allowableSymbols.addAll(Arrays.asList(allowedSymbols.split(",")));
    }

    public Set<String> getAllowableSymbols() {
        return allowableSymbols;
    }

    @Override
    public String toString() {
        return "TestingProperties{" +
                "allowableSymbols=" + allowableSymbols +
                ", allowedSymbols='" + allowedSymbols + '\'' +
                '}';
    }
}
