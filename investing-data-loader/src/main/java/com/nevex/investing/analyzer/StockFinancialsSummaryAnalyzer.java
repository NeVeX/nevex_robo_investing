package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.database.entity.YahooStockInfoEntity;
import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.StockFinancialsUpdatedEvent;
import com.nevex.investing.event.type.TickerAnalyzerUpdatedEvent;
import com.nevex.investing.model.Analyzer;
import com.nevex.investing.service.TickerAnalyzersService;
import com.nevex.investing.service.YahooStockInfoService;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public class StockFinancialsSummaryAnalyzer extends EventConsumer<TickerAnalyzerUpdatedEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(StockFinancialsSummaryAnalyzer.class);
    private final TickerAnalyzersService tickerAnalyzersService;
    private final AnalyzerService analyzerService;

    public StockFinancialsSummaryAnalyzer(TickerAnalyzersService tickerAnalyzersService, AnalyzerService analyzerService) {
        super(TickerAnalyzerUpdatedEvent.class);
        if ( tickerAnalyzersService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersService is null"); }
        if ( analyzerService == null ) { throw new IllegalArgumentException("Provided analyzerService is null"); }
        this.tickerAnalyzersService = tickerAnalyzersService;
        this.analyzerService = analyzerService;
    }

    @Override
    public String getConsumerName() {
        return "stock-financials-summary-analyzer";
    }

    @Override
    protected void onEvent(TickerAnalyzerUpdatedEvent event) {
        int tickerId = event.getTickerId();
        LocalDate asOfDate = event.getAsOfDate();
        List<AnalyzerResult> analyzerResults = tickerAnalyzersService.getAllAnalyzers(tickerId, asOfDate);

        if ( analyzerResults.isEmpty()) {
            LOGGER.warn("{} cannot do anything for ticker [{}] since there are not ticker analyzers saved to process", getConsumerName(), tickerId);
            return;
        }

        // Use the java api to calculate the average
        DoubleSummaryStatistics stats = analyzerResults.stream().collect(Collectors.summarizingDouble(AnalyzerResult::getWeight));

        Optional<Double> weightOptional = analyzerService.getWeight(Analyzer.ANALYZER_SUMMARY_COUNTER_ADJUST_WEIGHT, BigDecimal.valueOf(stats.getCount()));
        if ( !weightOptional.isPresent()) {
            LOGGER.warn("Could not get an adjusted weight for ["+Analyzer.ANALYZER_SUMMARY_COUNTER_ADJUST_WEIGHT+"]");
            return;
        }
        double averageWeight = stats.getAverage();
        double adjustedWeight = weightOptional.get() + averageWeight;
        AnalyzerSummaryResult summaryResult = new AnalyzerSummaryResult(tickerId, averageWeight, adjustedWeight, (int) stats.getCount(), asOfDate);

        try {
            tickerAnalyzersService.saveNewAnalyzer(summaryResult);
        } catch (ServiceException serEx) {
            LOGGER.error("Could not save summary analyzer entity ["+summaryResult+"]", serEx);
        }

        LOGGER.info("{} has finished processing ticker {}", getConsumerName(), tickerId);
    }

}
