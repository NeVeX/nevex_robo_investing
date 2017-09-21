package com.nevex.investing.config.property;

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
    @NotNull
    @Valid
    private StockPriceChangeAnalyzerProperties stockPriceChangeAnalyzer;
    @NotNull
    @Valid
    private StockFinancialsSummaryAnalyzerProperties stockFinancialsSummaryAnalyzer;

    public static class StockFinancialsSummaryAnalyzerProperties {
        public final static String ENABLED = AnalyzerProperties.PREFIX + ".stock-financials-summary-analyzer.enabled";

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
            return "StockFinancialsSummaryAnalyzerProperties{" +
                    "enabled=" + enabled +
                    '}';
        }
    }

    public static class StockFinancialsAnalyzerProperties {
        public final static String ENABLED = AnalyzerProperties.PREFIX + ".stock-financials-analyzer.enabled";

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

    public static class StockPriceChangeAnalyzerProperties {

        public final static String ENABLED = AnalyzerProperties.PREFIX + ".stock-price-change-analyzer.enabled";

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
            return "StockPriceChangeAnalyzerProperties{" +
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

    public StockPriceChangeAnalyzerProperties getStockPriceChangeAnalyzer() {
        return stockPriceChangeAnalyzer;
    }

    public void setStockPriceChangeAnalyzer(StockPriceChangeAnalyzerProperties stockPriceChangeAnalyzer) {
        this.stockPriceChangeAnalyzer = stockPriceChangeAnalyzer;
    }

    public StockFinancialsSummaryAnalyzerProperties getStockFinancialsSummaryAnalyzer() {
        return stockFinancialsSummaryAnalyzer;
    }

    public void setStockFinancialsSummaryAnalyzer(StockFinancialsSummaryAnalyzerProperties stockFinancialsSummaryAnalyzer) {
        this.stockFinancialsSummaryAnalyzer = stockFinancialsSummaryAnalyzer;
    }

    @Override
    public String toString() {
        return "AnalyzerProperties{" +
                "configurationEnabled=" + configurationEnabled +
                ", stockFinancialsAnalyzer=" + stockFinancialsAnalyzer +
                ", stockPriceChangeAnalyzer=" + stockPriceChangeAnalyzer +
                ", stockFinancialsSummaryAnalyzer=" + stockFinancialsSummaryAnalyzer +
                '}';
    }
}
