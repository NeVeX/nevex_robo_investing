package com.nevex.investing.api;

import org.junit.Test;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.math.BigDecimal;

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
    }

}
