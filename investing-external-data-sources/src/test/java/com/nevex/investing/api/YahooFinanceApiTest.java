package com.nevex.investing.api;

import org.junit.Test;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Mark Cunningham on 9/5/2017.
 */
public class YahooFinanceApiTest {

    @Test
    public void test() throws Exception {

        Map<String, Stock> stocks = YahooFinance.get(new String[]{"MSFT", "123412313"});

//        BigDecimal price = stock.getQuote().getPrice();
//        BigDecimal change = stock.getQuote().getChangeInPercent();
//        BigDecimal peg = stock.getStats().getPeg();
//        BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();

        stocks.toString();


//        Calendar from = Calendar.getInstance();
//        Calendar to = Calendar.getInstance();
//        from.add(Calendar.YEAR, -1); // from 1 year ago
//
//        Stock google = YahooFinance.get("GOOG");
//        List<HistoricalQuote> googleHistQuotes = google.getHistory(from, to, Interval.DAILY);
//        googleHistQuotes.toString();


    }

}
