package com.nevex.investing.service.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
public enum StockExchange {

    Nasdaq((short)1),
    Nyse((short)2);

    private final short id;

    StockExchange(short id) {
        this.id = id;
    }

    public short getId() {
        return id;
    }

    /**
     * Using the id of the stock exchange, it will return the stock exchange; or null if none matches
     */
    public static Optional<StockExchange> fromId(short id) {
        return Optional.ofNullable(Arrays.stream(StockExchange.values()).filter(se -> se.id == id).findFirst().orElse(null));
    }
}
