package com.nevex.investing.database;

import com.nevex.investing.database.entity.TickerAnalyzerEntity;
import com.nevex.investing.database.entity.TickerAnalyzerSummaryEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface TickerAnalyzersSummaryRepository extends PagingAndSortingRepository<TickerAnalyzerSummaryEntity, Integer> {

    Optional<TickerAnalyzerSummaryEntity> findTopByTickerIdOrderByDateDesc(int tickerId);

    Optional<TickerAnalyzerSummaryEntity> findByTickerIdAndDate(int tickerId, LocalDate date);

}
