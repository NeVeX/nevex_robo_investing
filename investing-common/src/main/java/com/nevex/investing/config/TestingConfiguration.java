package com.nevex.investing.config;

import com.nevex.investing.TestingControlUtil;
import com.nevex.investing.config.property.ApplicationProperties;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
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
@ConditionalOnProperty(name = ApplicationProperties.Testing.ENABLED, havingValue = "true")
public class TestingConfiguration {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestingConfiguration.class);
    static final String TESTING_PREFIX = NEVEX_INVESTING + ".testing";
    private final Set<String> allowableSymbols = new HashSet<>();

    @Autowired
    private ApplicationProperties applicationProperties;

    @PostConstruct
    void init() {

        LOGGER.warn("\n\n\n*** Testing mode is activated ***\n\n\n");
        LOGGER.info("Testing mode properties: {}", applicationProperties.getTesting());

        List<String> symbols = Arrays.asList(applicationProperties.getTesting().getAllowedSymbols().toUpperCase().split(","));
        allowableSymbols.addAll(symbols.stream().map(StringUtils::trimWhitespace).collect(Collectors.toList()));
        TestingControlUtil.addAllowableTickers(allowableSymbols);
    }


}
