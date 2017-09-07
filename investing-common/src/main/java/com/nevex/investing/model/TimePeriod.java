package com.nevex.investing.model;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * Created by Mark Cunningham on 9/7/2017.
 */
public enum TimePeriod {

    SevenDays("7_days", 7, ChronoUnit.DAYS),
    OneMonth("1_month", 1, ChronoUnit.MONTHS),
    ThreeMonths("3_months", 3, ChronoUnit.MONTHS),
    SixMonths("6_months", 6, ChronoUnit.MONTHS),
    OneYear("1_year", 1, ChronoUnit.YEARS);

    private String title;
    private long days;

    TimePeriod(String title, long amount, TemporalUnit temporalUnit) {
        this.title = title;
        days = Duration.of(amount, temporalUnit).get(ChronoUnit.DAYS);
    }

    public String getTitle() {
        return title;
    }

    public long getDays() {
        return days;
    }
}
