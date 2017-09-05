package com.nevex.investing.api.usfundamentals.model;

import java.util.*;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class UsFundamentalsResponse {

    private final long cik;
    private final long downloadStartTimeNanoseconds;
    private final Set<UsFundamentalIndicator> quarterlyIndicators = new TreeSet<>();
    private final Set<UsFundamentalIndicator> yearlyIndicators = new TreeSet<>();

    public UsFundamentalsResponse(long cik, long downloadStartTimeNanoseconds) {
        this.cik = cik;
        this.downloadStartTimeNanoseconds = downloadStartTimeNanoseconds;
    }

    public long getCik() {
        return cik;
    }

    public long getDownloadStartTimeNanoseconds() {
        return downloadStartTimeNanoseconds;
    }

    public void addQuarterlyIndicator(UsFundamentalIndicator usFundamentalIndicator) {
        quarterlyIndicators.add(usFundamentalIndicator);
    }

    public void addYearlyIndicator(UsFundamentalIndicator usFundamentalIndicator) {
        yearlyIndicators.add(usFundamentalIndicator);
    }

    public Set<UsFundamentalIndicator> getQuarterlyIndicators() {
        return quarterlyIndicators;
    }

    public Set<UsFundamentalIndicator> getYearlyIndicators() {
        return yearlyIndicators;
    }
}


