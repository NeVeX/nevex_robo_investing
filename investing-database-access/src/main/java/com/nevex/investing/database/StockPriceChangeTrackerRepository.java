package com.nevex.investing.database;

import com.nevex.investing.database.entity.StockPriceChangeTrackerEntity;
import com.nevex.investing.database.entity.StockPriceHistoricalEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface StockPriceChangeTrackerRepository extends PagingAndSortingRepository<StockPriceChangeTrackerEntity, Integer> {

    Optional<StockPriceChangeTrackerEntity> findByTickerIdAndPeriodName(int tickerId, String periodName);

}