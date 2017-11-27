package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.analyzer.model.AnalyzerWeightV2;
import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.AllAnalyzerSummaryUpdatedEvent;
import com.nevex.investing.event.type.TickerAnalyzerUpdatedEvent;
import com.nevex.investing.model.Analyzer;
import com.nevex.investing.service.TickerAnalyzersAdminService;
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
public class AllAnalyzersSummaryAnalyzer extends EventConsumer<TickerAnalyzerUpdatedEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AllAnalyzersSummaryAnalyzer.class);
    private final TickerAnalyzersAdminService tickerAnalyzersAdminService;
    private final AnalyzerServiceV2 analyzerService;

    public AllAnalyzersSummaryAnalyzer(TickerAnalyzersAdminService tickerAnalyzersAdminService, AnalyzerServiceV2 analyzerService) {
        super(TickerAnalyzerUpdatedEvent.class);
        if ( tickerAnalyzersAdminService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersAdminService is null"); }
        if ( analyzerService == null ) { throw new IllegalArgumentException("Provided analyzerService is null"); }
        this.tickerAnalyzersAdminService = tickerAnalyzersAdminService;
        this.analyzerService = analyzerService;
    }

    @Override
    public int getOrder() {
        return AnalyzerOrder.ALL_ANALYZERS_SUMMARY_ANALYZER;
    }

    @Override
    public String getConsumerName() {
        return "all-analyzers-summary-analyzer";
    }

    @Override
    protected void onEvent(TickerAnalyzerUpdatedEvent event) {
        int tickerId = event.getTickerId();
        LocalDate asOfDate = event.getAsOfDate();
        List<AnalyzerResult> analyzerResults = tickerAnalyzersAdminService.getAnalyzers(tickerId, asOfDate);

        if ( analyzerResults.isEmpty()) {
            LOGGER.warn("{} cannot do anything for ticker [{}] since there are no saved ticker analyzers to process", getConsumerName(), tickerId);
            return;
        }

        // Use the java api to calculateWeight the average
        DoubleSummaryStatistics stats = analyzerResults.stream().collect(Collectors.summarizingDouble(AnalyzerResult::getWeight));

        double averageWeight = stats.getAverage();

        int currentTotalAnalyzersInSystem = analyzerService.getTotalWeights();
        int tickerAnalyzerCount = (int) stats.getCount();

        double adjustedWeight = getAdjustedWeight(currentTotalAnalyzersInSystem, tickerAnalyzerCount);

        AnalyzerSummaryResult summaryResult = new AnalyzerSummaryResult(tickerId, averageWeight, adjustedWeight, tickerAnalyzerCount, asOfDate);

        try {
            tickerAnalyzersAdminService.saveNewAnalyzer(summaryResult);
            EventManager.sendEvent(new AllAnalyzerSummaryUpdatedEvent(tickerId, asOfDate));
        } catch (ServiceException serEx) {
            LOGGER.error("Could not save summary analyzer entity ["+summaryResult+"]", serEx);
        }
        LOGGER.debug("{} has finished processing ticker {}", getConsumerName(), tickerId);
    }

    double getAdjustedWeight(int currentTotalAnalyzersInSystem, int tickerAnalyzerCount) {
        AnalyzerWeightV2 analyzerWeight = getWeightForTotalAnalyzers(currentTotalAnalyzersInSystem);
        return analyzerWeight.calculateWeight(new BigDecimal(tickerAnalyzerCount));
    }

    private AnalyzerWeightV2 getWeightForTotalAnalyzers(int totalAnalyzers) {
        BigDecimal center = new BigDecimal(totalAnalyzers);
        BigDecimal highest = center;
        BigDecimal lowest = highest.negate();
        return new AnalyzerWeightV2(1, Analyzer.ANALYZER_SUMMARY_DYNAMIC_COUNTER_ADJUST_WEIGHT,
                center, lowest, highest, false, false, 1);
    }

}
