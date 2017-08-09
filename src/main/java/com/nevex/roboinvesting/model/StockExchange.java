package com.nevex.roboinvesting.model;

import java.util.Arrays;

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
    public static StockExchange fromId(short id) {
        return Arrays.stream(StockExchange.values()).filter(se -> se.id == id).findFirst().orElse(null);
    }
}
