package com.nevex.investing.analyzer.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private final int totalCorrectTopRecommendationsPercent;
//    private final int totalInCorrectTopRecommendationsPercent;
    private final List<AnalyzerSummaryResult> correctRecommendedBuys;
    private final List<AnalyzerSummaryResult> correctRecommendedSells;
    private final List<AnalyzerSummaryResult> inCorrectRecommendedBuys;
    private final List<AnalyzerSummaryResult> inCorrectRecommendedSells;

    public AnalyzerPricePerformanceSummary(LocalDate date, int totalStocks, int percentCorrect, BigDecimal bestPriceMovement, BigDecimal bestPricePercentMovement,
                                           BigDecimal worstPriceMovement, BigDecimal worstPricePercentMovement,
                                           int totalCorrectTopRecommendationsPercent,
                                           List<AnalyzerSummaryResult> correctRecommendedBuys, List<AnalyzerSummaryResult> correctRecommendedSells,
                                           List<AnalyzerSummaryResult> inCorrectRecommendedBuys, List<AnalyzerSummaryResult> inCorrectRecommendedSells,
                                           List<AnalyzerPricePerformance> bestPriceMovements, List<AnalyzerPricePerformance> bestPricePercentMovements,
                                           List<AnalyzerPricePerformance> worstPriceMovements, List<AnalyzerPricePerformance> worstPricePercentMovements) {
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

        this.correctRecommendedBuys = correctRecommendedBuys;
        this.correctRecommendedSells = correctRecommendedSells;

        this.inCorrectRecommendedBuys = inCorrectRecommendedBuys;
        this.inCorrectRecommendedSells = inCorrectRecommendedSells;

        this.totalCorrectTopRecommendationsPercent = totalCorrectTopRecommendationsPercent;
//        this.totalInCorrectTopRecommendationsPercent = totalInCorrectTopRecommendationsPercent;


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

    public List<AnalyzerSummaryResult> getCorrectRecommendedBuys() {
        return correctRecommendedBuys;
    }

    public List<AnalyzerSummaryResult> getCorrectRecommendedSells() {
        return correctRecommendedSells;
    }

    public int getTotalCorrectTopRecommendationsPercent() {
        return totalCorrectTopRecommendationsPercent;
    }

//    public int getTotalInCorrectTopRecommendationsPercent() {
//        return totalInCorrectTopRecommendationsPercent;
//    }

    public List<AnalyzerSummaryResult> getInCorrectRecommendedBuys() {
        return inCorrectRecommendedBuys;
    }

    public List<AnalyzerSummaryResult> getInCorrectRecommendedSells() {
        return inCorrectRecommendedSells;
    }
}
