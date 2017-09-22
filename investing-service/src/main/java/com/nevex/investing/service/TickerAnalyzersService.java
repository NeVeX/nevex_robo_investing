package com.nevex.investing.service;

import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.database.TickerAnalyzersRepository;
import com.nevex.investing.database.TickerAnalyzersSummaryRepository;
import com.nevex.investing.database.entity.TickerAnalyzerEntity;
import com.nevex.investing.database.entity.TickerAnalyzerSummaryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/21/2017.
 */
@Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
public class TickerAnalyzersService {

    protected final TickerAnalyzersRepository tickerAnalyzersRepository;
    protected final TickerAnalyzersSummaryRepository tickerAnalyzersSummaryRepository;

    public TickerAnalyzersService(TickerAnalyzersRepository tickerAnalyzersRepository, TickerAnalyzersSummaryRepository tickerAnalyzersSummaryRepository) {
        if ( tickerAnalyzersRepository == null) { throw new IllegalArgumentException("Provided tickerAnalyzersRepository is null"); }
        if ( tickerAnalyzersSummaryRepository == null) { throw new IllegalArgumentException("Provided tickerAnalyzersSummaryRepository is null"); }
        this.tickerAnalyzersRepository = tickerAnalyzersRepository;
        this.tickerAnalyzersSummaryRepository = tickerAnalyzersSummaryRepository;
    }

    public List<AnalyzerResult> getLatestAnalyzers(int tickerId) {
        Optional<TickerAnalyzerEntity> topAnalyzerOpt = tickerAnalyzersRepository.findTopByTickerIdOrderByDateDesc(tickerId);
        if ( !topAnalyzerOpt.isPresent()) {
            return new ArrayList<>();
        }
        TickerAnalyzerEntity topEntity = topAnalyzerOpt.get();
        List<TickerAnalyzerEntity> entities = tickerAnalyzersRepository.findByTickerIdAndDate(tickerId, topEntity.getDate());
        return entities.stream().map(AnalyzerResult::new).collect(Collectors.toList());
    }

    public Optional<AnalyzerSummaryResult> getLatestAnalyzerSummary(int tickerId) {
        Optional<TickerAnalyzerSummaryEntity> summaryEntityOpt = tickerAnalyzersSummaryRepository.findTopByTickerIdOrderByDateDesc(tickerId);
        if ( !summaryEntityOpt.isPresent()) {
            return Optional.empty();
        }
        TickerAnalyzerSummaryEntity summaryEntity = summaryEntityOpt.get();
        return Optional.of(new AnalyzerSummaryResult(summaryEntity));
    }

    public List<AnalyzerResult> getAnalyzers(int tickerId, LocalDate date) {
        List<TickerAnalyzerEntity> entities = tickerAnalyzersRepository.findByTickerIdAndDate(tickerId, date);
        return entities.stream().map(AnalyzerResult::new).collect(Collectors.toList());
    }

}