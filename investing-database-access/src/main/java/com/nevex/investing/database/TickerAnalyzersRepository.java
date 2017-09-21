package com.nevex.investing.database;

import com.nevex.investing.database.entity.TickerAnalyzerEntity;
import com.nevex.investing.database.entity.YahooStockInfoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface TickerAnalyzersRepository extends PagingAndSortingRepository<TickerAnalyzerEntity, Integer> {

    Optional<TickerAnalyzerEntity> findByTickerIdAndDateAndName(int tickerId, LocalDate date, String name);

}
