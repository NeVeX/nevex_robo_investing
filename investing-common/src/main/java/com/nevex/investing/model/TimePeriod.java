package com.nevex.investing.model;

import com.nevex.investing.util.DateUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/7/2017.
 */
public enum TimePeriod {

    OneDay("1-day", 1, 1, 0),
    FiveDays("5-days", 5, 7, 1),
    TenDays("10-days", 10, 14, 2),
    OneMonth("1-month", 20, 31, 4), // minimum weekdays (using February as minimum, and months that start on a Saturday that have 30 weekDays)
    ThreeMonths("3-months", 20 * 3, 31 * 3, 15),
    SixMonths("6-months", 20 * 6, 31 * 6, 35),
    OneYear("1-year", 260, 366, 70); // https://stackoverflow.com/questions/689282/what-is-the-maximum-number-of-weekdays-in-a-year-how-would-you-code-it
//    ThreeYears("3-years", 260 * 3, 366 * 3, 100),
//    FiveYears("5-years",  260 * 5, 366 * 5, 300);

    private String title;
    private int weekDays;
    private int days;
    private int bufferDays; // when grouping days together, there can be missing days (holidays), so we allow for a buffer

    TimePeriod(String title, int weekDays, int days, int bufferDays) {
        this.title = title;
        this.weekDays = weekDays;
        this.days = days;
        this.bufferDays = days + bufferDays;
    }

    public String getTitle() {
        return title;
    }

    public int getWeekDays() {
        return weekDays;
    }

    public int getDays() {
        return days;
    }

    public static TimePeriod getMaxTimePeriod() {
        return values()[values().length-1]; // hack
    }

    /**
     * Given an ordered collection, where each element represents a day, this will split the collection
     * into a map of each defined period.
     * If there is not enough elements to fill the time period, the time period is not returned
     */
    public static <T extends TimePeriodDate & Comparable<T>> Map<TimePeriod, Set<T>> groupDailyElementsIntoExactBuckets(Collection<T> collection) {

        Map<TimePeriod, Set<T>> periodBuckets = new HashMap<>();
        for (TimePeriod tp : values()) {
            periodBuckets.put(tp, new HashSet<>());
        }

        LocalDate startDate = null;
        for ( T data : collection) {

            if ( startDate == null ) {
                startDate = data.getDate();
            }

            long daysBetween = DateUtils.getDaysBetween(data.getDate(), startDate);

            for ( Map.Entry<TimePeriod, Set<T>> entry : periodBuckets.entrySet()) {

                TimePeriod timePeriod = entry.getKey();
                Set<T> values = entry.getValue();
                if ( daysBetween < timePeriod.bufferDays && values.size() < timePeriod.weekDays) {
                    entry.getValue().add(data);
                }
            }
        }

        // Remove any periods that do not have the expected number of elements
        return periodBuckets.entrySet().parallelStream()
                .filter(e -> e.getValue().size() == e.getKey().weekDays)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
