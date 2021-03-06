package com.nevex.investing.model;

import com.nevex.investing.util.DateUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Mark Cunningham on 10/3/2017.
 */
public class TimePeriodTest {

    @Test
    public void makeSurePeriodGroupingWorks() {
        List<MyPeriod> periods = getWeekDayPeriodsForDays(TimePeriod.OneDay.getDays());
        LocalDate fromDate = periods.get(0).getDate();
        Map<TimePeriod, Set<MyPeriod>> result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(1);

        periods = getWeekDayPeriodsForDays(TimePeriod.FiveDays.getDays());
        fromDate = periods.get(0).getDate();
        result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(2);

        periods = getWeekDayPeriodsForDays(TimePeriod.TenDays.getDays());
        fromDate = periods.get(0).getDate();
        result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(3);

        periods = getWeekDayPeriodsForDays(TimePeriod.OneMonth.getDays());
        fromDate = periods.get(0).getDate();
        result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(4);

        periods = getWeekDayPeriodsForDays(TimePeriod.ThreeMonths.getDays());
        fromDate = periods.get(0).getDate();
        result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(5);

        periods = getWeekDayPeriodsForDays(TimePeriod.SixMonths.getDays());
        fromDate = periods.get(0).getDate();
        result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(6);

        periods = getWeekDayPeriodsForDays(TimePeriod.OneYear.getDays());
        fromDate = periods.get(0).getDate();
        result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(7);

//        periods = getWeekDayPeriodsForDays(TimePeriod.ThreeYears.getDays());
//        result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(periods);
//        assertThat(result.size()).isEqualTo(8);

        // Take some days out and see the effect
        periods = getWeekDayPeriodsForDays(TimePeriod.TenDays.getDays());
        periods.remove(0);
        periods.remove(6);
        fromDate = periods.get(0).getDate();
        result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void makeSureGapsInWeekdayDatesIsOk() {
        // Add a weekday gap of two days
        List<MyPeriod> periods = new ArrayList<>();
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 29)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 28)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 27)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 25))); // missing 26th

        periods.add(new MyPeriod(LocalDate.of(2017, 9, 22)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 21)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 19)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 18))); // missing some days

        periods.add(new MyPeriod(LocalDate.of(2017, 9, 15)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 14)));
        LocalDate fromDate = periods.get(0).getDate();
        // So even though there are missing days above, we allow for a buffer (see the time period)
        Map<TimePeriod, Set<MyPeriod>> result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(3); // all 3 should be grouped
    }

    @Test
    public void makeSureFromDateUsageWorks() {
        // Add a weekday gap of two days
        List<MyPeriod> periods = new ArrayList<>();
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 29)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 28)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 27)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 26)));
        periods.add(new MyPeriod(LocalDate.of(2017, 9, 25)));
        LocalDate fromDate = periods.get(0).getDate().plusDays(10); // this should make the buckets below, not work

        Map<TimePeriod, Set<MyPeriod>> result = TimePeriod.groupDailyElementsIntoExactBucketsFromDate(fromDate, periods);
        assertThat(result.size()).isEqualTo(0); // the date is out of range
    }

    private List<MyPeriod> getWeekDayPeriodsForDays(int number) {
        LocalDate startDate = LocalDate.of(2017, 9, 29);
        return IntStream.range(0, number)
                .boxed()
                .map(i -> new MyPeriod(startDate.minusDays(i)))
                .filter(p -> DateUtils.isAWeekday(p.getDate()))
                .collect(Collectors.toList());
    }

    private static class MyPeriod implements TimePeriodDate, Comparable<MyPeriod> {

        private final LocalDate date;
        private MyPeriod(LocalDate date) {
            this.date = date;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        @Override
        public int compareTo(MyPeriod o) {
            return -(date.compareTo(o.getDate()));
        }
    }


}
