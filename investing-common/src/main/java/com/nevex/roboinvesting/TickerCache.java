package com.nevex.roboinvesting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/17/2017.
 */
public final class TickerCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(TickerCache.class);
    private static final ConcurrentHashMap<String, Integer> TICKER_SYMBOL_TO_ID = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, String> TICKER_ID_TO_SYMBOL = new ConcurrentHashMap<>();

    public static Optional<Integer> getIdForSymbol(String symbol) {
        String ticker = symbol.toUpperCase();
        Integer tickerId = TICKER_SYMBOL_TO_ID.get(ticker);
        return Optional.ofNullable(tickerId);
    }

    public static Optional<String> getSymbolForId(int tickerId) {
        String tickerSymbol = TICKER_ID_TO_SYMBOL.get(tickerId);
        return Optional.ofNullable(tickerSymbol);
    }

    public static void update(Map<String, Integer> newSymbolsToIds) {
        int previousCount = TICKER_ID_TO_SYMBOL.size();
        Map<Integer, String> tickerIdToSymbols = newSymbolsToIds
                .entrySet()
                .stream().map( e -> new AbstractMap.SimpleEntry<>(e.getValue(), e.getKey()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        TICKER_SYMBOL_TO_ID.putAll(newSymbolsToIds);
        TICKER_ID_TO_SYMBOL.putAll(tickerIdToSymbols);
        LOGGER.info("Refreshed [{}] tickers into the cache from the previous size of [{}]", TICKER_ID_TO_SYMBOL.size(), previousCount);
    }
    
}
