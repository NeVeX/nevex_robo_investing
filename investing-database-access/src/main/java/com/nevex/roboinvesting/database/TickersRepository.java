package com.nevex.roboinvesting.database;

import com.nevex.roboinvesting.database.entity.TickerEntity;
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

}
