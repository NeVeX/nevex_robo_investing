package com.nevex.investing.analyzer;

import com.nevex.investing.database.entity.YahooStockInfoEntity;
import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.StockFinancialsUpdatedEvent;
import com.nevex.investing.event.type.TickerAnalyzerUpdatedEvent;
import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.model.Analyzer;
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
    private final AnalyzerService analyzerService;
    private EventManager eventManager;

    public StockFinancialsAnalyzer(YahooStockInfoService yahooStockInfoService,
                                   TickerAnalyzersService tickerAnalyzersService,
                                   AnalyzerService analyzerService) {
        super(StockFinancialsUpdatedEvent.class);
        if ( yahooStockInfoService == null ) { throw new IllegalArgumentException("Provided yahooStockInfoService is null"); }
        if ( tickerAnalyzersService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersService is null"); }
        if ( analyzerService == null ) { throw new IllegalArgumentException("Provided analyzerService is null"); }
        this.yahooStockInfoService = yahooStockInfoService;
        this.tickerAnalyzersService = tickerAnalyzersService;
        this.analyzerService = analyzerService;
    }

    @Override
    public String getConsumerName() {
        return "stock-financials-analyzer";
    }

    @Override
    protected void onEvent(StockFinancialsUpdatedEvent event) {
        int tickerId = event.getTickerId();
        LocalDate asOfDate = event.getAsOfDate();
        Optional<YahooStockInfoEntity> entityOpt = yahooStockInfoService.getLatestStockInfo(tickerId, asOfDate);
        if ( !entityOpt.isPresent()) {
            return; // nothing to do
        }

        Set<AnalyzerResult> analyzerResults = new HashSet<>();

        YahooStockInfoEntity entity = entityOpt.get();

        addResult(analyzerResults, tickerId, asOfDate, entity.getPriceToEarningsRatio(), Analyzer.PRICE_TO_EARNINGS_RATIO);

        try {
            // Save all our analyzers
            tickerAnalyzersService.saveNewAnalyzers(analyzerResults);
        } catch (ServiceException sEx) {
            LOGGER.error("Could not save analyzer data in consumer [{}] for [{}] analyzer entries", getConsumerName(), analyzerResults.size(), sEx);
            return;
        }

        sendTickerAnalyzerUpdatedEvent(tickerId, event.getAsOfDate());

        LOGGER.info("{} has finished processing ticker {}", getConsumerName(), tickerId);
    }

    private void addResult(Set<AnalyzerResult> analyzerResults, int tickerId, LocalDate asOfDate, BigDecimal value, Analyzer analyzer) {
        if ( value != null ) {
            Optional<Double> weightResultOpt = analyzerService.getWeight(analyzer, value);
            if ( weightResultOpt.isPresent()) {
                analyzerResults.add(new AnalyzerResult(tickerId, analyzer.getTitle(), weightResultOpt.get(), asOfDate));
            }
        }
    }

    private void sendTickerAnalyzerUpdatedEvent(int tickerId, LocalDate asOfDate) {
        if ( eventManager != null ) {
            eventManager.sendEvent(new TickerAnalyzerUpdatedEvent(tickerId, asOfDate));
        }
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

}
