package com.nevex.investing.analyzer;

import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.model.StockPriceSummary;
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
public class StockPriceChangeAnalyzerTest {


    @Mock
    private StockPriceAdminService stockPriceAdminService;
    @Mock
    private TickerService tickerService;

    @Test
    public void testStockPriceAverageCalculations() {
        StockPriceSummary summary = new StockPriceSummary(BigDecimal.valueOf(2), BigDecimal.valueOf(2), BigDecimal.valueOf(2),
                BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(5), BigDecimal.valueOf(5),
                BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100),
                Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
        List<StockPrice> oneYearStockPrices = generateStockPrices(TimePeriod.OneYear.getDays(), summary);
        StockPriceChangeAnalyzer priceChangeProcessor = new StockPriceChangeAnalyzer(stockPriceAdminService);
        Map<TimePeriod, StockPriceSummary> results = priceChangeProcessor.calculateStockPriceAverages(oneYearStockPrices);
        assertThat(results.keySet().size()).isEqualTo(5); // we have 5 periods so far
        // Each period we get should be equal to the summary we created above (2+2+2+...) = 2 on average
        results.values().forEach(result -> assertThat(result).isEqualTo(summary));
    }

    @Test
    public void testStockPriceAverageCalculationsFor7DaysOnly() {
        StockPriceSummary summary = new StockPriceSummary(BigDecimal.valueOf(2), BigDecimal.valueOf(2), BigDecimal.valueOf(2),
                BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(5), BigDecimal.valueOf(5),
                BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100),
                Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
        List<StockPrice> oneYearStockPrices = generateStockPrices(10, summary); // only 10 days
        StockPriceChangeAnalyzer priceChangeProcessor = new StockPriceChangeAnalyzer(stockPriceAdminService);
        Map<TimePeriod, StockPriceSummary> results = priceChangeProcessor.calculateStockPriceAverages(oneYearStockPrices);
        assertThat(results.keySet().size()).isEqualTo(1); // we should only have one
        assertThat(results.get(TimePeriod.SevenDays)).isNotNull(); // should be a value
        results.values().forEach(result -> assertThat(result).isEqualTo(summary)); // test it's as we expect
    }

    @Test
    public void testStockPriceAverageCalculationsForHighsAndLows() {

        // Create different prices for different days (at least 7 days)
        List<StockPrice> stockPrices = new ArrayList<>();                    // open                 // high                 //low                  // close             // vol
        stockPrices.add(new StockPrice("TEST", LocalDate.now().minusDays(6), BigDecimal.valueOf(1), BigDecimal.valueOf(5), BigDecimal.valueOf(2), BigDecimal.valueOf(3), 100, null, null, null, null, null, null, null));
        stockPrices.add(new StockPrice("TEST", LocalDate.now().minusDays(5), BigDecimal.valueOf(2), BigDecimal.valueOf(6), BigDecimal.valueOf(3), BigDecimal.valueOf(4), 200, null, null, null, null, null, null, null));
        stockPrices.add(new StockPrice("TEST", LocalDate.now().minusDays(4), BigDecimal.valueOf(3), BigDecimal.valueOf(7), BigDecimal.valueOf(4), BigDecimal.valueOf(5), 300, null, null, null, null, null, null, null));
        stockPrices.add(new StockPrice("TEST", LocalDate.now().minusDays(3), BigDecimal.valueOf(4), BigDecimal.valueOf(8), BigDecimal.valueOf(5), BigDecimal.valueOf(6), 400, null, null, null, null, null, null, null));
        stockPrices.add(new StockPrice("TEST", LocalDate.now().minusDays(2), BigDecimal.valueOf(5), BigDecimal.valueOf(9), BigDecimal.valueOf(6), BigDecimal.valueOf(7), 500, null, null, null, null, null, null, null));
        stockPrices.add(new StockPrice("TEST", LocalDate.now().minusDays(1), BigDecimal.valueOf(6), BigDecimal.valueOf(10), BigDecimal.valueOf(7), BigDecimal.valueOf(8), 600, null, null, null, null, null, null, null));
        stockPrices.add(new StockPrice("TEST", LocalDate.now(),              BigDecimal.valueOf(7), BigDecimal.valueOf(11), BigDecimal.valueOf(8), BigDecimal.valueOf(9), 700, null, null, null, null, null, null, null));

        StockPriceChangeAnalyzer priceChangeProcessor = new StockPriceChangeAnalyzer(stockPriceAdminService);
        Map<TimePeriod, StockPriceSummary> results = priceChangeProcessor.calculateStockPriceAverages(stockPrices);
        assertThat(results.keySet().size()).isEqualTo(1); // we should only have one
        assertThat(results.get(TimePeriod.SevenDays)).isNotNull(); // should be a value

        StockPriceSummary sevenDaySummary = results.get(TimePeriod.SevenDays);
        assertThat(sevenDaySummary.getLowest()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(sevenDaySummary.getHighest()).isEqualTo(BigDecimal.valueOf(11));
        assertThat(sevenDaySummary.getOpenLowest()).isEqualTo(BigDecimal.valueOf(1));
        assertThat(sevenDaySummary.getOpenHighest()).isEqualTo(BigDecimal.valueOf(7));
        assertThat(sevenDaySummary.getCloseLowest()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(sevenDaySummary.getCloseHighest()).isEqualTo(BigDecimal.valueOf(9));
        assertThat(sevenDaySummary.getVolumeLowest()).isEqualTo(100);
        assertThat(sevenDaySummary.getVolumeHighest()).isEqualTo(700);

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
