package com.nevex.investing.processor;

import com.nevex.investing.event.type.DailyStockPriceUpdateConsumer;
import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.processor.model.StockPriceAverages;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.model.StockPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public class DailyStockPriceChangeProcessor implements DailyStockPriceUpdateConsumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(DailyStockPriceChangeProcessor.class);
//    private final StockPriceAdminService stockPriceAdminService;
//    private final TickerService tickerService;

//    public DailyStockPriceChangeProcessor(StockPriceAdminService stockPriceAdminService, TickerService tickerService) {
//        if ( stockPriceAdminService == null ) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
//        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
//        this.stockPriceAdminService = stockPriceAdminService;
//        this.tickerService = tickerService;
//    }

    @Override
    public void accept(Integer tickerId) {
        LOGGER.info("Received new ticker [{}] that has had it's stock price updated - will processor it now", tickerId);

//        try {
//            List<StockPrice> stockPrices = stockPriceAdminService.getHistoricalPrices(tickerId, 365);
//            analyzeStockPrices(stockPrices);
//        } catch (TickerNotFoundException tickerNotFound) {
//            LOGGER.warn("Ticker Id [{}] is not valid - could not find it", tickerId);
//        }
    }

    Map<TimePeriod, StockPriceAverages.Result> calculateStockPriceAverages(List<StockPrice> stockPrices) {

        TreeSet<StockPrice> orderedStockPrices = new TreeSet<>(stockPrices);
        TreeSet<StockPrice> lastSeven = new TreeSet<>();
        TreeSet<StockPrice> lastMonth = new TreeSet<>();
        TreeSet<StockPrice> lastThreeMonths = new TreeSet<>();
        TreeSet<StockPrice> lastSixMonths = new TreeSet<>();
        TreeSet<StockPrice> lastYear = new TreeSet<>();

        int counter = 0;
        for ( StockPrice stockPrice : orderedStockPrices) {
            if ( counter < TimePeriod.SevenDays.getDays()) { lastSeven.add(stockPrice); }
            if ( counter < TimePeriod.OneMonth.getDays()) { lastMonth.add(stockPrice); }
            if ( counter < TimePeriod.ThreeMonths.getDays() ) { lastThreeMonths.add(stockPrice); }
            if ( counter < TimePeriod.SixMonths.getDays()) { lastSixMonths.add(stockPrice); }
            if ( counter < TimePeriod.OneYear.getDays()) { lastYear.add(stockPrice); }
            counter++;
        }

        Map<TimePeriod, StockPriceAverages.Result> calculatedAverages = new HashMap<>();
        addToMap(calculatedAverages, TimePeriod.SevenDays, lastSeven);
        addToMap(calculatedAverages, TimePeriod.OneMonth, lastMonth);
        addToMap(calculatedAverages, TimePeriod.ThreeMonths, lastThreeMonths);
        addToMap(calculatedAverages, TimePeriod.SixMonths, lastSixMonths);
        addToMap(calculatedAverages, TimePeriod.OneYear, lastYear);
        return calculatedAverages;
    }

    private void addToMap(Map<TimePeriod, StockPriceAverages.Result> calculatedAverages, TimePeriod timePeriod, Set<StockPrice> prices) {
        Map.Entry<TimePeriod, StockPriceAverages.Result> result = calculateAverages(timePeriod, prices);
        if ( result != null ) {
            calculatedAverages.put(result.getKey(), result.getValue());
        }
    }

    private Map.Entry<TimePeriod, StockPriceAverages.Result> calculateAverages(TimePeriod timePeriod, Set<StockPrice> prices) {
        if ( prices.size() == timePeriod.getDays()) {
            StockPriceAverages.Result averagesResult = calculateAverages(prices);
            if ( averagesResult != null ) {
                return new AbstractMap.SimpleEntry<>(timePeriod, averagesResult);
            }
        }
        return null;
    }

    private StockPriceAverages.Result calculateAverages(Set<StockPrice> prices) {
        final StockPriceAverages.Calculator calc = new StockPriceAverages.Calculator();
        Optional<StockPriceAverages.Result> averagesResult = prices.stream()
                .map(StockPriceAverages::new)
                .reduce(calc::add)
                .map(calc::calcAverages);
        return averagesResult.isPresent() ? averagesResult.get() : null;
    }

}
