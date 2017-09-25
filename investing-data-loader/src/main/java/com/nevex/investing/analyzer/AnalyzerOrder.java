package com.nevex.investing.analyzer;

/**
 * Created by Mark Cunningham on 9/25/2017.
 */
class AnalyzerOrder {

    static final int STOCK_PRICE_CHANGE_ANALYZER = 1;
    static final int STOCK_FINANCIALS_ANALYZER = STOCK_PRICE_CHANGE_ANALYZER + 1;
    static final int ALL_ANALYZERS_SUMMARY_ANALYZER = STOCK_FINANCIALS_ANALYZER + 1000;

}
