package com.nevex.roboinvesting.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.roboinvesting.service.model.PageableData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/17/2017.
 */
public class PageableDataDto<T> {

    @JsonProperty("data")
    private List<T> data = new ArrayList<>();
    @JsonProperty("page_number")
    private int pageNumber = 0;
    @JsonProperty("page_elements")
    private int pageElements = 0;
    @JsonProperty("total_pages")
    private int totalPages = 0;
    @JsonProperty("total_elements")
    private long totalElements = 0;

    public PageableDataDto() {  }

    public <E> PageableDataDto(PageableData<E> pageableData, Function<E, T> converter) {
        if ( pageableData == null ) { throw new IllegalArgumentException("PageableData is null"); }
        if ( !pageableData.hasData()) {
            return; // don't set null data or empty data later
        }
        this.data = pageableData.getData().stream().map(converter).collect(Collectors.toList());
        this.pageNumber = pageableData.getPageNumber();
        this.pageElements = pageableData.getPageElements();
        this.totalPages = pageableData.getTotalPages();
        this.totalElements = pageableData.getTotalElements();
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageElements() {
        return pageElements;
    }

    public void setPageElements(int pageElements) {
        this.pageElements = pageElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
