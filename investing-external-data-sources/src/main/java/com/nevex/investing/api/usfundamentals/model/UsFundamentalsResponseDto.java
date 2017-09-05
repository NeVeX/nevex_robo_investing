package com.nevex.investing.api.usfundamentals.model;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class UsFundamentalsResponseDto {

    private final long cik;
    private final Set<UsFundamentalIndicatorDto> quarterlyIndicators = new TreeSet<>();
    private final Set<UsFundamentalIndicatorDto> yearlyIndicators = new TreeSet<>();

    public UsFundamentalsResponseDto(long cik) {
        this.cik = cik;
    }

    public long getCik() {
        return cik;
    }

    public void addQuarterlyIndicator(UsFundamentalIndicatorDto usFundamentalIndicatorDto) {
        quarterlyIndicators.add(usFundamentalIndicatorDto);
    }

    public void addYearlyIndicator(UsFundamentalIndicatorDto usFundamentalIndicatorDto) {
        yearlyIndicators.add(usFundamentalIndicatorDto);
    }

    public Set<UsFundamentalIndicatorDto> getQuarterlyIndicators() {
        return quarterlyIndicators;
    }

    public Set<UsFundamentalIndicatorDto> getYearlyIndicators() {
        return yearlyIndicators;
    }
}


