package com.nevex.investing.config.property;

import com.nevex.investing.processor.StockFinancialsAnalyzer;
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
@ConfigurationProperties(prefix = AnalyzerProperties.PREFIX)
public class AnalyzerProperties {

    public final static String PREFIX = ApplicationProperties.PREFIX + ".analyzers";
    public final static String CONFIGURATION_ENABLED = PREFIX + ".configuration-enabled";

    @NotNull
    private Boolean configurationEnabled;
    @NotNull
    @Valid
    private StockFinancialsAnalyzerProperties stockFinancialsAnalyzer;

    public static class StockFinancialsAnalyzerProperties {

        public final static String ENABLED = AnalyzerProperties.PREFIX + ".enabled";

        @NotNull
        private Boolean enabled;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return "StockFinancialsAnalyzerProperties{" +
                    "enabled=" + enabled +
                    '}';
        }
    }

    public Boolean getConfigurationEnabled() {
        return configurationEnabled;
    }

    public void setConfigurationEnabled(Boolean configurationEnabled) {
        this.configurationEnabled = configurationEnabled;
    }

    public StockFinancialsAnalyzerProperties getStockFinancialsAnalyzer() {
        return stockFinancialsAnalyzer;
    }

    public void setStockFinancialsAnalyzer(StockFinancialsAnalyzerProperties stockFinancialsAnalyzer) {
        this.stockFinancialsAnalyzer = stockFinancialsAnalyzer;
    }

    @Override
    public String toString() {
        return "AnalyzerProperties{" +
                "configurationEnabled=" + configurationEnabled +
                ", stockFinancialsAnalyzer=" + stockFinancialsAnalyzer +
                '}';
    }
}
