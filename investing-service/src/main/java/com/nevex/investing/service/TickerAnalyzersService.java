package com.nevex.investing.service;

import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.database.TickerAnalyzersRepository;
import com.nevex.investing.database.TickerAnalyzersSummaryRepository;
import com.nevex.investing.database.entity.TickerAnalyzerEntity;
import com.nevex.investing.database.entity.TickerAnalyzerSummaryEntity;
import com.nevex.investing.model.Analyzer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        // Get the latest entry for this tickers analyzers
        Optional<TickerAnalyzerEntity> topAnalyzerOpt = tickerAnalyzersRepository.findTopByTickerIdOrderByDateDesc(tickerId);
        if ( !topAnalyzerOpt.isPresent()) {
            return new ArrayList<>();
        }
        TickerAnalyzerEntity topEntity = topAnalyzerOpt.get();
        // Find all analyzers for this date - order by the weight desc
        List<TickerAnalyzerEntity> entities = tickerAnalyzersRepository.findByTickerIdAndDateOrderByWeightDesc(tickerId, topEntity.getDate());
        return entities.stream().map(AnalyzerResult::new).collect(Collectors.toList());
    }

    public Optional<AnalyzerSummaryResult> getAnalyzerSummary(int tickerId, LocalDate date) {
        Optional<TickerAnalyzerSummaryEntity> summaryEntityOpt = tickerAnalyzersSummaryRepository.findByTickerIdAndDate(tickerId, date);
        if ( !summaryEntityOpt.isPresent()) {
            return Optional.empty();
        }
        TickerAnalyzerSummaryEntity summaryEntity = summaryEntityOpt.get();
        return Optional.of(new AnalyzerSummaryResult(summaryEntity));
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

    public List<AnalyzerSummaryResult> getTopBestWeightedTickerSummaryAnalyzers(LocalDate date) {
        return getTopTickerSummaryAnalyzers(Sort.Direction.DESC, date);
    }

    public List<AnalyzerSummaryResult> getTopWorstWeightedTickerSummaryAnalyzers(LocalDate date) {
        return getTopTickerSummaryAnalyzers(Sort.Direction.ASC, date);
    }

    public List<AnalyzerSummaryResult> getLatestTopBestWeightedTickerSummaryAnalyzers() {
        return getTopTickerSummaryAnalyzers(Sort.Direction.DESC, getLatestDateForSummaryAnalyzers());
    }

    public List<AnalyzerSummaryResult> getLatestTopWorstWeightedTickerSummaryAnalyzers() {
        return getTopTickerSummaryAnalyzers(Sort.Direction.ASC, getLatestDateForSummaryAnalyzers());
    }

    private LocalDate getLatestDateForSummaryAnalyzers() {
        Optional<TickerAnalyzerSummaryEntity> topDateOpt = tickerAnalyzersSummaryRepository.findTopByOrderByDateDesc();
        if ( topDateOpt.isPresent()) {
            return topDateOpt.get().getDate();
        }
        return null;
    }

    private List<AnalyzerSummaryResult> getTopTickerSummaryAnalyzers(Sort.Direction weightSortDirection, LocalDate date) {
        PageRequest pageRequest = new PageRequest(0, 30, new Sort(
                new Sort.Order(weightSortDirection, TickerAnalyzerSummaryEntity.ADJUSTED_WEIGHT_PROPERTY_NAME)
        ));
        Page<TickerAnalyzerSummaryEntity> page = tickerAnalyzersSummaryRepository.findAllByDate(date, pageRequest);
        if ( page.hasContent() ) {
            return page.getContent().stream().map(AnalyzerSummaryResult::new).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
