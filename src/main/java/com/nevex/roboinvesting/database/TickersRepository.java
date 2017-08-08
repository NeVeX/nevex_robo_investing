package com.nevex.roboinvesting.database;

import com.nevex.roboinvesting.database.entity.TickersEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface TickersRepository extends CrudRepository<TickersEntity, Integer> {

    /**
     * Tries to find the ticker by it's symbol
     */
    Optional<TickersEntity> findBySymbol(String symbol);

}
