package com.nevex.investing.model;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * Created by Mark Cunningham on 9/7/2017.
 */
public enum TimePeriod {

    SevenDays("7_days", Period.ofDays(7)),
    OneMonth("1_month", Period.ofMonths(1)),
    ThreeMonths("3_months", Period.ofMonths(3)),
    SixMonths("6_months", Period.ofMonths(6)),
    OneYear("1_year", Period.ofYears(1));

    private String title;
    private int days;

    TimePeriod(String title, Period period) {
        int DEFAULT_DAYS_IN_MONTH = 31;
        int DEFAULT_DAYS_IN_YEAR = 365;
        this.title = title;
        this.days = (period.getYears() * DEFAULT_DAYS_IN_YEAR) + (period.getMonths() * DEFAULT_DAYS_IN_MONTH) + period.getDays();
    }

    public String getTitle() {
        return title;
    }

    public long getDays() {
        return days;
    }
}
