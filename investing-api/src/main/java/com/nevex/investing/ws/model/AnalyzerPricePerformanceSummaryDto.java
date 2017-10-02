package com.nevex.investing.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nevex.investing.analyzer.model.AnalyzerPricePerformance;
import com.nevex.investing.analyzer.model.AnalyzerPricePerformanceSummary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 10/1/2017.
 */
public class AnalyzerPricePerformanceSummaryDto {
    private final static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("total_stocks")
    private int totalStocks;
    @JsonProperty("percent_correct")
    private int percentCorrect;
    @JsonProperty("best_price_movement")
    private BigDecimal bestPriceMovement;
    @JsonProperty("best_percent_movement")
    private BigDecimal bestPricePercentMovement;
    @JsonProperty("worst_price_movement")
    private BigDecimal worstPriceMovement;
    @JsonProperty("worst_percent_movement")
    private BigDecimal worstPricePercentMovement;
    @JsonProperty("best_price_movements")
    private List<AnalyzerPricePerformanceDto> bestPriceMovements;
    @JsonProperty("best_percent_movements")
    private List<AnalyzerPricePerformanceDto> bestPricePercentMovements;
    @JsonProperty("worst_price_movements")
    private List<AnalyzerPricePerformanceDto> worstPriceMovements;
    @JsonProperty("worst_percent_movements")
    private List<AnalyzerPricePerformanceDto> worstPricePercentMovements;

    public AnalyzerPricePerformanceSummaryDto() { }

    public AnalyzerPricePerformanceSummaryDto(AnalyzerPricePerformanceSummary summary) {
        this.date = summary.getDate();
        this.totalStocks = summary.getTotalStocks();
        this.percentCorrect = summary.getPercentCorrect();
        this.bestPriceMovement = summary.getBestPriceMovement().setScale(2, RoundingMode.HALF_EVEN);
        this.bestPricePercentMovement = summary.getBestPricePercentMovement().multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_EVEN);
        this.worstPriceMovement = summary.getWorstPriceMovement().setScale(2, RoundingMode.HALF_EVEN);
        this.worstPricePercentMovement = summary.getWorstPricePercentMovement().multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_EVEN);
        this.bestPriceMovements = summary.getBestPriceMovements().stream().map(AnalyzerPricePerformanceDto::new).collect(Collectors.toList());
        this.bestPricePercentMovements = summary.getBestPricePercentMovements().stream().map(AnalyzerPricePerformanceDto::new).collect(Collectors.toList());
        this.worstPriceMovements = summary.getWorstPriceMovements().stream().map(AnalyzerPricePerformanceDto::new).collect(Collectors.toList());
        this.worstPricePercentMovements = summary.getWorstPricePercentMovements().stream().map(AnalyzerPricePerformanceDto::new).collect(Collectors.toList());
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalStocks() {
        return totalStocks;
    }

    public void setTotalStocks(int totalStocks) {
        this.totalStocks = totalStocks;
    }

    public int getPercentCorrect() {
        return percentCorrect;
    }

    public void setPercentCorrect(int percentCorrect) {
        this.percentCorrect = percentCorrect;
    }

    public BigDecimal getBestPriceMovement() {
        return bestPriceMovement;
    }

    public void setBestPriceMovement(BigDecimal bestPriceMovement) {
        this.bestPriceMovement = bestPriceMovement;
    }

    public BigDecimal getBestPricePercentMovement() {
        return bestPricePercentMovement;
    }

    public void setBestPricePercentMovement(BigDecimal bestPricePercentMovement) {
        this.bestPricePercentMovement = bestPricePercentMovement;
    }

    public BigDecimal getWorstPriceMovement() {
        return worstPriceMovement;
    }

    public void setWorstPriceMovement(BigDecimal worstPriceMovement) {
        this.worstPriceMovement = worstPriceMovement;
    }

    public BigDecimal getWorstPricePercentMovement() {
        return worstPricePercentMovement;
    }

    public void setWorstPricePercentMovement(BigDecimal worstPricePercentMovement) {
        this.worstPricePercentMovement = worstPricePercentMovement;
    }

    public List<AnalyzerPricePerformanceDto> getBestPriceMovements() {
        return bestPriceMovements;
    }

    public void setBestPriceMovements(List<AnalyzerPricePerformanceDto> bestPriceMovements) {
        this.bestPriceMovements = bestPriceMovements;
    }

    public List<AnalyzerPricePerformanceDto> getBestPricePercentMovements() {
        return bestPricePercentMovements;
    }

    public void setBestPricePercentMovements(List<AnalyzerPricePerformanceDto> bestPricePercentMovements) {
        this.bestPricePercentMovements = bestPricePercentMovements;
    }

    public List<AnalyzerPricePerformanceDto> getWorstPriceMovements() {
        return worstPriceMovements;
    }

    public void setWorstPriceMovements(List<AnalyzerPricePerformanceDto> worstPriceMovements) {
        this.worstPriceMovements = worstPriceMovements;
    }

    public List<AnalyzerPricePerformanceDto> getWorstPricePercentMovements() {
        return worstPricePercentMovements;
    }

    public void setWorstPricePercentMovements(List<AnalyzerPricePerformanceDto> worstPricePercentMovements) {
        this.worstPricePercentMovements = worstPricePercentMovements;
    }
}
