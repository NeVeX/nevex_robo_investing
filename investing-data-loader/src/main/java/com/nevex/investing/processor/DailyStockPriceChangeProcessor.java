package com.nevex.investing.processor;

import com.nevex.investing.event.type.DailyStockPriceUpdateConsumer;
import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.processor.model.StockPriceSummary;
import com.nevex.investing.processor.model.StockPriceSummaryCollector;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.StockPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public class DailyStockPriceChangeProcessor implements DailyStockPriceUpdateConsumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(DailyStockPriceChangeProcessor.class);
    private final StockPriceAdminService stockPriceAdminService;
    private final TickerService tickerService;

    public DailyStockPriceChangeProcessor(StockPriceAdminService stockPriceAdminService, TickerService tickerService) {
        if ( stockPriceAdminService == null ) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        this.stockPriceAdminService = stockPriceAdminService;
        this.tickerService = tickerService;
    }

    @Override
    public void accept(Integer tickerId) {
        LOGGER.info("Received new ticker [{}] that has had it's stock price updated - will process it now", tickerId);

        try {
            List<StockPrice> stockPrices = stockPriceAdminService.getHistoricalPrices(tickerId, TimePeriod.OneYear.getDays());
            Map<TimePeriod, StockPriceSummary> averages = calculateStockPriceAverages(stockPrices);

            // Todo: remove
            averages.entrySet().stream().forEach( e -> LOGGER.info(e.getKey().getTitle()+": "+e.getValue().toString()));

        } catch (TickerNotFoundException tickerNotFound) {
            LOGGER.warn("Ticker Id [{}] is not valid - could not find it", tickerId);
        }
    }

    Map<TimePeriod, StockPriceSummary> calculateStockPriceAverages(List<StockPrice> stockPrices) {
        // Order the stock prices
        TreeSet<StockPrice> orderedStockPrices = new TreeSet<>(stockPrices);
        // Get a mapping of time periods for all stock prices
        Map<TimePeriod, Set<StockPrice>> timePeriodBuckets = TimePeriod.groupDailyElementsIntoExactBuckets(orderedStockPrices);
        // Do the magic - look at all elements and on the fly calculate various summaries
        Map<TimePeriod, StockPriceSummary> timePeriodSummaries = timePeriodBuckets.entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream().map(p -> new TimePeriodToStockPrice(e.getKey(), p)))
                .collect(groupingBy(TimePeriodToStockPrice::getTimePeriod, Collectors.mapping(TimePeriodToStockPrice::getPrice, new StockPriceSummaryCollector())));

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
