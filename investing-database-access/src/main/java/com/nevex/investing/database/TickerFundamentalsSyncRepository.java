package com.nevex.investing.database;

import com.nevex.investing.database.entity.TickerFundamentalsEntity;
import com.nevex.investing.database.entity.TickerFundamentalsSyncEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
@Repository
public interface TickerFundamentalsSyncRepository extends CrudRepository<TickerFundamentalsSyncEntity, Integer> {

    Optional<TickerFundamentalsSyncEntity> findByTickerId(int tickerId);
}
