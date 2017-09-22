package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerWeight;
import com.nevex.investing.database.AnalyzerWeightsRepository;
import com.nevex.investing.database.entity.AnalyzerWeightEntity;
import com.nevex.investing.model.Analyzer;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Mark Cunningham on 9/21/2017.
 */
public class AnalyzerService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AnalyzerService.class);
    private final Map<Analyzer, TreeSet<AnalyzerWeight>> analyzerWeights = new HashMap<>();
    private final AnalyzerWeightsRepository analyzerWeightsRepository;

    public AnalyzerService(AnalyzerWeightsRepository analyzerWeightsRepository) {
        if ( analyzerWeightsRepository == null ) { throw new IllegalArgumentException("Provided analyzerWeightsRepository is null"); }
        this.analyzerWeightsRepository = analyzerWeightsRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public void refresh() throws ServiceException {
        int previousCount = analyzerWeights.size();
        for (AnalyzerWeightEntity entity : analyzerWeightsRepository.findAll()) {
            AnalyzerWeight weight = new AnalyzerWeight(entity);
            if ( !analyzerWeights.containsKey(weight.getAnalyzer())) {
                analyzerWeights.put(weight.getAnalyzer(), new TreeSet<>());
            }
            analyzerWeights.get(weight.getAnalyzer()).add(weight);
        }

        if ( !analyzerWeights.isEmpty()) {
            validateAnalyzerWeights();
            LOGGER.info("Updated and validated the analyzer weights. Previous count [{}], New count [{}]", previousCount, analyzerWeights.size());
        } else {
            LOGGER.warn("No analyzer weights were updated");
        }
    }

    private void validateAnalyzerWeights() throws ServiceException {
        for ( Analyzer analyzer : analyzerWeights.keySet()) {
            for ( AnalyzerWeight weight : analyzerWeights.get(analyzer)) {

                for ( AnalyzerWeight compareWeight : analyzerWeights.get(analyzer)) {
                    if ( weight == compareWeight) { continue; }

                    if ( weight.isAround(compareWeight.getEnd())) {
                        throw new ServiceException("Analyzer ["+analyzer.getTitle()+"] has incorrect weight configurations for ["+weight+"] and ["+compareWeight+"]");
                    }
                }

            }
        }
    }

    public Optional<Double> getWeight(Analyzer analyzer, BigDecimal value) {
        if ( !analyzerWeights.containsKey(analyzer)) {
            return Optional.empty();
        }

        TreeSet<AnalyzerWeight> weights = analyzerWeights.get(analyzer);

        Optional<AnalyzerWeight> foundWeightOpt = weights.stream().filter( weight -> weight.isAround(value)).findFirst();
        if ( !foundWeightOpt.isPresent()) {
            return Optional.empty();
        }
        AnalyzerWeight foundWeight = foundWeightOpt.get();
        return Optional.of(foundWeight.getWeight());
    }

}
