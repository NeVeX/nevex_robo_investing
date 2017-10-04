package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerPricePerformance;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.database.AnalyzerPricePerformanceRepository;
import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.type.AllAnalyzerSummaryUpdatedEvent;
import com.nevex.investing.service.AnalyzerPricePerformanceAdminService;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerAnalyzersAdminService;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.ServiceException;
import com.nevex.investing.service.model.StockPrice;
import com.nevex.investing.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public class AnalyerPreviousPricePerformanceAnalyzer extends EventConsumer<AllAnalyzerSummaryUpdatedEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AnalyerPreviousPricePerformanceAnalyzer.class);
    private final TickerAnalyzersAdminService tickerAnalyzersAdminService;
    private final StockPriceAdminService stockPriceAdminService;
    private final AnalyzerPricePerformanceAdminService analyzerPricePerformanceAdminService;

    public AnalyerPreviousPricePerformanceAnalyzer(TickerAnalyzersAdminService tickerAnalyzersAdminService,
                                                   StockPriceAdminService stockPriceAdminService,
                                                   AnalyzerPricePerformanceAdminService analyzerPricePerformanceAdminService) {
        super(AllAnalyzerSummaryUpdatedEvent.class);
        if ( tickerAnalyzersAdminService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersAdminService is null"); }
        if ( stockPriceAdminService == null ) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( analyzerPricePerformanceAdminService == null ) { throw new IllegalArgumentException("Provided analyzerPricePerformanceAdminService is null"); }
        this.tickerAnalyzersAdminService = tickerAnalyzersAdminService;
        this.stockPriceAdminService = stockPriceAdminService;
        this.analyzerPricePerformanceAdminService = analyzerPricePerformanceAdminService;
}

    @Override
    public int getOrder() {
        return AnalyzerOrder.ANALYZERS_PERFORMANCE_ANALYZER;
    }

    @Override
    public String getConsumerName() {
        return "analyzer-previous-price-performance-analyzer";
    }

    @Override
    protected void onEvent(AllAnalyzerSummaryUpdatedEvent event) {

        int tickerId = event.getTickerId();
        LocalDate asOfDate = event.getAsOfDate();

        // get the previous price (a non weekend date)
        LocalDate previousDateToFetch = DateUtils.getPreviousWeekDate(asOfDate);

        Optional<AnalyzerSummaryResult> summaryResultOpt = tickerAnalyzersAdminService.getAnalyzerSummary(tickerId, previousDateToFetch);
        if ( !summaryResultOpt.isPresent()) {
            return;
        }
        AnalyzerSummaryResult previousSummaryResult = summaryResultOpt.get();
        // Now get the stock price for that same day
        Optional<StockPrice> previousStockPriceOpt;
        // And get the event price too
        Optional<StockPrice> asOfEventPriceOpt;
        try {
            previousStockPriceOpt = stockPriceAdminService.getHistoricalPriceForDate(tickerId, previousDateToFetch);
            asOfEventPriceOpt = stockPriceAdminService.getHistoricalPriceForDate(tickerId, asOfDate);
        } catch (TickerNotFoundException notFoundEx) {
            LOGGER.error("No historical stock price was found, since ticker could not be found [{}]", tickerId);
            return;
        }

        if ( !previousStockPriceOpt.isPresent() || !asOfEventPriceOpt.isPresent()) {
            return;
        }
        calculatePricePerformance(tickerId, event.getAsOfDate(), asOfEventPriceOpt.get(), previousStockPriceOpt.get(), previousSummaryResult);
    }

    private void calculatePricePerformance(int tickerId, LocalDate asOfDate, StockPrice asOfStockPrice, StockPrice previousStockPrice, AnalyzerSummaryResult summaryResult) {

        BigDecimal previousPrice = previousStockPrice.getClose();
        BigDecimal asOfPrice = asOfStockPrice.getClose();
        double previousAdjustedWeight = summaryResult.getAdjustedWeight();
        BigDecimal priceDifference = asOfPrice.subtract(previousPrice);
        BigDecimal percentDifference = priceDifference.setScale(6, RoundingMode.HALF_EVEN).divide(asOfPrice, RoundingMode.HALF_EVEN);
        boolean priceDirectionAsExpected = isSignValuesEqual(previousAdjustedWeight, priceDifference);

        Optional<String> symbolOpt = stockPriceAdminService.tryGetSymbolForTickerId(tickerId);
        if ( !symbolOpt.isPresent()) {
            return;
        }

        AnalyzerPricePerformance pricePerformance = new AnalyzerPricePerformance(symbolOpt.get(), tickerId, asOfDate, priceDirectionAsExpected, priceDifference, percentDifference);
        try {
            analyzerPricePerformanceAdminService.saveNewPerformancePriceAnalyzer(pricePerformance);
        } catch (ServiceException serviceEx) {
            LOGGER.error("Could not save price analyzer performance for [{}]", pricePerformance);
        }
    }

    private boolean isSignValuesEqual(double previousAdjustedWeight, BigDecimal priceDifference) {
        return BigDecimal.valueOf(previousAdjustedWeight).signum() == priceDifference.signum();
    }

}
