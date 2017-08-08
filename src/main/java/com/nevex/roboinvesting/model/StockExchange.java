package com.nevex.roboinvesting.model;

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

}
