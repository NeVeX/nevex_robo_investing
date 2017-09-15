package com.nevex.investing.service.model;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Cunningham on 8/17/2017.
 */
public final class PageableData<T> {

    private final List<T> data;
    private final int pageNumber;
    private final int pageElements;
    private final int totalPages;
    private final long totalElements;
    private final boolean hasData;

    private PageableData() {
        this.data = new ArrayList<>();
        this.pageNumber = 0;
        this.pageElements = 0;
        this.totalPages = 0;
        this.totalElements = 0;
        hasData = false;
    }

    public PageableData(List<T> data, Page page) {
        if ( data == null ) { throw new IllegalArgumentException("Data cannot be null"); }
        if ( page == null ) { throw new IllegalArgumentException("Page cannot be null"); }
        this.data = data;
        this.pageNumber = page.getNumber();
        this.pageElements = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.hasData = !this.data.isEmpty();
    }

    public static <T> PageableData<T> empty() {
        return new PageableData<>();
    }

    public List<T> getData() {
        return data;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageElements() {
        return pageElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public boolean hasData() {
        return hasData;
    }
}
