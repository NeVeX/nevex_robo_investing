package com.nevex.investing.processor;

import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.processor.model.StockPriceAverages;
import com.nevex.investing.service.model.StockPrice;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mark Cunningham on 9/7/2017.
 */
public class DailyStockPriceChangeProcessorTest {

    @Test
    public void testStockPriceAverageCalculations() {
        List<StockPrice> stockPrices = new ArrayList<>();
        LocalDate date = LocalDate.now();
        for ( int i = 0; i < 100; i++) {
            stockPrices.add(
                new StockPrice("TEST", date.minusDays(i), BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, 100L, null, null, null, null, null, null, null)
            );
        }

        DailyStockPriceChangeProcessor priceChangeProcessor = new DailyStockPriceChangeProcessor();

        Map<TimePeriod, StockPriceAverages.Result> results = priceChangeProcessor.calculateStockPriceAverages(stockPrices);
        results.toString();
    }

}
