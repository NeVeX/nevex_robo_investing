package com.nevex.investing.database;

import com.nevex.investing.database.entity.YahooStockInfoEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface YahooStockInfoRepository extends PagingAndSortingRepository<YahooStockInfoEntity, Integer> {

    Optional<YahooStockInfoEntity> findByTickerIdAndDate(int tickerId, LocalDate date);

}
