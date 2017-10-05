package com.nevex.investing.util;

import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 10/3/2017.
 */
public class DateUtils {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Given two dates, a and b, this will return if a is one weekday before b.
     * Weekends do not count, and holidays are not catered for here either.
     */
    public static boolean isDateAWeekdayPrevious(LocalDate previousDate, LocalDate futureDate) {

        if ( !isAWeekday(previousDate) ) { return false; }

        if ( previousDate.isBefore(futureDate)) {

            LocalDate weekDayPrevious = getPreviousWeekDateIfNotAWeekDate(previousDate);

            if ( futureDate.getDayOfWeek() == DayOfWeek.MONDAY && weekDayPrevious.until(futureDate).getDays() == 3) {
                return true; // a weekend is between them
            }

            if ( weekDayPrevious.until(futureDate).getDays() == 1) {
                return true; // one weekday between them
            }
        }
        return false;
    }

    /**
     * Given a date, this will get the previous weekday, if the given date is not a weekdate already
     */
    public static LocalDate getPreviousWeekDateIfNotAWeekDate(LocalDate date) {
        if ( isAWeekday(date)) { return date; }
        return getPreviousWeekDate(date);
    }

    public static boolean isAWeekday(LocalDate date) {
        return !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    public static LocalDate getPreviousWeekDate(LocalDate date) {
        LocalDate previousDate = date;
        do {
            previousDate = previousDate.minusDays(1);
        } while (!isAWeekday(previousDate));
        return previousDate;
    }

    public static long getDaysBetween(LocalDate one, LocalDate two) {
        return ChronoUnit.DAYS.between(one, two);
    }

    public static Optional<LocalDate> tryGetDate(String date) {
        if (StringUtils.isNotBlank(date)) {
            try {
                return Optional.of(LocalDate.parse(date, DATE_FORMATTER));
            } catch (Exception ignore) { }
        }
        return Optional.empty();
    }

//    public static long getWeekDaysBetween(LocalDate one, LocalDate two) {
//        long daysBetween = ChronoUnit.DAYS.between(one, two);
//        long weeksBetween = daysBetween / 7;
//        weeksBetween = weeksBetween == 0 ? 1 : weeksBetween;
//        long daysRemainder = daysBetween % weeksBetween;
//        long extraDays = daysRemainder <= 1 ? daysRemainder : 2; // cap it at 2
//        return (daysBetween / weeksBetween) + extraDays;
//    }

}
