package com.nevex.investing.analyzer;

import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.type.StockPriceUpdatedEvent;
import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.model.StockPriceSummary;
import com.nevex.investing.analyzer.model.StockPriceSummaryCollector;
import com.nevex.investing.service.model.ServiceException;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.StockPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public class StockPriceChangeAnalyzer extends EventConsumer<StockPriceUpdatedEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(StockPriceChangeAnalyzer.class);
    private final StockPriceAdminService stockPriceAdminService;

    public StockPriceChangeAnalyzer(StockPriceAdminService stockPriceAdminService) {
        super(StockPriceUpdatedEvent.class);
        if ( stockPriceAdminService == null ) { throw new IllegalArgumentException("Provided stockPriceAdminService is null"); }
        this.stockPriceAdminService = stockPriceAdminService;
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
        try {
            List<StockPrice> stockPrices = stockPriceAdminService.getHistoricalPrices(tickerId, asOfDate, TimePeriod.OneYear.getDays());
            Map<TimePeriod, StockPriceSummary> averages = calculateStockPriceAverages(stockPrices, asOfDate);
            if ( averages == null || averages.isEmpty()) {
                LOGGER.info("Stock price change summary analyzer cannot summarize for ticker [{}], probably not enough data", tickerId);
                return;
            }
            for ( Map.Entry<TimePeriod, StockPriceSummary> entry : averages.entrySet()) {
                try {
                    stockPriceAdminService.savePriceChanges(tickerId, entry.getKey(), entry.getValue());
                } catch (ServiceException sEx) {
                    LOGGER.error("Could not save stock price changes for ticker [{}]", sEx);
                }
            }
        } catch (TickerNotFoundException tickerNotFound) {
            LOGGER.warn("Ticker Id [{}] is not valid - could not find it", tickerId);
        }
        LOGGER.info("{} has finished processing ticker {}", getConsumerName(), tickerId);
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
