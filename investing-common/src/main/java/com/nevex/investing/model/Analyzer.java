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
    PRICE_EARNINGS_TO_GROWTH_RATIO("price-earnings-to-growth_ratio"),
    PRICE_TO_BOOK_RATIO("price-to-book-ratio"),
    PRICE_TO_SALES_RATIO("price-to-sales-ratio"),
    RETURN_ON_EQUITY("return-on-equity"),
    ANALYZER_SUMMARY_COUNTER_ADJUST_WEIGHT("analyzer-summary-counter-adjust-weight"),
    CURRENT_STOCK_PRICE("current-stock-price"),

    // 1 day
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

    // 5 days
    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_DAYS_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-5-days-previous-close-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_DAYS_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-5-days-previous-high-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_DAYS_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-5-days-previous-low-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_DAYS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-5-days-previous-lowest-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_DAYS_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-5-days-previous-highest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_FIVE_DAYS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-5-days-previous-lowest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_FIVE_DAYS_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-5-days-previous-low-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_FIVE_DAYS_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-5-days-previous-vol-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_FIVE_DAYS_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-5-days-previous-vol-high-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_FIVE_DAYS_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-5-days-previous-vol-low-percent-deviation"),
    FIVE_DAYS_CLOSE_PRICE_SIMPLE_REGRESSION_R("5-days-close-price-simple-regression-r"),
    FIVE_DAYS_CLOSE_PRICE_SIMPLE_REGRESSION_SLOPE("5-days-close-price-simple-regression-slope"),
    FIVE_DAYS_VOLUME_SIMPLE_REGRESSION_R("5-days-volume-simple-regression-r"),
    FIVE_DAYS_VOLUME_SIMPLE_REGRESSION_SLOPE("5-days-volume-simple-regression-slope"),

    // 10 days
    CURRENT_STOCK_PRICE_COMPARED_TO_TEN_DAYS_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-10-days-previous-close-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_TEN_DAYS_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-10-days-previous-high-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_TEN_DAYS_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-10-days-previous-low-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_TEN_DAYS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-10-days-previous-lowest-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_TEN_DAYS_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-10-days-previous-highest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_TEN_DAYS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-10-days-previous-lowest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_TEN_DAYS_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-10-days-previous-low-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_TEN_DAYS_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-10-days-previous-vol-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_TEN_DAYS_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-10-days-previous-vol-high-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_TEN_DAYS_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-10-days-previous-vol-low-percent-deviation"),
    TEN_DAYS_CLOSE_PRICE_SIMPLE_REGRESSION_R("10-days-close-price-simple-regression-r"),
    TEN_DAYS_CLOSE_PRICE_SIMPLE_REGRESSION_SLOPE("10-days-close-price-simple-regression-slope"),
    TEN_DAYS_VOLUME_SIMPLE_REGRESSION_R("10-days-volume-simple-regression-r"),
    TEN_DAYS_VOLUME_SIMPLE_REGRESSION_SLOPE("10-days-volume-simple-regression-slope"),

    // 1 month
    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_MONTH_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-1-month-previous-close-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_MONTH_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-1-month-previous-high-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_MONTH_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-1-month-previous-low-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_MONTH_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-1-month-previous-lowest-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_MONTH_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-1-month-previous-highest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_ONE_MONTH_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-1-month-previous-lowest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_ONE_MONTH_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-1-month-previous-low-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_ONE_MONTH_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-1-month-previous-vol-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_ONE_MONTH_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-1-month-previous-vol-high-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_ONE_MONTH_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-1-month-previous-vol-low-percent-deviation"),
    ONE_MONTHS_CLOSE_PRICE_SIMPLE_REGRESSION_R("1-month-close-price-simple-regression-r"),
    ONE_MONTHS_CLOSE_PRICE_SIMPLE_REGRESSION_SLOPE("1-month-close-price-simple-regression-slope"),
    ONE_MONTHS_VOLUME_SIMPLE_REGRESSION_R("1-month-volume-simple-regression-r"),
    ONE_MONTHS_VOLUME_SIMPLE_REGRESSION_SLOPE("1-month-volume-simple-regression-slope"),

    // 3 months
    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_MONTHS_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-3-months-previous-close-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_MONTHS_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-3-months-previous-high-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_MONTHS_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-3-months-previous-low-average-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_MONTHS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-3-months-previous-lowest-percent-deviation"),
    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_MONTHS_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-3-months-previous-highest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_THREE_MONTHS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-3-months-previous-lowest-percent-deviation"),
    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_THREE_MONTHS_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-3-months-previous-low-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_THREE_MONTHS_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-3-months-previous-vol-average-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_THREE_MONTHS_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-3-months-previous-vol-high-percent-deviation"),
    CURRENT_STOCK_VOL_COMPARED_TO_THREE_MONTHS_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-3-months-previous-vol-low-percent-deviation"),
    THREE_MONTHS_CLOSE_PRICE_SIMPLE_REGRESSION_R("3-months-close-price-simple-regression-r"),
    THREE_MONTHS_CLOSE_PRICE_SIMPLE_REGRESSION_SLOPE("3-months-close-price-simple-regression-slope"),
    THREE_MONTHS_VOLUME_SIMPLE_REGRESSION_R("3-months-volume-simple-regression-r"),
    THREE_MONTHS_VOLUME_SIMPLE_REGRESSION_SLOPE("3-months-volume-simple-regression-slope");
