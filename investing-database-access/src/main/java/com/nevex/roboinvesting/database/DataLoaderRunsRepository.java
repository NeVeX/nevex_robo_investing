package com.nevex.roboinvesting.database;

import com.nevex.roboinvesting.database.entity.DataLoaderErrorEntity;
import com.nevex.roboinvesting.database.entity.DataLoaderRunEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface DataLoaderRunsRepository extends PagingAndSortingRepository<DataLoaderRunEntity, Integer> {

    // Nothing to extend at the moment

}
