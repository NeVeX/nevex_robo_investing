package com.nevex.investing.config.property;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    @NotNull
    @Valid
    private AnalyzerPreviousPricePerformanceAnalyzerProperties analyzerPreviousPricePerformanceAnalyzer;

    public static class BaseAnalyzerProperties {
        private static final String DATE_FORMAT = "yyyy-MM-dd";
        @NotNull
        private Boolean enabled;
        @NotNull
        private Boolean sendEventsOnStartup;
        @NotBlank
        private String sendEventsOnStartupStartingFromDate;
        @NotBlank
        private String sendEventsOnStartupEndingOnDate;
        private LocalDate sendEventsOnStartupStartingFromLocalDate;
        private LocalDate sendEventsOnStartupEndingOnLocalDate;

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

        public LocalDate getSendEventsOnStartupStartingFromDate() {
            return sendEventsOnStartupStartingFromLocalDate;
        }

        public void setSendEventsOnStartupStartingFromDate(String sendEventsOnStartupStartingFromDate) {
            this.sendEventsOnStartupStartingFromDate = sendEventsOnStartupStartingFromDate;
            sendEventsOnStartupStartingFromLocalDate = LocalDate.parse(sendEventsOnStartupStartingFromDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
//            if ( this.sendEventsOnStartupStartingFromDate == null ) {
//                sendEventsOnStartupStartingFromLocalDate = LocalDate.now();
//            } else {
//                sendEventsOnStartupStartingFromLocalDate = LocalDate.parse(sendEventsOnStartupStartingFromDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
//            }
        }

        public void setSendEventsOnStartupEndingOnDate(String sendEventsOnStartupEndingOnDate) {
            this.sendEventsOnStartupEndingOnDate = sendEventsOnStartupEndingOnDate;
            sendEventsOnStartupEndingOnLocalDate = LocalDate.parse(sendEventsOnStartupEndingOnDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
//            if ( this.sendEventsOnStartupEndingOnDate == null ) {
//                sendEventsOnStartupEndingOnLocalDate = LocalDate.now();
//            } else {
//                sendEventsOnStartupEndingOnLocalDate = LocalDate.parse(sendEventsOnStartupEndingOnDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
//            }
        }

        public LocalDate getSendEventsOnStartupEndingOnLocalDate() {
            return sendEventsOnStartupEndingOnLocalDate;
        }

        @Override
        public String toString() {
            return "BaseAnalyzerProperties{" +
                    "enabled=" + enabled +
                    ", sendEventsOnStartup=" + sendEventsOnStartup +
                    ", sendEventsOnStartupStartingFromDate=" + sendEventsOnStartupStartingFromDate +
                    ", sendEventsOnStartupStartingFromLocalDate=" + sendEventsOnStartupStartingFromLocalDate +
                    ", sendEventsOnStartupEndingOnDate=" + sendEventsOnStartupEndingOnDate +
                    ", sendEventsOnStartupEndingOnLocalDate=" + sendEventsOnStartupEndingOnLocalDate +
                    '}';
        }
    }

    public static class AllAnalyzersSummaryAnalyzerProperties {
        public final static String ENABLED = AnalyzerProperties.PREFIX + ".all-analyzers-summary-analyzer.enabled";
    }

    public static class StockFinancialsAnalyzerProperties extends BaseAnalyzerProperties {
        public final static String ENABLED = AnalyzerProperties.PREFIX + ".stock-financials-analyzer.enabled";
    }

    public static class StockPriceChangeAnalyzerProperties extends BaseAnalyzerProperties {
        public final static String ENABLED = AnalyzerProperties.PREFIX + ".stock-price-change-analyzer.enabled";
    }

    public static class AnalyzerPreviousPricePerformanceAnalyzerProperties extends BaseAnalyzerProperties {
        public final static String ENABLED = AnalyzerProperties.PREFIX + ".analyzer-previous-price-performance-analyzer.enabled";
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

    public AnalyzerPreviousPricePerformanceAnalyzerProperties getAnalyzerPreviousPricePerformanceAnalyzer() {
        return analyzerPreviousPricePerformanceAnalyzer;
    }

    public void setAnalyzerPreviousPricePerformanceAnalyzer(AnalyzerPreviousPricePerformanceAnalyzerProperties analyzerPreviousPricePerformanceAnalyzer) {
        this.analyzerPreviousPricePerformanceAnalyzer = analyzerPreviousPricePerformanceAnalyzer;
    }

    @Override
    public String toString() {
        return "AnalyzerProperties{" +
                "configurationEnabled=" + configurationEnabled +
                ", stockFinancialsAnalyzer=" + stockFinancialsAnalyzer +
                ", stockPriceChangeAnalyzer=" + stockPriceChangeAnalyzer +
                ", allAnalyzersSummaryAnalyzer=" + allAnalyzersSummaryAnalyzer +
                ", analyzerPreviousPricePerformanceAnalyzer=" + analyzerPreviousPricePerformanceAnalyzer +
                '}';
    }
}
