package com.nevex.investing.database;

import com.nevex.investing.database.entity.StockExchangeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface StockExchangesRepository extends CrudRepository<StockExchangeEntity, Short> {

    // the super class is all we need for now...

}
