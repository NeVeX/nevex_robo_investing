package com.nevex.roboinvesting.database;

import com.nevex.roboinvesting.database.entity.StockPricesHistoricalEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface StockPricesHistoricalRepository extends CrudRepository<StockPricesHistoricalEntity, BigInteger> {


}
