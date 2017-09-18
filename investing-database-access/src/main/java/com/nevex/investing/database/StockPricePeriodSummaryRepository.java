package com.nevex.investing.database;

import com.nevex.investing.database.entity.StockPricePeriodSummaryEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface StockPricePeriodSummaryRepository extends PagingAndSortingRepository<StockPricePeriodSummaryEntity, Integer> {

    Optional<StockPricePeriodSummaryEntity> findByTickerIdAndPeriodName(int tickerId, String periodName);

}
