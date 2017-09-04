package com.nevex.investing.config;

import com.nevex.investing.TestingControlUtil;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nevex.investing.PropertyNames.NEVEX_INVESTING;
import static com.nevex.investing.config.TestingConfiguration.TESTING_PREFIX;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = TESTING_PREFIX)
@ConditionalOnProperty(name = TESTING_PREFIX + ".enabled", havingValue = "true")
public class TestingConfiguration {

    static final String TESTING_PREFIX = NEVEX_INVESTING + ".testing";
    private final Set<String> allowableSymbols = new HashSet<>();

    @NotBlank
    private String allowedSymbols;

    public void setAllowedSymbols(String allowedSymbols) {
        this.allowedSymbols = allowedSymbols;

        List<String> symbols = Arrays.asList(allowedSymbols.toUpperCase().split(","));
        allowableSymbols.addAll(symbols.stream().map(StringUtils::trimWhitespace).collect(Collectors.toList()));

        TestingControlUtil.addAllowableTickers(allowableSymbols);
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
