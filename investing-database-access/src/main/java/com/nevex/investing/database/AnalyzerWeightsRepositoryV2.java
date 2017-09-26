package com.nevex.investing.database;

import com.nevex.investing.database.entity.AnalyzerWeightEntityV1;
import com.nevex.investing.database.entity.AnalyzerWeightEntityV2;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Repository
public interface AnalyzerWeightsRepositoryV2 extends PagingAndSortingRepository<AnalyzerWeightEntityV2, Integer> {

    // Nothing to extend at the moment

}
