package com.nevex.roboinvesting.dataloader;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
class DataLoaderOrder {

    static final int REFERENCE_DATA_LOADER = 1;
    static final int TICKER_SYMBOL_LOADER = REFERENCE_DATA_LOADER + 1;
    static final int STOCK_PRICE_HISTORICAL_LOADER = TICKER_SYMBOL_LOADER + 1;
    static final int STOCK_PRICE_CURRENT_LOADER = STOCK_PRICE_HISTORICAL_LOADER + 1;

}
