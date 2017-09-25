package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.StockPriceUpdatedEvent;
import com.nevex.investing.model.Analyzer;
import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.model.StockPriceSummary;
import com.nevex.investing.analyzer.model.StockPriceSummaryCollector;
import com.nevex.investing.service.TickerAnalyzersAdminService;
import com.nevex.investing.service.model.ServiceException;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.StockPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public class StockPriceChangeAnalyzer extends EventConsumer<StockPriceUpdatedEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(StockPriceChangeAnalyzer.class);
    private final StockPriceAdminService stockPriceAdminService;
    private final AnalyzerService analyzerService;
    private final TickerAnalyzersAdminService tickerAnalyzersAdminService;
    private final static BigDecimal ONE_HUNDERD = new BigDecimal("100");

    public StockPriceChangeAnalyzer(StockPriceAdminService stockPriceAdminService,
                                    AnalyzerService analyzerService,
                                    TickerAnalyzersAdminService tickerAnalyzersAdminService) {
        super(StockPriceUpdatedEvent.class);
        if ( stockPriceAdminService == null ) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( analyzerService == null ) { throw new IllegalArgumentException("Provided analyzerService is null"); }
        if ( tickerAnalyzersAdminService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersAdminService is null"); }
        this.stockPriceAdminService = stockPriceAdminService;
        this.analyzerService = analyzerService;
        this.tickerAnalyzersAdminService = tickerAnalyzersAdminService;
    }

    @Override
    public String getConsumerName() {
        return "stock-price-change-summary-analyzer";
    }

    @Override
    public void onEvent(StockPriceUpdatedEvent event) {
        int tickerId = event.getTickerId();
        LocalDate asOfDate = event.getAsOfDate();
        LOGGER.info("Received new ticker [{}] that has had it's stock price updated - will process it now", tickerId);
        Map<TimePeriod, StockPriceSummary> averages;
        try {

            List<StockPrice> stockPrices = stockPriceAdminService.getHistoricalPrices(tickerId, asOfDate, TimePeriod.OneYear.getDays());
            averages = calculateStockPriceAverages(stockPrices, asOfDate);
            if ( averages == null || averages.isEmpty()) {
                LOGGER.info("Stock price change summary analyzer cannot summarize for ticker [{}], probably not enough data", tickerId);
                return;
            }

            // TODO: Do we actually need to save this data??
            for ( Map.Entry<TimePeriod, StockPriceSummary> entry : averages.entrySet()) {
                try {
                    stockPriceAdminService.savePriceChanges(tickerId, entry.getKey(), entry.getValue());
                } catch (ServiceException sEx) {
                    LOGGER.error("Could not save stock price changes for ticker [{}]", sEx);
                }
            }
        } catch (TickerNotFoundException tickerNotFound) {
            LOGGER.warn("Ticker Id [{}] is not valid - could not find it", tickerId);
            return;
        }

        calculateAnalysis(tickerId, asOfDate, averages);

        LOGGER.info("{} has finished processing ticker {}", getConsumerName(), tickerId);
    }

    private void calculateAnalysis(int tickerId, LocalDate asOfDate, Map<TimePeriod, StockPriceSummary> averages) {
        StockPrice currentStockPrice;
        try {
            Optional<StockPrice> foundStock = stockPriceAdminService.getCurrentPrice(tickerId);
            if ( !foundStock.isPresent()) {
                return;
            }
            currentStockPrice = foundStock.get();
        } catch (TickerNotFoundException tickerNotFoundEx) {
            LOGGER.warn("[{}] - Could not find the current stock price for ticker [{}]", getConsumerName(), tickerId);
            return;
        }
        calculateAnalysis(tickerId, asOfDate, currentStockPrice, averages);
    }

    private void calculateAnalysis(int tickerId, LocalDate asOfDate, StockPrice currentStockPrice, Map<TimePeriod, StockPriceSummary> averages) {

        Set<AnalyzerResult> analyzerResults = new HashSet<>();
        if ( averages.containsKey(TimePeriod.SevenDays)) {
            calculateAnalysisForSevenDays(analyzerResults, tickerId, asOfDate, currentStockPrice, averages.get(TimePeriod.SevenDays));
        }

        if ( !analyzerResults.isEmpty()) {
            try {
                tickerAnalyzersAdminService.saveNewAnalyzers(analyzerResults);
            } catch (ServiceException serEx) {
                LOGGER.error("Could not save all ticker [{}] analyzer results", analyzerResults.size(), serEx);
            }

        }

    }

    private void calculateAnalysisForSevenDays(Set<AnalyzerResult> analyzerResults, int tickerId, LocalDate asOfDate, StockPrice currentStockPrice, StockPriceSummary stockPriceSummary) {

        Analyzer analyzer = Analyzer.CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION;
        BigDecimal summaryDataPoint = stockPriceSummary.getCloseAvg();
        BigDecimal currentDataPoint = currentStockPrice.getClose();
        addAnalyzerEntry(analyzerResults, tickerId, asOfDate, analyzer, currentDataPoint, summaryDataPoint);

        analyzer = Analyzer.CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION;
        summaryDataPoint = stockPriceSummary.getHighAvg();
        currentDataPoint = currentStockPrice.getClose();
        addAnalyzerEntry(analyzerResults, tickerId, asOfDate, analyzer, currentDataPoint, summaryDataPoint);

        analyzer = Analyzer.CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_LOW_AVG_PERCENT_DEVIATION;
        summaryDataPoint = stockPriceSummary.getLowAvg();
        currentDataPoint = currentStockPrice.getClose();
        addAnalyzerEntry(analyzerResults, tickerId, asOfDate, analyzer, currentDataPoint, summaryDataPoint);

        analyzer = Analyzer.CURRENT_STOCK_VOL_COMPARED_TO_SEVEN_DAYS_PREVIOUS_VOL_AVG_PERCENT_DEVIATION;
        summaryDataPoint = BigDecimal.valueOf(stockPriceSummary.getVolumeAvg());
        currentDataPoint = BigDecimal.valueOf(currentStockPrice.getVolume());
        addAnalyzerEntry(analyzerResults, tickerId, asOfDate, analyzer, currentDataPoint, summaryDataPoint);

        analyzer = Analyzer.CURRENT_STOCK_VOL_COMPARED_TO_SEVEN_DAYS_PREVIOUS_VOL_HIGH_AVG_PERCENT_DEVIATION;
        summaryDataPoint = BigDecimal.valueOf(stockPriceSummary.getVolumeHighest());
        currentDataPoint = BigDecimal.valueOf(currentStockPrice.getVolume());
        addAnalyzerEntry(analyzerResults, tickerId, asOfDate, analyzer, currentDataPoint, summaryDataPoint);

        analyzer = Analyzer.CURRENT_STOCK_VOL_COMPARED_TO_SEVEN_DAYS_PREVIOUS_VOL_LOW_AVG_PERCENT_DEVIATION;
        summaryDataPoint = BigDecimal.valueOf(stockPriceSummary.getVolumeLowest());
        currentDataPoint = BigDecimal.valueOf(currentStockPrice.getVolume());
        addAnalyzerEntry(analyzerResults, tickerId, asOfDate, analyzer, currentDataPoint, summaryDataPoint);

    }

    private void addAnalyzerEntry(Set<AnalyzerResult> analyzerResults, int tickerId, LocalDate asOfDate,
                                  Analyzer analyzer, BigDecimal currentValue, BigDecimal summaryValue) {

        BigDecimal percentDeviation = currentValue.subtract(summaryValue).divide(summaryValue, RoundingMode.HALF_EVEN).multiply(ONE_HUNDERD);
        Optional<Double> weightOptional = analyzerService.getWeight(analyzer, percentDeviation);
        if ( weightOptional.isPresent()) {
            analyzerResults.add(new AnalyzerResult(tickerId, analyzer.getTitle(), weightOptional.get(), asOfDate));
        }
    }

    Map<TimePeriod, StockPriceSummary> calculateStockPriceAverages(List<StockPrice> stockPrices, LocalDate asOfDate) {
        // Order the stock prices
        TreeSet<StockPrice> orderedStockPrices = new TreeSet<>(stockPrices);
        // Get a mapping of time periods for all stock prices
        Map<TimePeriod, Set<StockPrice>> timePeriodBuckets = TimePeriod.groupDailyElementsIntoExactBuckets(orderedStockPrices);
        // Do the magic - look at all elements and on the fly calculate various summaries
        Map<TimePeriod, StockPriceSummary> timePeriodSummaries = timePeriodBuckets.entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream().map(p -> new TimePeriodToStockPrice(e.getKey(), p)))
                .collect(groupingBy(TimePeriodToStockPrice::getTimePeriod, Collectors.mapping(TimePeriodToStockPrice::getPrice, new StockPriceSummaryCollector(asOfDate))));

        return timePeriodSummaries;
    }

    /**
     * Inner class to hold the time period to stock price during the streaming - could be done a different way using more "groupingBy"
     * in the above stream, but it was getting messing - let's just use a container class
     */
    private static class TimePeriodToStockPrice {
        private TimePeriod timePeriod;
        private StockPrice price;

        private TimePeriodToStockPrice(TimePeriod timePeriod, StockPrice price) {
            this.timePeriod = timePeriod;
            this.price = price;
        }

        private TimePeriod getTimePeriod() {
            return timePeriod;
        }

        private StockPrice getPrice() {
            return price;
        }
    }

}
