package com.nevex.investing.api;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Mark Cunningham on 9/13/2017.
 */
public interface ApiStockPriceClient {

    Optional<ApiStockPrice> getPriceForSymbol(String symbol) throws ApiException;

    Set<ApiStockPrice> getHistoricalPricesForSymbol(String symbol, int maxDaysToFetch) throws ApiException;

}
