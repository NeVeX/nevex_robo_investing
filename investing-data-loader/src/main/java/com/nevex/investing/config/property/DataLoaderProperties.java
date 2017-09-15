package com.nevex.investing.config.property;

import com.nevex.investing.PropertyNames;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Mark Cunningham on 9/14/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = DataLoaderProperties.PREFIX)
public class DataLoaderProperties {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataLoaderProperties.class);
    public static final String PREFIX = PropertyNames.NEVEX_INVESTING+".data-loaders";

    @Valid
    @NotNull
    private TickerDataLoaderProperties tickerDataLoader;
    @Valid
    @NotNull
    private HistoricalStockLoaderProperties stockHistoricalLoader;
    @Valid
    @NotNull
    private DailyStockPriceLoaderProperties dailyStockPriceLoader;
    @Valid
    @NotNull
    private EdgarTickerToCikLoaderProperties edgarTickerToCikLoader;
    @Valid
    @NotNull
    private HistoricalTickerFundamentalsLoaderProperties tickerHistoricalFundamentalsLoader;
    @Valid
    @NotNull
    private YahooStockInfoDataLoaderProperties yahooStockInfoDataLoader;

    @PostConstruct
    void init() {
        LOGGER.info("The data-loader properties are {}", this.toString());
    }

    public static class YahooStockInfoDataLoaderProperties {
        @NotNull
        private Boolean enabled;
        @NotNull
        private Boolean forceStartOnAppStartup;
        @NotNull
        @Min(value = 1)
        private Long waitTimeBetweenBulkMs;
        @NotNull
        @Min(value = 1)
        private Integer bulkAmountPerPage;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean getForceStartOnAppStartup() {
            return forceStartOnAppStartup;
        }

        public void setForceStartOnAppStartup(Boolean forceStartOnAppStartup) {
            this.forceStartOnAppStartup = forceStartOnAppStartup;
        }

        public Long getWaitTimeBetweenBulkMs() {
            return waitTimeBetweenBulkMs;
        }

        public void setWaitTimeBetweenBulkMs(Long waitTimeBetweenBulkMs) {
            this.waitTimeBetweenBulkMs = waitTimeBetweenBulkMs;
        }

        public Integer getBulkAmountPerPage() {
            return bulkAmountPerPage;
        }

        public void setBulkAmountPerPage(Integer bulkAmountPerPage) {
            this.bulkAmountPerPage = bulkAmountPerPage;
        }

        @Override
        public String toString() {
            return "YahooStockInfoDataLoaderProperties{" +
                    "enabled=" + enabled +
                    ", forceStartOnAppStartup=" + forceStartOnAppStartup +
                    ", bulkAmountPerPage=" + bulkAmountPerPage +
                    ", waitTimeBetweenBulkMs=" + waitTimeBetweenBulkMs +
                    '}';
        }
    }

    public static class HistoricalTickerFundamentalsLoaderProperties {
        @NotNull
        private Boolean enabled;
        @NotNull
        private Boolean forceStartOnAppStartup;
        @NotNull
        @Min(value = 0)
        private Integer waitTimeBetweenTickersMs;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean getForceStartOnAppStartup() {
            return forceStartOnAppStartup;
        }

        public void setForceStartOnAppStartup(Boolean forceStartOnAppStartup) {
            this.forceStartOnAppStartup = forceStartOnAppStartup;
        }

        public Integer getWaitTimeBetweenTickersMs() {
            return waitTimeBetweenTickersMs;
        }

        public void setWaitTimeBetweenTickersMs(Integer waitTimeBetweenTickersMs) {
            this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        }

        @Override
        public String toString() {
            return "HistoricalTickerFundamentalsLoaderProperties{" +
                    "enabled=" + enabled +
                    ", forceStartOnAppStartup=" + forceStartOnAppStartup +
                    ", waitTimeBetweenTickersMs=" + waitTimeBetweenTickersMs +
                    '}';
        }
    }

    public static class EdgarTickerToCikLoaderProperties {
        @NotNull
        private Boolean enabled;
        @NotNull
        private Boolean onlyLookupMissingTickerCiks;
        @NotNull
        @Min(value = 0)
        private Integer waitTimeBetweenTickersMs;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean getOnlyLookupMissingTickerCiks() {
            return onlyLookupMissingTickerCiks;
        }

        public void setOnlyLookupMissingTickerCiks(Boolean onlyLookupMissingTickerCiks) {
            this.onlyLookupMissingTickerCiks = onlyLookupMissingTickerCiks;
        }

        public Integer getWaitTimeBetweenTickersMs() {
            return waitTimeBetweenTickersMs;
        }

        public void setWaitTimeBetweenTickersMs(Integer waitTimeBetweenTickersMs) {
            this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        }

        @Override
        public String toString() {
            return "EdgarTickerToCikLoaderProperties{" +
                    "enabled=" + enabled +
                    ", onlyLookupMissingTickerCiks=" + onlyLookupMissingTickerCiks +
                    ", waitTimeBetweenTickersMs=" + waitTimeBetweenTickersMs +
                    '}';
        }
    }

    public static class DailyStockPriceLoaderProperties {
        @NotNull
        private Boolean enabled;
        @NotNull
        @Min(value = 0)
        private Integer waitTimeBetweenTickersMs;
        @NotNull
        private Boolean forceStartOnAppStartup;
        @NotNull
        private Boolean useBulkMode;
        @NotNull
        @Min(value = 1)
        private Long waitTimeBetweenBulkMs;
        @NotNull
        @Min(value = 1)
        private Integer bulkAmountPerPage;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getWaitTimeBetweenTickersMs() {
            return waitTimeBetweenTickersMs;
        }

        public void setWaitTimeBetweenTickersMs(Integer waitTimeBetweenTickersMs) {
            this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        }

        public Boolean getForceStartOnAppStartup() {
            return forceStartOnAppStartup;
        }

        public void setForceStartOnAppStartup(Boolean forceStartOnAppStartup) {
            this.forceStartOnAppStartup = forceStartOnAppStartup;
        }

        public Boolean getUseBulkMode() {
            return useBulkMode;
        }

        public void setUseBulkMode(Boolean useBulkMode) {
            this.useBulkMode = useBulkMode;
        }

        public Long getWaitTimeBetweenBulkMs() {
            return waitTimeBetweenBulkMs;
        }

        public void setWaitTimeBetweenBulkMs(Long waitTimeBetweenBulkMs) {
            this.waitTimeBetweenBulkMs = waitTimeBetweenBulkMs;
        }

        public Integer getBulkAmountPerPage() {
            return bulkAmountPerPage;
        }

        public void setBulkAmountPerPage(Integer bulkAmountPerPage) {
            this.bulkAmountPerPage = bulkAmountPerPage;
        }

        @Override
        public String toString() {
            return "DailyStockPriceLoaderProperties{" +
                    "enabled=" + enabled +
                    ", waitTimeBetweenTickersMs=" + waitTimeBetweenTickersMs +
                    ", useBulkMode=" + useBulkMode +
                    ", waitTimeBetweenBulkMs=" + waitTimeBetweenBulkMs +
                    ", bulkAmountPerPage=" + bulkAmountPerPage +
                    ", forceStartOnAppStartup=" + forceStartOnAppStartup +
                    '}';
        }
    }

    public static class HistoricalStockLoaderProperties {
        @NotNull
        private Boolean enabled;
        @NotNull
        @Min(value = 0)
        private Integer waitTimeBetweenTickersMs;
        @NotNull
        private Boolean useBulkMode;
        @NotNull
        @Min(value = 1)
        private Integer maxDaysToFetch;
        @NotNull
        @Min(value = 1)
        private Long waitTimeBetweenBulkMs;
        @NotNull
        @Min(value = 1)
        private Integer bulkAmountPerPage;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getWaitTimeBetweenTickersMs() {
            return waitTimeBetweenTickersMs;
        }

        public void setWaitTimeBetweenTickersMs(Integer waitTimeBetweenTickersMs) {
            this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
        }

        public Boolean getUseBulkMode() {
            return useBulkMode;
        }

        public void setUseBulkMode(Boolean useBulkMode) {
            this.useBulkMode = useBulkMode;
        }

        public Integer getMaxDaysToFetch() {
            return maxDaysToFetch;
        }

        public void setMaxDaysToFetch(Integer maxDaysToFetch) {
            this.maxDaysToFetch = maxDaysToFetch;
        }

        public Long getWaitTimeBetweenBulkMs() {
            return waitTimeBetweenBulkMs;
        }

        public void setWaitTimeBetweenBulkMs(Long waitTimeBetweenBulkMs) {
            this.waitTimeBetweenBulkMs = waitTimeBetweenBulkMs;
        }

        public Integer getBulkAmountPerPage() {
            return bulkAmountPerPage;
        }

        public void setBulkAmountPerPage(Integer bulkAmountPerPage) {
            this.bulkAmountPerPage = bulkAmountPerPage;
        }

        @Override
        public String toString() {
            return "HistoricalStockLoaderProperties{" +
                    "enabled=" + enabled +
                    ", maxDaysToFetch=" + maxDaysToFetch +
                    ", waitTimeBetweenTickersMs=" + waitTimeBetweenTickersMs +
                    ", waitTimeBetweenBulkMs=" + waitTimeBetweenBulkMs +
                    ", bulkAmountPerPage=" + bulkAmountPerPage +
                    ", useBulkMode=" + useBulkMode +
                    '}';
        }
    }

    public static class TickerDataLoaderProperties {
        @NotNull
        private Boolean enabled;
        @NotBlank
        private String nasdaqFile;
        @NotBlank
        private String nyseFile;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getNasdaqFile() {
            return nasdaqFile;
        }

        public void setNasdaqFile(String nasdaqFile) {
            this.nasdaqFile = nasdaqFile;
        }

        public String getNyseFile() {
            return nyseFile;
        }

        public void setNyseFile(String nyseFile) {
            this.nyseFile = nyseFile;
        }

        @Override
        public String toString() {
            return "TickerDataLoaderProperties{" +
                    "enabled=" + enabled +
                    ", nasdaqFile='" + nasdaqFile + '\'' +
                    ", nyseFile='" + nyseFile + '\'' +
                    '}';
        }
    }

    public TickerDataLoaderProperties getTickerDataLoader() {
        return tickerDataLoader;
    }

    public void setTickerDataLoader(TickerDataLoaderProperties tickerDataLoader) {
        this.tickerDataLoader = tickerDataLoader;
    }

    public HistoricalStockLoaderProperties getStockHistoricalLoader() {
        return stockHistoricalLoader;
    }

    public void setStockHistoricalLoader(HistoricalStockLoaderProperties stockHistoricalLoader) {
        this.stockHistoricalLoader = stockHistoricalLoader;
    }

    public DailyStockPriceLoaderProperties getDailyStockPriceLoader() {
        return dailyStockPriceLoader;
    }

    public void setDailyStockPriceLoader(DailyStockPriceLoaderProperties dailyStockPriceLoader) {
        this.dailyStockPriceLoader = dailyStockPriceLoader;
    }

    public EdgarTickerToCikLoaderProperties getEdgarTickerToCikLoader() {
        return edgarTickerToCikLoader;
    }

    public void setEdgarTickerToCikLoader(EdgarTickerToCikLoaderProperties edgarTickerToCikLoader) {
        this.edgarTickerToCikLoader = edgarTickerToCikLoader;
    }

    public HistoricalTickerFundamentalsLoaderProperties getTickerHistoricalFundamentalsLoader() {
        return tickerHistoricalFundamentalsLoader;
    }

    public void setTickerHistoricalFundamentalsLoader(HistoricalTickerFundamentalsLoaderProperties tickerHistoricalFundamentalsLoader) {
        this.tickerHistoricalFundamentalsLoader = tickerHistoricalFundamentalsLoader;
    }

    public YahooStockInfoDataLoaderProperties getYahooStockInfoDataLoader() {
        return yahooStockInfoDataLoader;
    }

    public void setYahooStockInfoDataLoader(YahooStockInfoDataLoaderProperties yahooStockInfoDataLoader) {
        this.yahooStockInfoDataLoader = yahooStockInfoDataLoader;
    }

    @Override
    public String toString() {
        return "DataLoadersProperties{" +
                "tickerDataLoader=" + tickerDataLoader +
                ", stockHistoricalLoader=" + stockHistoricalLoader +
                ", dailyStockPriceLoader=" + dailyStockPriceLoader +
                ", edgarTickerToCikLoader=" + edgarTickerToCikLoader +
                ", tickerHistoricalFundamentalsLoader=" + tickerHistoricalFundamentalsLoader +
                ", yahooStockInfoDataLoader=" + yahooStockInfoDataLoader +
                '}';
    }
}
