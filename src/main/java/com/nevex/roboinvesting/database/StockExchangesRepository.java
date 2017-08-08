package com.nevex.roboinvesting.database;

import com.nevex.roboinvesting.database.entity.StockExchangesEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface StockExchangesRepository extends CrudRepository<StockExchangesEntity, Short> {

    // the super class is all we need for now...

}
