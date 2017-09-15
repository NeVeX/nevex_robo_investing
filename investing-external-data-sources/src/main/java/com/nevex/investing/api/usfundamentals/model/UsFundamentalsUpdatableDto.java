package com.nevex.investing.api.usfundamentals.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class UsFundamentalsUpdatableDto {

    @JsonProperty("update_id")
    private String updateId;
    @JsonProperty("company_id")
    private Long companyId;
    @JsonProperty("period")
    private LocalDate periodDate;

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public LocalDate getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(LocalDate periodDate) {
        this.periodDate = periodDate;
    }
}
