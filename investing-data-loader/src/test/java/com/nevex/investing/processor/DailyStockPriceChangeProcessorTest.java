package com.nevex.investing.processor;

import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.processor.model.StockPriceSummary;
import com.nevex.investing.service.StockPriceAdminService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.model.StockPrice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Mark Cunningham on 9/7/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DailyStockPriceChangeProcessorTest {


    @Mock
    private StockPriceAdminService stockPriceAdminService;
    @Mock
    private TickerService tickerService;

    @Test
    public void testStockPriceAverageCalculations() {
        StockPriceSummary summary = new StockPriceSummary(BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.valueOf(10), BigDecimal.valueOf(100), Long.MAX_VALUE);
        List<StockPrice> oneYearStockPrices = generateStockPrices(TimePeriod.OneYear.getDays(), summary);
        DailyStockPriceChangeProcessor priceChangeProcessor = new DailyStockPriceChangeProcessor(stockPriceAdminService, tickerService);
        Map<TimePeriod, StockPriceSummary> results = priceChangeProcessor.calculateStockPriceAverages(oneYearStockPrices);
        assertThat(results.keySet().size()).isEqualTo(5); // we have 5 periods so far
        // Each period we get should be equal to the summary we created above (2+2+2+...) = 2 on average
        results.values().forEach(result -> assertThat(result).isEqualTo(summary));
    }

    @Test
    public void testStockPriceAverageCalculationsFor7DaysOnly() {
        StockPriceSummary summary = new StockPriceSummary(BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.valueOf(10), BigDecimal.valueOf(100), Long.MAX_VALUE);
        List<StockPrice> oneYearStockPrices = generateStockPrices(10, summary); // only 10 days
        DailyStockPriceChangeProcessor priceChangeProcessor = new DailyStockPriceChangeProcessor(stockPriceAdminService, tickerService);
        Map<TimePeriod, StockPriceSummary> results = priceChangeProcessor.calculateStockPriceAverages(oneYearStockPrices);
        assertThat(results.keySet().size()).isEqualTo(1); // we should only have one
        assertThat(results.get(TimePeriod.SevenDays)).isNotNull(); // should be a value
        results.values().forEach(result -> assertThat(result).isEqualTo(summary)); // test it's as we expect
    }

    private List<StockPrice> generateStockPrices(long loopMax, StockPriceSummary summary) {
        List<StockPrice> stockPrices = new ArrayList<>();
        LocalDate date = LocalDate.now();
        for (int i = 1; i <= loopMax; i++) {
            stockPrices.add(
                    new StockPrice("TEST", date.minusDays(i), summary.getOpenAvg(), summary.getHighAvg(), summary.getLowAvg(), summary.getCloseAvg(), summary.getVolumeAvg(), null, null, null, null, null, null, null)
            );
        }
        return stockPrices;
    }

}
