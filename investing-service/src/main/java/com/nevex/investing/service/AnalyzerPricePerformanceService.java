package com.nevex.investing.service;

import com.nevex.investing.analyzer.model.AnalyzerPricePerformance;
import com.nevex.investing.analyzer.model.AnalyzerPricePerformanceSummary;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.database.AnalyzerPricePerformanceRepository;
import com.nevex.investing.database.entity.AnalyzerPricePerformanceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 10/1/2017.
 */
public class AnalyzerPricePerformanceService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AnalyzerPricePerformanceService.class);
    final AnalyzerPricePerformanceRepository analyzerPricePerformanceRepository;
    final TickerService tickerService;
    final TickerAnalyzersService tickerAnalyzersService;

    public AnalyzerPricePerformanceService(AnalyzerPricePerformanceRepository analyzerPricePerformanceRepository, TickerService tickerService, TickerAnalyzersService tickerAnalyzersService) {
        if ( analyzerPricePerformanceRepository == null ) { throw new IllegalArgumentException("Provided analyzerPricePerformanceRepository is null"); }
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        if ( tickerAnalyzersService == null ) { throw new IllegalArgumentException("Provided tickerAnalyzersService is null"); }
        this.analyzerPricePerformanceRepository = analyzerPricePerformanceRepository;
        this.tickerService = tickerService;
        this.tickerAnalyzersService = tickerAnalyzersService;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public Optional<AnalyzerPricePerformance> getLatestAnalyzerPricePerformance(int tickerId) {
        Optional<AnalyzerPricePerformanceEntity> entityOpt = analyzerPricePerformanceRepository.findTopOneByTickerIdOrderByDateDesc(tickerId);
        if ( !entityOpt.isPresent()) {
            return Optional.empty();
        }
        return Optional.ofNullable(createAnalyzerPerformance(entityOpt.get()));
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public Optional<AnalyzerPricePerformance> getAnalyzerPricePerformance(int tickerId, LocalDate date) {

        Optional<AnalyzerPricePerformanceEntity> entityOpt = analyzerPricePerformanceRepository.findByTickerIdAndDate(tickerId, date);
        if ( !entityOpt.isPresent()) {
            return Optional.empty();
        }
        return Optional.ofNullable(createAnalyzerPerformance(entityOpt.get()));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<AnalyzerPricePerformanceSummary> getLatestPricePerformanceSummary() {
        Optional<AnalyzerPricePerformanceEntity> entityOpt = analyzerPricePerformanceRepository.findTopByOrderByDateDesc();
        if ( !entityOpt.isPresent()) {
            return Optional.empty();
        }
        LocalDate latestDate = entityOpt.get().getDate();
        return this.getPricePerformanceSummary(latestDate);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<AnalyzerPricePerformanceSummary> getPricePerformanceSummary(LocalDate date) {
        List<AnalyzerPricePerformanceEntity> allEntitiesForDate = analyzerPricePerformanceRepository.findAllByDate(date);
        return Optional.ofNullable(getSummary(date, allEntitiesForDate));
    }

    private AnalyzerPricePerformance createAnalyzerPerformance(AnalyzerPricePerformanceEntity entity) {
        Optional<String> symbolOpt = tickerService.tryGetSymbolForId(entity.getTickerId());
        if (!symbolOpt.isPresent()) {
            return null;
        }
        return new AnalyzerPricePerformance(symbolOpt.get(), entity);
    }

    /**
     * NOTE: THIS METHOD IS MADNESS - 2AM MADNESS!!!!!! M.A.D.N.E.S.S
     * TODO: GO MADDER
     */
    private AnalyzerPricePerformanceSummary getSummary(LocalDate asOfDate, List<AnalyzerPricePerformanceEntity> allEntitiesForDate) {

        if ( allEntitiesForDate.isEmpty()) {
            return null;
        }

        int totalStocks = allEntitiesForDate.size();


        List<AnalyzerPricePerformanceEntity> correctEntities = allEntitiesForDate.stream()
                .filter(AnalyzerPricePerformanceEntity::getPriceDirectionAsExpected)
                .collect(Collectors.toList());

        List<AnalyzerPricePerformanceEntity> inCorrectEntities = allEntitiesForDate.stream()
                .filter(entity -> !entity.getPriceDirectionAsExpected() )
                .collect(Collectors.toList());

        List<AnalyzerPricePerformance> bestPrices = correctEntities.stream()
                .filter(entity -> entity.getPriceDifference().signum() >= 0)
                .map(this::createAnalyzerPerformance)
                .filter(perf -> perf != null)
                .sorted(Comparator.comparing(AnalyzerPricePerformance::getPriceDifference).reversed())
                .limit(10)
                .collect(Collectors.toList());

        List<AnalyzerPricePerformance> bestPercents = correctEntities.stream()
                .filter(entity -> entity.getPercentDifference().signum() >= 0)
                .map(this::createAnalyzerPerformance)
                .filter(perf -> perf != null)
                .sorted(Comparator.comparing(AnalyzerPricePerformance::getPercentDifference).reversed())
                .limit(10)
                .collect(Collectors.toList());

        List<AnalyzerPricePerformance> worstPrices = inCorrectEntities.stream()
                .filter(entity -> entity.getPriceDifference().signum() < 0)
                .map(this::createAnalyzerPerformance)
                .filter(perf -> perf != null)
                .sorted(Comparator.comparing(AnalyzerPricePerformance::getPriceDifference))
                .limit(10)
                .collect(Collectors.toList());

        List<AnalyzerPricePerformance> worstPercents = inCorrectEntities.stream()
                .filter(entity -> entity.getPercentDifference().signum() < 0)
                .map(this::createAnalyzerPerformance)
                .filter(perf -> perf != null)
                .sorted(Comparator.comparing(AnalyzerPricePerformance::getPercentDifference))
                .limit(10)
                .collect(Collectors.toList());

        Optional<BigDecimal> bestPriceOpt = bestPrices.stream().findFirst().map(AnalyzerPricePerformance::getPriceDifference);
        Optional<BigDecimal> bestPercentOpt = bestPercents.stream().findFirst().map(AnalyzerPricePerformance::getPercentDifference);
        Optional<BigDecimal> worstPriceOpt = worstPrices.stream().findFirst().map(AnalyzerPricePerformance::getPriceDifference);
        Optional<BigDecimal> worstPercentOpt = worstPercents.stream().findFirst().map(AnalyzerPricePerformance::getPercentDifference);

        // see how the best "buy" and "sells" did
        List<AnalyzerSummaryResult> topBest = tickerAnalyzersService.getTopBestWeightedTickerSummaryAnalyzers(asOfDate);
        List<AnalyzerSummaryResult> topWorst = tickerAnalyzersService.getTopWorstWeightedTickerSummaryAnalyzers(asOfDate);
        List<AnalyzerSummaryResult> allTopRecommendations = new ArrayList<>();
        allTopRecommendations.addAll(topBest);
        allTopRecommendations.addAll(topWorst);

        List<AnalyzerSummaryResult> correctBuyRecommendations = new ArrayList<>();
        List<AnalyzerSummaryResult> correctSellRecommendations = new ArrayList<>();
        List<AnalyzerSummaryResult> inCorrectBuyRecommendations = new ArrayList<>();
        List<AnalyzerSummaryResult> inCorrectSellRecommendations = new ArrayList<>();

        for ( AnalyzerPricePerformanceEntity entity : allEntitiesForDate) {
            for ( AnalyzerSummaryResult result : allTopRecommendations ) {
                // check the correct ones
                if ( entity.getTickerId() == result.getTickerId()) {
                    if ( entity.getPriceDirectionAsExpected()) {
                        if (entity.getPercentDifference().signum() < 0) {
                            correctSellRecommendations.add(result);
                        } else {
                            correctBuyRecommendations.add(result);
                        }
                    } else {
                        if ( entity.getPercentDifference().signum() < 0) { // The price went down when we expected up, so it's a bad buy recommendation
                            inCorrectBuyRecommendations.add(result);
                        } else {
                            inCorrectSellRecommendations.add(result);
                        }
                    }
                    break;
                }
            }
        }

        int totalTopRecommendations = allTopRecommendations.size();

        // TODO: Fix why we recommed stocks, yet cannot do a comparison to the price here - below I have to fucking remove stocks that did not get a price analysis done on it. wtf.
        for ( AnalyzerSummaryResult result : allTopRecommendations ) {
            boolean found = false;
            for ( AnalyzerPricePerformanceEntity entity : allEntitiesForDate) {
                if ( entity.getTickerId() == result.getTickerId()) {
                    found = true;
                    break;
                }
            }
            if ( ! found) {
                LOGGER.error("why is this ticker not found - {}", result);
                totalTopRecommendations--;
            }
        }


        int percentCorrect = (int) (((double) correctEntities.size() / (double) totalStocks) * 100);
        int totalCorrectRecommendations = correctBuyRecommendations.size() + correctSellRecommendations.size();
//        int totalInCorrectRecommendations = inCorrectBuyRecommendations.size() + inCorrectSellRecommendations.size();
        int topRecommendationsCorrectPercent = (int) (((double) totalCorrectRecommendations / (double) totalTopRecommendations) * 100);
//        int topRecommendationsInCorrectPercent = (int) (((double) totalInCorrectRecommendations / (double) totalTopRecommendations) * 100);
        return new AnalyzerPricePerformanceSummary(
                asOfDate,
                totalStocks,
                percentCorrect,
                bestPriceOpt.isPresent() ? bestPriceOpt.get() : null,
                bestPercentOpt.isPresent() ? bestPercentOpt.get() : null,
                worstPriceOpt.isPresent() ? worstPriceOpt.get() : null,
                worstPercentOpt.isPresent() ? worstPercentOpt.get() : null,
                topRecommendationsCorrectPercent,
                correctBuyRecommendations,
                correctSellRecommendations,
                inCorrectBuyRecommendations,
                inCorrectSellRecommendations,
                bestPrices,
                bestPercents,
                worstPrices,
                worstPercents);
    }


}
