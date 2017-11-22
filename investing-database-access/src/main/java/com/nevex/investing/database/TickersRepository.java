package com.nevex.investing.database;

import com.nevex.investing.database.entity.TickerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface TickersRepository extends PagingAndSortingRepository<TickerEntity, Integer> {

    /**
     * Tries to find the ticker by it's symbol
     */
    Optional<TickerEntity> findBySymbol(String symbol);

    // https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
    Page<TickerEntity> findAllByIsTradableTrue(Pageable pageable);

    Iterable<TickerEntity> findAllByIsTradableTrue();
}
