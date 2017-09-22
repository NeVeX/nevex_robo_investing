package com.nevex.investing.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/21/2017.
 */
public enum Analyzer {

    PRICE_TO_EARNINGS_RATIO("price-to-earnings-ratio"),
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
