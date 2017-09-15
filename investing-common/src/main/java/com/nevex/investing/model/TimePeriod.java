package com.nevex.investing.model;

import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

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

    public int getDays() {
        return days;
    }

    /**
     * Given an ordered collection, where each element represents a day, this will split the collection
     * into a map of each defined period.
     * If there is not enough elements to fill the time period, the time period is not returned
     */
    public static <T extends Comparable<T>> Map<TimePeriod, Set<T>> groupDailyElementsIntoExactBuckets(Collection<T> collection) {

        Map<TimePeriod, Set<T>> periodBuckets = new HashMap<>();
        periodBuckets.put(TimePeriod.SevenDays, new HashSet<>());
        periodBuckets.put(TimePeriod.OneMonth, new HashSet<>());
        periodBuckets.put(TimePeriod.ThreeMonths, new HashSet<>());
        periodBuckets.put(TimePeriod.SixMonths, new HashSet<>());
        periodBuckets.put(TimePeriod.OneYear, new HashSet<>());

        int counter = 0;
        for ( T data : collection) {
            if ( counter < TimePeriod.SevenDays.getDays()) { periodBuckets.get(TimePeriod.SevenDays).add(data); }
            if ( counter < TimePeriod.OneMonth.getDays()) { periodBuckets.get(TimePeriod.OneMonth).add(data); }
            if ( counter < TimePeriod.ThreeMonths.getDays() ) { periodBuckets.get(TimePeriod.ThreeMonths).add(data); }
            if ( counter < TimePeriod.SixMonths.getDays()) { periodBuckets.get(TimePeriod.SixMonths).add(data); }
            if ( counter < TimePeriod.OneYear.getDays()) { periodBuckets.get(TimePeriod.OneYear).add(data); }
            counter++;
        }

        // Remove any periods that do not have the expected number of elements
        return periodBuckets.entrySet().stream().filter(e -> e.getKey().getDays() == e.getValue().size()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
