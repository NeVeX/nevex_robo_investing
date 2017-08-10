package com.nevex.roboinvesting.database;

import com.nevex.roboinvesting.database.entity.StockPricesHistoricalEntity;
import org.springframework.data.repository.CrudRepository;
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
public interface StockPricesHistoricalRepository extends CrudRepository<StockPricesHistoricalEntity, BigInteger> {

    List<StockPricesHistoricalEntity> findAllBySymbol(String symbol);

    Optional<StockPricesHistoricalEntity> findBySymbolAndDate(String symbol, LocalDate date);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteBySymbol(String symbol);

}
