package com.nevex.investing.api.yahoo;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.ApiStockPrice;
import com.nevex.investing.api.ApiStockPriceClient;
import com.nevex.investing.api.yahoo.model.YahooStockInfo;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/5/2017.
 */
public class YahooApiClient implements ApiStockPriceClient {

    public List<YahooStockInfo> getYahooStockInfo(List<String> symbols) throws ApiException {
        try {
            Map<String, Stock> stockMap = YahooFinance.get(symbols.toArray(new String[symbols.size()]));
            if ( stockMap != null ) {
                return stockMap.values().stream().map(YahooStockInfo::new).collect(Collectors.toList());
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
        Map<String, Set<ApiStockPrice>> yahooStockPrices = getHistoricalPricesForSymbols(symbols, 1, false);
        Map<String, Optional<ApiStockPrice>> prices = new HashMap<>();
        for ( Map.Entry<String, Set<ApiStockPrice>> entry : yahooStockPrices.entrySet()) {
            prices.put(entry.getKey(), ApiStockPrice.getLatestPrice(entry.getValue()));
        }
        return prices;
    }

    @Override
    public Set<ApiStockPrice> getHistoricalPricesForSymbol(String symbol, int maxDaysToFetch) throws ApiException {
        Map<String, Set<ApiStockPrice>> historicals = getHistoricalPricesForSymbols(Collections.singletonList(symbol), maxDaysToFetch, true);
        if ( historicals.containsKey(symbol) ) {
            return historicals.get(symbol);
        }
        return new HashSet<>();
    }

    @Override
    public Map<String, Set<ApiStockPrice>> getHistoricalPricesForSymbols(List<String> symbols, int maxDaysToFetch) throws ApiException {
        return getHistoricalPricesForSymbols(symbols, maxDaysToFetch, true);
    }

    private Map<String, Set<ApiStockPrice>> getHistoricalPricesForSymbols(List<String> symbols, int maxDaysToFetch, boolean includeHistoryInitially) throws ApiException {
        Calendar to = Calendar.getInstance();
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
            throw new ApiException("Could not get stock prices for symbols ["+symbols+"] using Yahoo", e);
        }
    }

    private Set<ApiStockPrice> convertToApiStockPrices(List<HistoricalQuote> historicalQuotes) throws ApiException {
        Set<ApiStockPrice> prices = new HashSet<>();
        for ( HistoricalQuote quote : historicalQuotes) {
            prices.add(convertToApiStockPrice(quote));
        }
        return prices;
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
            throw new ApiException("Could not convert historical yahoo quote into "+ApiStockPrice.class.getSimpleName(), e);
        }
    }
}
