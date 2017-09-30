package com.nevex.investing.database;

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
public interface StockPricesHistoricalRepository extends PagingAndSortingRepository<StockPriceHistoricalEntity, BigInteger> {

    List<StockPriceHistoricalEntity> findAllByTickerIdAndDateLessThanEqual(int tickerId, LocalDate asOfDate, Pageable pageable);

    Optional<StockPriceHistoricalEntity> findTopByTickerIdOrderByDateDesc(int tickerId);

    Optional<StockPriceHistoricalEntity> findByTickerIdAndDate(int tickerId, LocalDate date);

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    void deleteByTickerId(int tickerId);

}
