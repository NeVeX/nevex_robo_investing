package com.nevex.roboinvesting.database;

import com.nevex.roboinvesting.database.entity.StockPricesEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface StockPricesRepository extends CrudRepository<StockPricesEntity, Integer> {

    Optional<StockPricesEntity> findBySymbol(String symbol);

}
