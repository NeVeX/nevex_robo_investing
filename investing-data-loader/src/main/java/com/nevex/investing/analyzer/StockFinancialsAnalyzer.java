package com.nevex.investing.analyzer;

import com.nevex.investing.database.entity.YahooStockInfoEntity;
import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.StockFinancialsUpdatedEvent;
import com.nevex.investing.event.type.TickerAnalyzerUpdatedEvent;
import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.YahooStockInfoService;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public class StockFinancialsAnalyzer extends EventConsumer<StockFinancialsUpdatedEvent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(StockFinancialsAnalyzer.class);
    private final YahooStockInfoService yahooStockInfoService;
    private final TickerAnalyzersService tickerAnalyzersService;
    private EventManager eventManager;

    public StockFinancialsAnalyzer(YahooStockInfoService yahooStockInfoService,
                                   TickerAnalyzersService tickerAnalyzersService) {
        super(StockFinancialsUpdatedEvent.class);
        if ( yahooStockInfoService == null ) { throw new IllegalArgumentException("Provided yahooStockInfoService is null"); }
        if ( tickerAnalyzersService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersService is null"); }
        this.yahooStockInfoService = yahooStockInfoService;
        this.tickerAnalyzersService = tickerAnalyzersService;
    }

    @Override
    public String getConsumerName() {
        return "stock-financials-analyzer";
    }

    @Override
    protected void onEvent(StockFinancialsUpdatedEvent event) {
        int tickerId = event.getTickerId();
        Optional<YahooStockInfoEntity> entityOpt = yahooStockInfoService.getLatestStockInfo(tickerId);
        if ( !entityOpt.isPresent()) {
            return; // nothing to do
        }

        Set<AnalyzerResult> analyzerResults = new HashSet<>();

        YahooStockInfoEntity entity = entityOpt.get();
        if ( entity.getPriceToEarningsRatio() != null ) {
            analyzerResults.add(new AnalyzerResult(entity.getTickerId(), "price-to-earnings", getResultForPE(entity.getPriceToEarningsRatio())));
        }

        try {
            // Save all our analyzers
            tickerAnalyzersService.saveNewAnalyzers(analyzerResults);
        } catch (ServiceException sEx) {
            LOGGER.error("Could not save analyzer data in consumer [{}] for [{}] analyzer entries", getConsumerName(), analyzerResults.size(), sEx);
            return;
        }

        sendTickerAnalyzerUpdatedEvent(tickerId, event.getAsOfDate());
    }

    private void sendTickerAnalyzerUpdatedEvent(int tickerId, LocalDate asOfDate) {
        if ( eventManager != null ) {
            eventManager.sendEvent(new TickerAnalyzerUpdatedEvent(tickerId, asOfDate));
        }
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    private double getResultForPE(BigDecimal priceToEarningsRatio) {

        if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(5)) <= 1) { return 0.8; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(10)) <= 1) { return 0.7; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(20)) <= 1) { return 0.6; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(30)) <= 1) { return 0.4; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(50)) <= 1) { return 0.1; }
        return 0; // terrible...
    }


}
