package com.nevex.investing.api.yahoo;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.ApiStockPrice;
import com.nevex.investing.api.ApiStockPriceClient;
import com.nevex.investing.api.yahoo.model.YahooStockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/5/2017.
 */
public class YahooApiClient implements ApiStockPriceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(YahooApiClient.class);

    public List<YahooStockInfo> getYahooStockInfo(List<String> symbols) throws ApiException {
        try {
            Map<String, Stock> stockMap = YahooFinance.get(symbols.toArray(new String[symbols.size()]));
            if ( stockMap != null ) {
                return stockMap.values().stream().filter(Stock::isValid).map(YahooStockInfo::new).collect(Collectors.toList());
            }
            return new ArrayList<>();
        } catch (Exception e ) {
            throw new ApiException("Could not get yahoo stock info for list ["+symbols+"]", e);
        }
    }

    @Override
    public Optional<ApiStockPrice> getPriceForSymbol(String symbol) throws ApiException {
        Map<String, Optional<ApiStockPrice>> yahooStockPrices = getPriceForSymbols(Collections.singletonList(symbol));
        if ( yahooStockPrices.containsKey(symbol)) {
            return yahooStockPrices.get(symbol);
        }
        return Optional.empty();
    }

    @Override
    public Map<String, Optional<ApiStockPrice>> getPriceForSymbols(List<String> symbols) throws ApiException {

        try {
            Map<String, Stock> results = YahooFinance.get(symbols.toArray(new String[symbols.size()]));
            Map<String, Optional<ApiStockPrice>> parsedResults = new HashMap<>();

            for ( Map.Entry<String, Stock> entry : results.entrySet()) {

                if ( entry.getValue() == null || !entry.getValue().isValid()) {
                    LOGGER.warn("Skipping over daily stock price for ticker [{}] since the data returned is not valid", entry.getKey());
                    continue;
                }

                ApiStockPrice stockPrice = convertToApiStockPrice(entry.getValue().getQuote());
                parsedResults.put(entry.getKey(), Optional.ofNullable(stockPrice));
            }
            return parsedResults;
        } catch (Exception e ) {
            throw new ApiException("Could not get current stock prices from Yahoo for symbols ["+symbols+"]", e);
        }
    }

    @Override
    public Set<ApiStockPrice> getHistoricalPricesForSymbol(String symbol, LocalDate asOfDate, int maxDaysToFetch) throws ApiException {
        Map<String, Set<ApiStockPrice>> historicals = getHistoricalPricesForSymbols(Collections.singletonList(symbol), asOfDate, maxDaysToFetch, true);
        if ( historicals.containsKey(symbol) ) {
            return historicals.get(symbol);
        }
        return new HashSet<>();
    }

    @Override
    public Map<String, Set<ApiStockPrice>> getHistoricalPricesForSymbols(List<String> symbols, LocalDate asOfDate, int maxDaysToFetch) throws ApiException {
        return getHistoricalPricesForSymbols(symbols, asOfDate, maxDaysToFetch, true);
    }

    private Map<String, Set<ApiStockPrice>> getHistoricalPricesForSymbols(List<String> symbols, LocalDate asOfDate, int maxDaysToFetch, boolean includeHistoryInitially) throws ApiException {

        LOGGER.warn("Use of the Yahoo historical price function is flaky - so it may fail...");

        Calendar to = GregorianCalendar.from(asOfDate.atStartOfDay(ZoneId.systemDefault()));
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DAY_OF_MONTH, -maxDaysToFetch); // go back the required days

        try {
            Map<String, Stock> results = YahooFinance.get(symbols.toArray(new String[symbols.size()]), includeHistoryInitially);
            Map<String, Set<ApiStockPrice>> symbolToPrices = new HashMap<>();
            for ( Map.Entry<String, Stock> entry : results.entrySet()) {
                symbolToPrices.put(entry.getKey(), convertToApiStockPrices(entry.getValue().getHistory(from, to, Interval.DAILY)));
            }
            return symbolToPrices;
        } catch (Exception e) {
            throw new ApiException("Could not get historical stock prices from Yahoo for symbols ["+symbols+"]", e);
        }
    }

    private Set<ApiStockPrice> convertToApiStockPrices(List<HistoricalQuote> historicalQuotes) throws ApiException {
        Set<ApiStockPrice> prices = new HashSet<>();
        for ( HistoricalQuote quote : historicalQuotes) {
            ApiStockPrice price = convertToApiStockPrice(quote);
            if ( price != null) {
                prices.add(price);
            }
        }
        return prices;
    }

    private ApiStockPrice convertToApiStockPrice(StockQuote quote) throws ApiException {
        try {
            return ApiStockPrice.builder()
                    .withClose(quote.getPrice()) // close is the current price. TODO: Need to make sure this is ok
//                    .withAdjustedClose(stock.getQuote().getA)
                    // TODO: need to make sure this is ok
                    .withDate(quote.getLastTradeTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .withHigh(quote.getDayHigh())
                    .withOpen(quote.getOpen())
                    .withVolume(quote.getVolume())
                    .withLow(quote.getDayLow())
                    .build();
        } catch (Exception e ) {
            LOGGER.error("Could not convert historical yahoo quote into "+ApiStockPrice.class.getSimpleName(), e);
            return null;
        }
    }

    private ApiStockPrice convertToApiStockPrice(HistoricalQuote historicalQuote) throws ApiException {
        try {
            return ApiStockPrice.builder()
                    .withClose(historicalQuote.getClose())
                    .withAdjustedClose(historicalQuote.getAdjClose())
                    .withDate(historicalQuote.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .withHigh(historicalQuote.getHigh())
                    .withOpen(historicalQuote.getOpen())
                    .withVolume(historicalQuote.getVolume())
                    .withLow(historicalQuote.getLow())
                    .build();
        } catch (Exception e ) {
            LOGGER.error("Could not convert historical yahoo quote into "+ApiStockPrice.class.getSimpleName(), e);
            return null;
        }
    }
}
