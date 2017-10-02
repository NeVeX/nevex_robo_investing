package com.nevex.investing.analyzer.model;

import com.nevex.investing.database.entity.AnalyzerPricePerformanceEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Mark Cunningham on 10/1/2017.
 */
public final class AnalyzerPricePerformanceSummary {

    private final LocalDate date;
    private final int totalStocks;
    private final int percentCorrect;
    private final BigDecimal bestPriceMovement;
    private final BigDecimal bestPricePercentMovement;
    private final BigDecimal worstPriceMovement;
    private final BigDecimal worstPricePercentMovement;
    private final List<AnalyzerPricePerformance> bestPriceMovements;
    private final List<AnalyzerPricePerformance> bestPricePercentMovements;
    private final List<AnalyzerPricePerformance> worstPriceMovements;
    private final List<AnalyzerPricePerformance> worstPricePercentMovements;

    public AnalyzerPricePerformanceSummary(LocalDate date, int totalStocks, int percentCorrect, BigDecimal bestPriceMovement, BigDecimal bestPricePercentMovement, BigDecimal worstPriceMovement, BigDecimal worstPricePercentMovement, List<AnalyzerPricePerformance> bestPriceMovements, List<AnalyzerPricePerformance> bestPricePercentMovements, List<AnalyzerPricePerformance> worstPriceMovements, List<AnalyzerPricePerformance> worstPricePercentMovements) {
        this.date = date;
        this.totalStocks = totalStocks;
        this.percentCorrect = percentCorrect;
        this.bestPriceMovement = bestPriceMovement;
        this.bestPricePercentMovement = bestPricePercentMovement;
        this.worstPriceMovement = worstPriceMovement;
        this.worstPricePercentMovement = worstPricePercentMovement;
        this.bestPriceMovements = bestPriceMovements;
        this.bestPricePercentMovements = bestPricePercentMovements;
        this.worstPriceMovements = worstPriceMovements;
        this.worstPricePercentMovements = worstPricePercentMovements;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getTotalStocks() {
        return totalStocks;
    }

    public int getPercentCorrect() {
        return percentCorrect;
    }

    public BigDecimal getBestPriceMovement() {
        return bestPriceMovement;
    }

    public BigDecimal getBestPricePercentMovement() {
        return bestPricePercentMovement;
    }

    public BigDecimal getWorstPriceMovement() {
        return worstPriceMovement;
    }

    public BigDecimal getWorstPricePercentMovement() {
        return worstPricePercentMovement;
    }

    public List<AnalyzerPricePerformance> getBestPriceMovements() {
        return bestPriceMovements;
    }

    public List<AnalyzerPricePerformance> getBestPricePercentMovements() {
        return bestPricePercentMovements;
    }

    public List<AnalyzerPricePerformance> getWorstPriceMovements() {
        return worstPriceMovements;
    }

    public List<AnalyzerPricePerformance> getWorstPricePercentMovements() {
        return worstPricePercentMovements;
    }
}
