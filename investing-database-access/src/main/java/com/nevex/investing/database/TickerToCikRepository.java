package com.nevex.investing.database;

import com.nevex.investing.database.entity.TickerToCikEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository

public interface TickerToCikRepository extends PagingAndSortingRepository<TickerToCikEntity, Integer> {

    Optional<TickerToCikEntity> findByTickerId(int tickerId);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    TickerToCikEntity save(TickerToCikEntity entity);
}
