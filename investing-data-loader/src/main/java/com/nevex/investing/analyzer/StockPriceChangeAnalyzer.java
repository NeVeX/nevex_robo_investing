package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.Event;
import com.nevex.investing.event.type.StockPriceUpdatedEvent;
import com.nevex.investing.event.type.TickerAnalyzerUpdatedEvent;
import com.nevex.investing.model.Analyzer;
import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.model.StockPriceSummary;
import com.nevex.investing.analyzer.model.StockPriceSummaryCollector;
import com.nevex.investing.service.TickerAnalyzersAdminService;
import com.nevex.investing.service.model.ServiceException;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.StockPrice;
import com.nevex.investing.service.model.Ticker;
import org.apache.commons.math3.stat.regression.SimpleRegression;
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
    private final AnalyzerServiceV2 analyzerService;
    private final TickerAnalyzersAdminService tickerAnalyzersAdminService;

    public StockPriceChangeAnalyzer(StockPriceAdminService stockPriceAdminService,
                                    AnalyzerServiceV2 analyzerService,
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
        return "stock-price-change-analyzer";
    }

    @Override
    public void onEvent(StockPriceUpdatedEvent event) {
        int tickerId = event.getTickerId();
        LocalDate asOfDate = event.getAsOfDate();
        Map<TimePeriod, Set<StockPrice>> timePeriodBuckets;
        Map<TimePeriod, StockPriceSummary> averages;
        List<StockPrice> stockPrices;
        try {
             stockPrices = stockPriceAdminService.getHistoricalPrices(tickerId, asOfDate, TimePeriod.ThreeYears.getDays());
        } catch (TickerNotFoundException tickerNotFound) {
            LOGGER.warn("Ticker Id [{}] is not valid - could not find it", tickerId);
            return;
        }

        // Order the stock prices
        TreeSet<StockPrice> orderedStockPrices = new TreeSet<>(stockPrices);
        // Get a mapping of time periods for all stock prices
        timePeriodBuckets = TimePeriod.groupDailyElementsIntoExactBuckets(orderedStockPrices);

        averages = calculateStockPriceAverages(timePeriodBuckets, asOfDate);

        if ( averages == null || averages.isEmpty()) {
            LOGGER.debug("Stock price change summary analyzer cannot summarize for ticker [{}], probably not enough data", tickerId);
            return;
        }

        if ( calculateAnalysis(tickerId, asOfDate, averages) ) {
            calculateSimpleRegressions(tickerId, asOfDate, timePeriodBuckets);
            calculateCurrentStockPriceAnalysis(tickerId, asOfDate, orderedStockPrices.first());
            EventManager.sendEvent(new TickerAnalyzerUpdatedEvent(tickerId, asOfDate));
        }

        LOGGER.debug("{} has finished processing ticker {}", getConsumerName(), tickerId);
    }

    private void calculateCurrentStockPriceAnalysis(int tickerId, LocalDate asOfDate, StockPrice currentStockPrice) {
        if ( currentStockPrice == null) { return; }

        Optional<Double> weightOpt = analyzerService.getWeight(Analyzer.CURRENT_STOCK_PRICE, currentStockPrice.getClose());
        if ( !weightOpt.isPresent()) {
            return;
        }
        // save this weight for the current stock price
        saveAnalyzer(new AnalyzerResult(tickerId, Analyzer.CURRENT_STOCK_PRICE.getTitle(), weightOpt.get(), asOfDate));
    }

    private void calculateSimpleRegressions(int tickerId, LocalDate asOfDate, Map<TimePeriod, Set<StockPrice>> timePeriodBuckets) {
        Set<AnalyzerResult> analyzerResults = new HashSet<>();

        try {
            for ( Map.Entry<TimePeriod, Set<StockPrice>> prices : timePeriodBuckets.entrySet()) {
                if (prices.getKey().getDays() < 3) {
                    continue;
                }
                SimpleRegression simpleRegressionClosePrice = new SimpleRegression();
                SimpleRegression simpleRegressionVolume = new SimpleRegression();
                int counter = 1;
                for (StockPrice price : prices.getValue()) {
                    if (price.getClose() != null) {
                        simpleRegressionClosePrice.addData(counter, price.getClose().doubleValue());
                    }
                    simpleRegressionVolume.addData(counter, price.getVolume());
                    counter++;
                }
                String prefix = prices.getKey().getTitle();
                addResultForSimpleRegression(analyzerResults, tickerId, prefix+"-close-price-simple-regression-r", simpleRegressionClosePrice.getR(), asOfDate);
                addResultForSimpleRegression(analyzerResults, tickerId, prefix+"-close-price-simple-regression-slope", simpleRegressionClosePrice.getSlope(), asOfDate);
                addResultForSimpleRegression(analyzerResults, tickerId, prefix+"-volume-simple-regression-r", simpleRegressionVolume.getR(), asOfDate);
                addResultForSimpleRegression(analyzerResults, tickerId, prefix+"-volume-simple-regression-slope", simpleRegressionVolume.getSlope(), asOfDate);
            }
        } catch (Exception e ) {
            LOGGER.error("Could not calculate simple regression analysis for tickerId [{}]. Reason: {}", tickerId, e.getMessage());
            return;
        }

        if ( !analyzerResults.isEmpty()) {
            saveAnalyzers(analyzerResults);
        } else {
            LOGGER.debug("No simple regressions were calculated for ticker [{}] for date [{}]", tickerId, asOfDate);
        }
    }

    private void addResultForSimpleRegression(Set<AnalyzerResult> analyzerResults, int tickerId, String title, double value, LocalDate asOfDate) {
        if ( Double.valueOf(value).isNaN()) {
            return;
        }
        Optional<Analyzer> analyzerOptional = Analyzer.fromTitle(title);
        if ( !analyzerOptional.isPresent()) {
            LOGGER.warn("Could not find the analyzer for name [{}]", title);
            return;
        }
        Optional<Double> weightOpt = analyzerService.getWeight(analyzerOptional.get(), BigDecimal.valueOf(value));
        if ( weightOpt.isPresent()) {
            analyzerResults.add(new AnalyzerResult(tickerId, title, weightOpt.get(), asOfDate));
        } else {
            LOGGER.warn("Could not get weight for analyzer [{}] with value [{}]", title, value);
        }
    }

    private boolean calculateAnalysis(int tickerId, LocalDate asOfDate, Map<TimePeriod, StockPriceSummary> averages) {
        StockPrice currentStockPrice;
        try {
            Optional<StockPrice> foundStock = stockPriceAdminService.getCurrentPrice(tickerId);
            if ( !foundStock.isPresent()) {
                return false;
            }
            currentStockPrice = foundStock.get();
        } catch (TickerNotFoundException tickerNotFoundEx) {
            LOGGER.warn("[{}] - Could not find the current stock price for ticker [{}]", getConsumerName(), tickerId);
            return false;
        }
        return calculateAnalysis(tickerId, asOfDate, currentStockPrice, averages);
    }

    private boolean calculateAnalysis(int tickerId, LocalDate asOfDate, StockPrice currentStockPrice, Map<TimePeriod, StockPriceSummary> averages) {

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
            return saveAnalyzers(analyzerResults);
        } else {
            LOGGER.info("No price analyzer results were calculated for ticker id [{}] for date [{}]", tickerId, asOfDate);
        }
        return false;
    }

    private boolean saveAnalyzers(Set<AnalyzerResult> analyzerResults) {
        try {
            tickerAnalyzersAdminService.saveNewAnalyzers(analyzerResults);
            return true;
        } catch (ServiceException serEx) {
            LOGGER.error("Could not save all ticker [{}] analyzer results", analyzerResults.size(), serEx);
            return false;
        }
    }

    private boolean saveAnalyzer(AnalyzerResult analyzerResult) {
        try {
            tickerAnalyzersAdminService.saveNewAnalyzer(analyzerResult);
            return true;
        } catch (ServiceException serEx) {
            LOGGER.error("Could not save single ticker [{}] analyzer result", analyzerResult, serEx);
            return false;
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
        BigDecimal percentDeviation;
        if ( summaryValue.compareTo(BigDecimal.ZERO) == 0) {
            percentDeviation = value;
        } else {
            percentDeviation = value.subtract(summaryValue).divide(summaryValue, RoundingMode.HALF_EVEN);
        }
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

    Map<TimePeriod, StockPriceSummary> calculateStockPriceAverages(Map<TimePeriod, Set<StockPrice>> timePeriodBuckets, LocalDate asOfDate) {
        // Do the magic - look at all elements and on the fly calculateWeight various summaries
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
