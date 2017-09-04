package com.nevex.investing.database;

import com.nevex.investing.database.entity.DataLoaderRunEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface DataLoaderRunsRepository extends PagingAndSortingRepository<DataLoaderRunEntity, Integer> {

    // Nothing to extend at the moment

}
