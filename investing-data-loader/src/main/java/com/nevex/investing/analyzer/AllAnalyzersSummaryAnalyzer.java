package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.event.EventConsumer;
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

        Optional<Double> weightOptional = analyzerService.getWeight(Analyzer.ANALYZER_SUMMARY_COUNTER_ADJUST_WEIGHT, BigDecimal.valueOf(stats.getCount()));
        if ( !weightOptional.isPresent()) {
            LOGGER.warn("Could not get an adjusted weight for ["+Analyzer.ANALYZER_SUMMARY_COUNTER_ADJUST_WEIGHT+"]");
            return;
        }
        double averageWeight = stats.getAverage();
        double adjustedWeight = weightOptional.get() + averageWeight;

        // limit the edges
        if ( adjustedWeight > 1.0 ) { adjustedWeight = 1.0; }
        if ( adjustedWeight < -1.0 ) { adjustedWeight = -1.0; }

        AnalyzerSummaryResult summaryResult = new AnalyzerSummaryResult(tickerId, averageWeight, adjustedWeight, (int) stats.getCount(), asOfDate);

        try {
            tickerAnalyzersAdminService.saveNewAnalyzer(summaryResult);
        } catch (ServiceException serEx) {
            LOGGER.error("Could not save summary analyzer entity ["+summaryResult+"]", serEx);
        }
        LOGGER.debug("{} has finished processing ticker {}", getConsumerName(), tickerId);
    }

}
