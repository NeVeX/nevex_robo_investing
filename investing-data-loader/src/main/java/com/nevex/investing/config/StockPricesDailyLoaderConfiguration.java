package com.nevex.investing.config;

import com.nevex.investing.api.tiingo.TiingoApiClient;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.dataloader.loader.DailyStockPriceLoader;
import com.nevex.investing.event.DailyStockPriceEventProcessor;
import com.nevex.investing.service.StockPriceAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.nevex.investing.PropertyNames.NEVEX_INVESTING;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = StockPricesDailyLoaderConfiguration.CONFIGURATION_PREFIX_KEY)
@ConditionalOnProperty(name = StockPricesDailyLoaderConfiguration.CONFIGURATION_ENABLED_KEY, havingValue = "true")
class StockPricesDailyLoaderConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockPricesDailyLoaderConfiguration.class);

    static final String CONFIGURATION_PREFIX_KEY = NEVEX_INVESTING + ".daily-stock-price-loader";
    static final String CONFIGURATION_ENABLED_KEY = CONFIGURATION_PREFIX_KEY + ".enabled";

    @Autowired
    private DataLoaderService dataLoaderService;
    @Autowired
    private TickersRepository tickersRepository;
    @Autowired
    private TiingoApiClient tiingoApiClient;
    @Autowired
    private StockPriceAdminService stockPriceAdminService;
    @Autowired
    private DailyStockPriceEventProcessor dailyStockPriceEventProcessor;

    @Valid
    @NotNull
    private Boolean enabled;
    @Valid
    @Min(value = 0)
    private Long waitTimeBetweenTickersMs;
    @Valid
    @NotNull
    private Boolean forceStartOnAppStartup;

    @PostConstruct
    void init() throws Exception {
        LOGGER.info("The daily price stock loader configuration has been activated. Configurations [{}]", this);
    }

    @Bean
    DailyStockPriceLoader currentStockPriceLoader() {
        // TODO: This loader is getting too big!
        return new DailyStockPriceLoader(tickersRepository, tiingoApiClient,
                stockPriceAdminService, dataLoaderService, dailyStockPriceEventProcessor, waitTimeBetweenTickersMs, forceStartOnAppStartup);
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setWaitTimeBetweenTickersMs(Long waitTimeBetweenTickersMs) {
        this.waitTimeBetweenTickersMs = waitTimeBetweenTickersMs;
    }

    public void setForceStartOnAppStartup(Boolean forceStartOnAppStartup) {
        this.forceStartOnAppStartup = forceStartOnAppStartup;
    }

    @Override
    public String toString() {
        return "StockPricesDailyLoaderConfiguration{" +
                "enabled=" + enabled +
                ", waitTimeBetweenTickersMs=" + waitTimeBetweenTickersMs +
                ", forceStartOnAppStartup=" + forceStartOnAppStartup +
                '}';
    }
}
