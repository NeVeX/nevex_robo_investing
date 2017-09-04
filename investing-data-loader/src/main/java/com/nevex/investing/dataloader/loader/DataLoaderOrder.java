package com.nevex.investing.dataloader.loader;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
class DataLoaderOrder {

    static final int REFERENCE_DATA_LOADER = 1;
    static final int TICKER_SYMBOL_LOADER = REFERENCE_DATA_LOADER + 1;
    static final int TICKER_CACHE_LOADER = TICKER_SYMBOL_LOADER + 1;
    static final int STOCK_PRICE_HISTORICAL_LOADER = TICKER_CACHE_LOADER + 1;
    static final int STOCK_PRICE_CURRENT_LOADER = STOCK_PRICE_HISTORICAL_LOADER + 1;

    static final int TICKER_TO_CIK_LOADER = STOCK_PRICE_CURRENT_LOADER + 1;
    static final int TICKER_FUNDAMENTALS_LOADER = TICKER_TO_CIK_LOADER + 1;

}
