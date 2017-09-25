package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.event.EventConsumer;
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

    private final static String PERCENT_DEVIATION = "percent-deviation";
    private final static String COMPARED_TO = "compared-to";
    private final static String CURRENT_STOCK_PRICE = "current-stock-price";
    private final static String CURRENT_LOWEST_STOCK_PRICE = "current-lowest-stock-price";
    private final static String CURRENT_STOCK_VOLUME = "current-stock-vol";
    private final static String PREVIOUS_CLOSE_AVG = "previous-close-average";
    private final static String PREVIOUS_HIGH_AVG = "previous-high-average";
    private final static String PREVIOUS_LOW_AVG = "previous-low-average";
    private final static String PREVIOUS_HIGHEST = "previous-highest";
    private final static String PREVIOUS_LOWEST = "previous-lowest";
    private final static String PREVIOUS_VOL_LOW = "previous-vol-low";
    private final static String PREVIOUS_VOL_HIGH = "previous-vol-high";
    private final static String PREVIOUS_VOL_AVG = "previous-vol-average";
    private final StockPriceAdminService stockPriceAdminService;
    private final AnalyzerService analyzerService;
    private final TickerAnalyzersAdminService tickerAnalyzersAdminService;

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
    public int getOrder() {
        return AnalyzerOrder.STOCK_PRICE_CHANGE_ANALYZER;
    }

    @Override
    public String getConsumerName() {
        return "stock-price-change-summary-analyzer";
    }

    @Override
    public void onEvent(StockPriceUpdatedEvent event) {
        int tickerId = event.getTickerId();
        LocalDate asOfDate = event.getAsOfDate();
//        LOGGER.info("Received new ticker [{}] that has had it's stock price updated - will process it now", tickerId);
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

        Set<TimePeriodAnalyzerResult> timePeriodAnalyzerResults = new HashSet<>();

        for ( TimePeriod timePeriod : averages.keySet()) {
            StockPriceSummary stockPriceSummary = averages.get(timePeriod);

            timePeriodAnalyzerResults.addAll(getStockPriceDeviations(timePeriod, stockPriceSummary, currentStockPrice));
            timePeriodAnalyzerResults.addAll(getStockVolumeDeviations(timePeriod, stockPriceSummary, currentStockPrice));
        }

        Set<AnalyzerResult> analyzerResults = timePeriodAnalyzerResults.stream()
                .map(period -> new AnalyzerResult(tickerId, period.analyzer.getTitle(), period.weight, asOfDate))
                .collect(Collectors.toSet());

        if ( !analyzerResults.isEmpty()) {
            try {
                tickerAnalyzersAdminService.saveNewAnalyzers(analyzerResults);
            } catch (ServiceException serEx) {
                LOGGER.error("Could not save all ticker [{}] analyzer results", analyzerResults.size(), serEx);
            }
        } else {
            LOGGER.info("No price analyzer results were calculated for ticker id [{}] for date [{}]", tickerId, asOfDate);
        }
    }

    private Set<TimePeriodAnalyzerResult> getStockPriceDeviations(TimePeriod timePeriod, StockPriceSummary stockPriceSummary, StockPrice currentStockPrice) {
        Set<TimePeriodAnalyzerResult> results = new HashSet<>();

        BigDecimal currentPrice = currentStockPrice.getClose();
        TimePeriodAnalyzerResult result = getAnalyzerWeight(timePeriod, CURRENT_STOCK_PRICE, PREVIOUS_CLOSE_AVG, currentPrice, stockPriceSummary.getCloseAvg());
        if ( result != null ) { results.add(result); }

        result = getAnalyzerWeight(timePeriod, CURRENT_STOCK_PRICE, PREVIOUS_HIGH_AVG, currentPrice, stockPriceSummary.getHighAvg());
        if ( result != null ) { results.add(result); }

        result = getAnalyzerWeight(timePeriod, CURRENT_STOCK_PRICE,PREVIOUS_LOW_AVG, currentPrice, stockPriceSummary.getLowAvg());
        if ( result != null ) { results.add(result); }

        result = getAnalyzerWeight(timePeriod, CURRENT_STOCK_PRICE,PREVIOUS_LOWEST, currentPrice, stockPriceSummary.getLowest());
        if ( result != null ) { results.add(result); }

        result = getAnalyzerWeight(timePeriod, CURRENT_STOCK_PRICE, PREVIOUS_HIGHEST, currentPrice, stockPriceSummary.getHighest());
        if ( result != null ) { results.add(result); }

        result = getAnalyzerWeight(timePeriod, CURRENT_LOWEST_STOCK_PRICE, PREVIOUS_LOWEST, currentStockPrice.getLow(), stockPriceSummary.getLowest());
        if ( result != null ) { results.add(result); }

        result = getAnalyzerWeight(timePeriod, CURRENT_LOWEST_STOCK_PRICE, PREVIOUS_LOW_AVG, currentStockPrice.getLow(), stockPriceSummary.getLowAvg());
        if ( result != null ) { results.add(result); }

        return results;
    }

    private Set<TimePeriodAnalyzerResult> getStockVolumeDeviations(TimePeriod timePeriod, StockPriceSummary stockPriceSummary, StockPrice currentStockPrices) {
        Set<TimePeriodAnalyzerResult> results = new HashSet<>();
        BigDecimal currentVolume = BigDecimal.valueOf(currentStockPrices.getVolume());

        TimePeriodAnalyzerResult result = getAnalyzerWeight(timePeriod, CURRENT_STOCK_VOLUME, PREVIOUS_VOL_AVG, currentVolume, BigDecimal.valueOf(stockPriceSummary.getVolumeAvg()));
        if ( result != null ) { results.add(result); }

        result = getAnalyzerWeight(timePeriod, CURRENT_STOCK_VOLUME, PREVIOUS_VOL_LOW, currentVolume, BigDecimal.valueOf(stockPriceSummary.getVolumeLowest()));
        if ( result != null ) { results.add(result); }

        result = getAnalyzerWeight(timePeriod, CURRENT_STOCK_VOLUME, PREVIOUS_VOL_HIGH, currentVolume, BigDecimal.valueOf(stockPriceSummary.getVolumeHighest()));
        if ( result != null ) { results.add(result); }

        return results;
    }

    private TimePeriodAnalyzerResult getAnalyzerWeight(TimePeriod timePeriod, String preFix, String comparisonName, BigDecimal value, BigDecimal summaryValue) {
        String dynamicAnalyzerTitle = getAnalyzerTitle(preFix, timePeriod, comparisonName, PERCENT_DEVIATION);
        BigDecimal percentDeviation = value.subtract(summaryValue).divide(summaryValue, RoundingMode.HALF_EVEN);
        return getWeightForDeviation(dynamicAnalyzerTitle, percentDeviation);
    }

    private String getAnalyzerTitle(String prefix, TimePeriod timePeriod, String comparisonName, String postFix) {
        return prefix+"-"+COMPARED_TO+"-"+timePeriod.getTitle()+"-"+comparisonName+"-"+postFix;
    }

    private TimePeriodAnalyzerResult getWeightForDeviation(String dynamicAnalyzerTitle, BigDecimal percentDeviation) {
        Optional<Analyzer> analyzerOpt = Analyzer.fromTitle(dynamicAnalyzerTitle);
        if (!analyzerOpt.isPresent()) {
            LOGGER.warn("Could not get analyzer for dynamic analyzer name [{}]", dynamicAnalyzerTitle);
            return null;

        }
        Analyzer analyzer = analyzerOpt.get();
        Optional<Double> weightOptional = analyzerService.getWeight(analyzer, percentDeviation);
        if ( weightOptional.isPresent()) {
            return new TimePeriodAnalyzerResult(analyzer, weightOptional.get());
        }
        return null;
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

    private static class TimePeriodAnalyzerResult {
        private Analyzer analyzer;
        private double weight;

        private TimePeriodAnalyzerResult(Analyzer analyzer, double weight) {
            this.analyzer = analyzer;
            this.weight = weight;
        }
    }


}
