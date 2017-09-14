package com.nevex.investing.api;

import org.junit.Test;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mark Cunningham on 9/5/2017.
 */
public class YahooFinanceApiTest {

    @Test
    public void test() throws Exception {

        Stock stock = YahooFinance.get("MSFT");

        BigDecimal price = stock.getQuote().getPrice();
        BigDecimal change = stock.getQuote().getChangeInPercent();
        BigDecimal peg = stock.getStats().getPeg();
        BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();

        stock.print();



//        Calendar from = Calendar.getInstance();
//        Calendar to = Calendar.getInstance();
//        from.add(Calendar.YEAR, -1); // from 1 year ago
//
//        Stock google = YahooFinance.get("GOOG");
//        List<HistoricalQuote> googleHistQuotes = google.getHistory(from, to, Interval.DAILY);
//        googleHistQuotes.toString();


    }

}
