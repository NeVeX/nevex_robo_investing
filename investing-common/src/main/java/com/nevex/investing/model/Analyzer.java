package com.nevex.investing.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/21/2017.
 */
public enum Analyzer {

    PRICE_TO_EARNINGS_RATIO("price-to-earnings-ratio"),
    EARNINGS_PER_SHARE("earnings-per-share"),
    ANALYZER_SUMMARY_COUNTER_ADJUST_WEIGHT("analyzer-summary-counter-adjust-weight"),


    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_DAY_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-1-day-previous-close-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_DAY_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-1-day-previous-high-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_DAY_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-1-day-previous-low-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_DAY_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-1-day-previous-lowest-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_DAY_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-1-day-previous-highest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_ONE_DAY_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-1-day-previous-lowest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_ONE_DAY_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-1-day-previous-low-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_ONE_DAY_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-1-day-previous-vol-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_ONE_DAY_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-1-day-previous-vol-high-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_ONE_DAY_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-1-day-previous-vol-low-percent-deviation"),

    CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-7-days-previous-close-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-7-days-previous-high-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-7-days-previous-low-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-7-days-previous-lowest-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-7-days-previous-highest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-7-days-previous-lowest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-7-days-previous-low-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_SEVEN_DAYS_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-7-days-previous-vol-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_SEVEN_DAYS_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-7-days-previous-vol-high-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_SEVEN_DAYS_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-7-days-previous-vol-low-percent-deviation");

    private final String title;

    Analyzer(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static Optional<Analyzer> fromTitle(String title) {
        if (StringUtils.isNotBlank(title)) {
            return Arrays.stream(values()).filter( v -> StringUtils.equalsIgnoreCase(v.getTitle(), title)).findFirst();
        }
        return Optional.empty();
    }

}
