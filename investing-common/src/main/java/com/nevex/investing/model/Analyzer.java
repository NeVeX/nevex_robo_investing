package com.nevex.investing.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/21/2017.
 */
public enum Analyzer {

    PRICE_TO_EARNINGS_RATIO("price-to-earnings-ratio"),
    CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-seven-days-previous-close-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-seven-days-previous-high-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_SEVEN_DAYS_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-seven-days-previous-low-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_SEVEN_DAYS_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-seven-days-previous-vol-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_SEVEN_DAYS_PREVIOUS_VOL_HIGH_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-seven-days-previous-vol-high-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_SEVEN_DAYS_PREVIOUS_VOL_LOW_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-seven-days-previous-vol-low-average-percent-deviation"),
    ANALYZER_SUMMARY_COUNTER_ADJUST_WEIGHT("analyzer-summary-counter-adjust-weight");

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
