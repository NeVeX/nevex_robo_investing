package com.nevex.investing.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Mark Cunningham on 9/13/2017.
 */
public interface ApiStockPriceClient {

    Optional<ApiStockPrice> getPriceForSymbol(String symbol) throws ApiException;

    Map<String, Optional<ApiStockPrice>> getPriceForSymbols(List<String> symbols) throws ApiException;

    Set<ApiStockPrice> getHistoricalPricesForSymbol(String symbol, LocalDate asOfDate, int maxDaysToFetch) throws ApiException;

    Map<String, Set<ApiStockPrice>> getHistoricalPricesForSymbols(List<String> symbols, LocalDate asOfDate, int maxDaysToFetch) throws ApiException;

}
