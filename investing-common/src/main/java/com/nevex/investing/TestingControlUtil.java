package com.nevex.investing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/10/2017.
 */
public class TestingControlUtil {

    private static final Set<String> ALLOWED_TICKERS = new HashSet<>();

    public static void addAllowableTickers(Set<String> tickers) {
        ALLOWED_TICKERS.addAll(tickers);
    }

    public static boolean isTickerAllowed(String symbol) {
        return ALLOWED_TICKERS.isEmpty() || ALLOWED_TICKERS.contains(symbol);
    }

    public static List<String> getAllowedTickers(List<String> symbols) {
        return symbols.stream().filter(TestingControlUtil::isTickerAllowed).collect(Collectors.toList());
    }

}
