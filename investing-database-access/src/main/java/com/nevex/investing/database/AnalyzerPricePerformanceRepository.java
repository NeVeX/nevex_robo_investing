package com.nevex.investing.database;

import com.nevex.investing.database.entity.AnalyzerPricePerformanceEntity;
import com.nevex.investing.database.entity.AnalyzerWeightEntityV1;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface AnalyzerPricePerformanceRepository extends PagingAndSortingRepository<AnalyzerPricePerformanceEntity, Integer> {

    Optional<AnalyzerPricePerformanceEntity> findByTickerIdAndDate(int tickerId, LocalDate date);

    Optional<AnalyzerPricePerformanceEntity> findTopOneByTickerIdOrderByDateDesc(int tickerId);

    Optional<AnalyzerPricePerformanceEntity> findTopByOrderByDateDesc();

    List<AnalyzerPricePerformanceEntity> findAllByDate(LocalDate date);

}