//
//    // 6 months
//    CURRENT_STOCK_PRICE_COMPARED_TO_SIX_MONTHS_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-6-months-previous-close-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_SIX_MONTHS_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-6-months-previous-high-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_SIX_MONTHS_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-6-months-previous-low-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_SIX_MONTHS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-6-months-previous-lowest-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_SIX_MONTHS_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-6-months-previous-highest-percent-deviation"),
//    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_SIX_MONTHS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-6-months-previous-lowest-percent-deviation"),
//    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_SIX_MONTHS_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-6-months-previous-low-average-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_SIX_MONTHS_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-6-months-previous-vol-average-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_SIX_MONTHS_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-6-months-previous-vol-high-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_SIX_MONTHS_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-6-months-previous-vol-low-percent-deviation"),
//    SIX_MONTHS_CLOSE_PRICE_SIMPLE_REGRESSION_R("6-months-close-price-simple-regression-r"),
//    SIX_MONTHS_CLOSE_PRICE_SIMPLE_REGRESSION_SLOPE("6-months-close-price-simple-regression-slope"),
//    SIX_MONTHS_VOLUME_SIMPLE_REGRESSION_R("6-months-volume-simple-regression-r"),
//    SIX_MONTHS_VOLUME_SIMPLE_REGRESSION_SLOPE("6-months-volume-simple-regression-slope"),
//
//    // 1 year
//    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_YEAR_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-1-year-previous-close-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_YEAR_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-1-year-previous-high-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_YEAR_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-1-year-previous-low-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_YEAR_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-1-year-previous-lowest-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_ONE_YEAR_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-1-year-previous-highest-percent-deviation"),
//    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_ONE_YEAR_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-1-year-previous-lowest-percent-deviation"),
//    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_ONE_YEAR_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-1-year-previous-low-average-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_ONE_YEAR_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-1-year-previous-vol-average-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_ONE_YEAR_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-1-year-previous-vol-high-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_ONE_YEAR_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-1-year-previous-vol-low-percent-deviation"),
//    ONE_YEAR_CLOSE_PRICE_SIMPLE_REGRESSION_R("1-year-close-price-simple-regression-r"),
//    ONE_YEAR_CLOSE_PRICE_SIMPLE_REGRESSION_SLOPE("1-year-close-price-simple-regression-slope"),
//    ONE_YEAR_VOLUME_SIMPLE_REGRESSION_R("1-year-volume-simple-regression-r"),
//    ONE_YEAR_VOLUME_SIMPLE_REGRESSION_SLOPE("1-year-volume-simple-regression-slope"),
//
//    // 3 years
//    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_YEARS_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-3-years-previous-close-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_YEARS_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-3-years-previous-high-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_YEARS_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-3-years-previous-low-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_YEARS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-3-years-previous-lowest-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_THREE_YEARS_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-3-years-previous-highest-percent-deviation"),
//    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_THREE_YEARS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-3-years-previous-lowest-percent-deviation"),
//    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_THREE_YEARS_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-3-years-previous-low-average-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_THREE_YEARS_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-3-years-previous-vol-average-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_THREE_YEARS_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-3-years-previous-vol-high-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_THREE_YEARS_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-3-years-previous-vol-low-percent-deviation"),
//    THREE_YEARS_CLOSE_PRICE_SIMPLE_REGRESSION_R("3-years-close-price-simple-regression-r"),
//    THREE_YEARS_CLOSE_PRICE_SIMPLE_REGRESSION_SLOPE("3-years-close-price-simple-regression-slope"),
//    THREE_YEARS_VOLUME_SIMPLE_REGRESSION_R("3-years-volume-simple-regression-r"),
//    THREE_YEARS_VOLUME_SIMPLE_REGRESSION_SLOPE("3-years-volume-simple-regression-slope"),
//
//    // 5 years
//    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_YEARS_PREVIOUS_CLOSE_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-5-years-previous-close-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_YEARS_PREVIOUS_HIGH_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-5-years-previous-high-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_YEARS_PREVIOUS_LOW_AVG_PERCENT_DEVIATION("current-stock-price-compared-to-5-years-previous-low-average-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_YEARS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-stock-price-compared-to-5-years-previous-lowest-percent-deviation"),
//    CURRENT_STOCK_PRICE_COMPARED_TO_FIVE_YEARS_PREVIOUS_HIGHEST_PERCENT_DEVIATION("current-stock-price-compared-to-5-years-previous-highest-percent-deviation"),
//    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_FIVE_YEARS_PREVIOUS_LOWEST_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-5-years-previous-lowest-percent-deviation"),
//    CURRENT_LOWEST_STOCK_PRICE_COMPARED_TO_FIVE_YEARS_PREVIOUS_LOWEST_AVG_PERCENT_DEVIATION("current-lowest-stock-price-compared-to-5-years-previous-low-average-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_FIVE_YEARS_PREVIOUS_VOL_AVG_PERCENT_DEVIATION("current-stock-vol-compared-to-5-years-previous-vol-average-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_FIVE_YEARS_PREVIOUS_VOL_HIGH_PERCENT_DEVIATION("current-stock-vol-compared-to-5-years-previous-vol-high-percent-deviation"),
//    CURRENT_STOCK_VOL_COMPARED_TO_FIVE_YEARS_PREVIOUS_VOL_LOW_PERCENT_DEVIATION("current-stock-vol-compared-to-5-years-previous-vol-low-percent-deviation"),
//    FIVE_YEARS_CLOSE_PRICE_SIMPLE_REGRESSION_R("5-years-close-price-simple-regression-r"),
//    FIVE_YEARS_CLOSE_PRICE_SIMPLE_REGRESSION_SLOPE("5-years-close-price-simple-regression-slope"),
//    FIVE_YEARS_VOLUME_SIMPLE_REGRESSION_R("5-years-volume-simple-regression-r"),
//    FIVE_YEARS_VOLUME_SIMPLE_REGRESSION_SLOPE("5-years-volume-simple-regression-slope");

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
