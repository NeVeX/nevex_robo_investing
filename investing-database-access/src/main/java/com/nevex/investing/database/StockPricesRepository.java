package com.nevex.investing.database;

import com.nevex.investing.database.entity.StockPriceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface StockPricesRepository extends CrudRepository<StockPriceEntity, Integer> {

    Optional<StockPriceEntity> findByTickerId(int tickerId);

}
