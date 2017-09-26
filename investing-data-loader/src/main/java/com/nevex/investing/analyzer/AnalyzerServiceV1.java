package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerWeightV1;
import com.nevex.investing.database.AnalyzerWeightsRepositoryV1;
import com.nevex.investing.database.entity.AnalyzerWeightEntityV1;
import com.nevex.investing.model.Analyzer;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/21/2017.
 */
@Deprecated
public class AnalyzerServiceV1 {

    private final static Logger LOGGER = LoggerFactory.getLogger(AnalyzerServiceV1.class);
    private final Map<Analyzer, TreeSet<AnalyzerWeightV1>> analyzerWeights = new HashMap<>();
    private final AnalyzerWeightsRepositoryV1 analyzerWeightsRepository;

    public AnalyzerServiceV1(AnalyzerWeightsRepositoryV1 analyzerWeightsRepository) {
        if ( analyzerWeightsRepository == null ) { throw new IllegalArgumentException("Provided analyzerWeightsRepository is null"); }
        this.analyzerWeightsRepository = analyzerWeightsRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public void refresh() throws ServiceException {
        int previousCount = analyzerWeights.size();
        for (AnalyzerWeightEntityV1 entity : analyzerWeightsRepository.findAll()) {

            if ( !Analyzer.fromTitle(entity.getName()).isPresent()) {
                throw new ServiceException("The database name for analyzer ["+entity.getName()+"] does not exist in the code");
            }

            AnalyzerWeightV1 weight = new AnalyzerWeightV1(entity);
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
        Set<Analyzer> databaseAnalyzers = new HashSet<>();
        for ( Analyzer analyzer : analyzerWeights.keySet()) {
            databaseAnalyzers.add(analyzer);
            for ( AnalyzerWeightV1 weight : analyzerWeights.get(analyzer)) {

                for ( AnalyzerWeightV1 compareWeight : analyzerWeights.get(analyzer)) {
                    if ( weight == compareWeight) { continue; }

                    if ( weight.isAround(compareWeight.getEnd())) {
                        throw new ServiceException("Analyzer ["+analyzer.getTitle()+"] has incorrect weight configurations for ["+weight+"] and ["+compareWeight+"]");
                    }
                }
            }
        }

        // Check we have the same amount of analyzers
        if ( databaseAnalyzers.size() != Analyzer.values().length) {
            throw new ServiceException("Amount of database analyzers ["+databaseAnalyzers.size()+"] does not equal to the amount of code analyzers ["+Analyzer.values().length+"]");
//            LOGGER.warn("The amount of database analyzers [{}] does not equal to the amount of defined code analyzers [{}]", databaseAnalyzers.size(), Analyzer.values().length);
        }
        List<Analyzer> missingAnalyzers = Arrays.stream(Analyzer.values()).filter( analyzer -> !databaseAnalyzers.contains(analyzer)).collect(Collectors.toList());
        if ( !missingAnalyzers.isEmpty()) {
            throw new ServiceException("Missing the following analyzers ["+missingAnalyzers+"] in the database");
        }
    }

    Optional<Double> getWeight(Analyzer analyzer, BigDecimal value) {
        if ( !analyzerWeights.containsKey(analyzer)) {
            return Optional.empty();
        }

        TreeSet<AnalyzerWeightV1> weights = analyzerWeights.get(analyzer);

        Optional<AnalyzerWeightV1> foundWeightOpt = weights.stream().filter( weight -> weight.isAround(value)).findFirst();
        if ( !foundWeightOpt.isPresent()) {
            return Optional.empty();
        }
        AnalyzerWeightV1 foundWeight = foundWeightOpt.get();
        return Optional.of(foundWeight.getWeight());
    }

}
