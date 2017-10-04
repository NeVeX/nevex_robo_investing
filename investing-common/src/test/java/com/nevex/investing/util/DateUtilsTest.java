package com.nevex.investing.util;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Mark Cunningham on 10/3/2017.
 */
public class DateUtilsTest {

    @Test
    public void makeSureIsWeekdayBetweenWorks() {
        LocalDate dateOne = LocalDate.of(2010, 5, 10); // A Monday
        LocalDate dateTwo = LocalDate.of(2010, 5, 11); // A Tuesday

        // Monday vs Tuesday
        assertThat(DateUtils.isDateAWeekdayPrevious(dateOne, dateTwo)).isTrue();

        dateOne = dateOne.minusDays(1); // Sunday vs Tuesday -- false
        assertThat(DateUtils.isDateAWeekdayPrevious(dateOne, dateTwo)).isFalse();

        dateTwo = dateTwo.minusDays(1); // Sunday vs Monday -- false
        assertThat(DateUtils.isDateAWeekdayPrevious(dateOne, dateTwo)).isFalse();

        dateOne = dateOne.minusDays(2); // Friday vs Monday -- true
        assertThat(DateUtils.isDateAWeekdayPrevious(dateOne, dateTwo)).isTrue();

        dateTwo = dateTwo.minusDays(1); // Friday vs Sunday -- false
        assertThat(DateUtils.isDateAWeekdayPrevious(dateOne, dateTwo)).isFalse();

        dateTwo = dateTwo.plusDays(2); // Friday vs Tuesday -- false
        assertThat(DateUtils.isDateAWeekdayPrevious(dateOne, dateTwo)).isFalse();
    }

    @Test
    public void makeSurePreviousWeekDayIfNotWeekDate() {
        LocalDate date = LocalDate.of(2010, 5, 10); // A Monday
        assertThat(DateUtils.getPreviousWeekDateIfNotAWeekDate(date)).isEqualTo(date); // same date should return
        date = LocalDate.of(2010, 5, 9); // A Sunday
        assertThat(DateUtils.getPreviousWeekDateIfNotAWeekDate(date)).isNotEqualTo(date); // different date
        assertThat(DateUtils.getPreviousWeekDateIfNotAWeekDate(date).getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
        date = LocalDate.of(2010, 5, 8); // A Saturday
        assertThat(DateUtils.getPreviousWeekDateIfNotAWeekDate(date)).isNotEqualTo(date); // different date
        assertThat(DateUtils.getPreviousWeekDateIfNotAWeekDate(date).getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
    }

    @Test
    public void daysBetweenIsCorrect() {
        LocalDate dateOne = LocalDate.of(2010, 5, 10);
        LocalDate dateTwo = LocalDate.of(2010, 5, 11);

        assertThat(DateUtils.getDaysBetween(dateOne, dateTwo)).isEqualTo(1);

        dateTwo = LocalDate.of(2010, 5, 20);
        assertThat(DateUtils.getDaysBetween(dateOne, dateTwo)).isEqualTo(10);

        dateTwo = LocalDate.of(2010, 6, 20);
        assertThat(DateUtils.getDaysBetween(dateOne, dateTwo)).isEqualTo(41);

    }

//    @Test
//    public void weekdaysBetweenIsCorrect() {
//        LocalDate dateOne = LocalDate.of(2010, 5, 10);
//        LocalDate dateTwo = LocalDate.of(2010, 5, 11);
//        assertThat(DateUtils.getWeekDaysBetween(dateOne, dateTwo)).isEqualTo(1);
//    }

}
