package com.nevex.investing.api.yahoo;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.yahoo.model.YahooStockInfo;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/5/2017.
 */
public class YahooApiClient {

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

}
