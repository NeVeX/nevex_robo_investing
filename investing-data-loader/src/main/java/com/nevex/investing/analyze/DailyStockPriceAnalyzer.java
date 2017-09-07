package com.nevex.investing.analyze;

import com.nevex.investing.event.type.DailyStockPriceUpdateConsumer;
import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.model.StockPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public class DailyStockPriceAnalyzer implements DailyStockPriceUpdateConsumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(DailyStockPriceAnalyzer.class);
    private final StockPriceAdminService stockPriceAdminService;
    private final TickerService tickerService;

    public DailyStockPriceAnalyzer(StockPriceAdminService stockPriceAdminService, TickerService tickerService) {
        if ( stockPriceAdminService == null ) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        this.stockPriceAdminService = stockPriceAdminService;
        this.tickerService = tickerService;
    }

    @Override
    public void accept(Integer tickerId) {
        LOGGER.info("Received new ticker [{}] that has had it's stock price updated - will analyze it now", tickerId);

//        try {
//            List<StockPrice> stockPrices = stockPriceAdminService.getHistoricalPrices(tickerId, 365);
//            analyzeStockPrices(stockPrices);
//        } catch (TickerNotFoundException tickerNotFound) {
//            LOGGER.warn("Ticker Id [{}] is not valid - could not find it", tickerId);
//        }
    }

    private void analyzeStockPrices(List<StockPrice> stockPrices) {

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

        Map<TimePeriod, Set<StockPrice>> calculatedAverages = new HashMap<>();
        if ( lastSeven.size() == TimePeriod.SevenDays.getDays()) { calculatedAverages.put(TimePeriod.SevenDays, calculateAverages(lastSeven)); }

    }

    private Set<StockPrice> calculateAverages(TreeSet<StockPrice> prices) {
//        prices.stream()
//                .filter(sp -> sp != null)
//                .map(StockPriceAveragesCalculations::new)
//                .reduce((x,y)->)
        return null;
    }


}
