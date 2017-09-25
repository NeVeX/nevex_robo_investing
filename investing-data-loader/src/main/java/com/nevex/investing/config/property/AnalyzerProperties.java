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
    private AllAnalyzersSummaryAnalyzerProperties allAnalyzersSummaryAnalyzer;

    public static class AllAnalyzersSummaryAnalyzerProperties {
        public final static String ENABLED = AnalyzerProperties.PREFIX + ".all-analyzers-summary-analyzer.enabled";

        @NotNull
        private Boolean enabled;
        @NotNull
        private Boolean sendEventsOnStartup;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean getSendEventsOnStartup() {
            return sendEventsOnStartup;
        }

        public void setSendEventsOnStartup(Boolean sendEventsOnStartup) {
            this.sendEventsOnStartup = sendEventsOnStartup;
        }

        @Override
        public String toString() {
            return "AllAnalyzersSummaryAnalyzerProperties{" +
                    "enabled=" + enabled +
                    '}';
        }
    }

    public static class StockFinancialsAnalyzerProperties {
        public final static String ENABLED = AnalyzerProperties.PREFIX + ".stock-financials-analyzer.enabled";

        @NotNull
        private Boolean enabled;
        @NotNull
        private Boolean sendEventsOnStartup;

        public Boolean getSendEventsOnStartup() {
            return sendEventsOnStartup;
        }

        public void setSendEventsOnStartup(Boolean sendEventsOnStartup) {
            this.sendEventsOnStartup = sendEventsOnStartup;
        }

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
        @NotNull
        private Boolean sendEventsOnStartup;

        public Boolean getSendEventsOnStartup() {
            return sendEventsOnStartup;
        }

        public void setSendEventsOnStartup(Boolean sendEventsOnStartup) {
            this.sendEventsOnStartup = sendEventsOnStartup;
        }

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

    public AllAnalyzersSummaryAnalyzerProperties getAllAnalyzersSummaryAnalyzer() {
        return allAnalyzersSummaryAnalyzer;
    }

    public void setAllAnalyzersSummaryAnalyzer(AllAnalyzersSummaryAnalyzerProperties allAnalyzersSummaryAnalyzer) {
        this.allAnalyzersSummaryAnalyzer = allAnalyzersSummaryAnalyzer;
    }

    @Override
    public String toString() {
        return "AnalyzerProperties{" +
                "configurationEnabled=" + configurationEnabled +
                ", stockFinancialsAnalyzer=" + stockFinancialsAnalyzer +
                ", stockPriceChangeAnalyzer=" + stockPriceChangeAnalyzer +
                ", allAnalyzersSummaryAnalyzer=" + allAnalyzersSummaryAnalyzer +
                '}';
    }
}
