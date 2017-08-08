package com.nevex.roboinvesting.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.roboinvesting.database.entity.TickersEntity;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class TickerDto {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("name")
    private String name;
    @JsonProperty("sector")
    private String sector;
    @JsonProperty("industry")
    private String industry;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public static TickerDto from(TickersEntity entity) {
        TickerDto dto = new TickerDto();
        dto.name = entity.getName();
        dto.symbol = entity.getSymbol();
        dto.sector = entity.getSector();
        dto.industry = entity.getIndustry();
        return dto;
    }

}
