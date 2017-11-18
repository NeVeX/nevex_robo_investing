package com.nevex.investing.analyzer;

import com.nevex.investing.analyzer.model.AnalyzerWeightV2;
import com.nevex.investing.database.AnalyzerWeightsRepositoryV2;
import com.nevex.investing.database.entity.AnalyzerWeightEntityV2;
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
public class AnalyzerServiceV2 {

    private final static Logger LOGGER = LoggerFactory.getLogger(AnalyzerServiceV2.class);
    private final Map<Analyzer, AnalyzerWeightV2> analyzerWeights = new HashMap<>();
    private final AnalyzerWeightsRepositoryV2 analyzerWeightsRepository;

    public AnalyzerServiceV2(AnalyzerWeightsRepositoryV2 analyzerWeightsRepository) {
        if ( analyzerWeightsRepository == null ) { throw new IllegalArgumentException("Provided analyzerWeightsRepository is null"); }
        this.analyzerWeightsRepository = analyzerWeightsRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public void refresh() throws ServiceException {
        int previousCount = analyzerWeights.size();
        for (AnalyzerWeightEntityV2 entity : analyzerWeightsRepository.findAll()) {

            Optional<Analyzer> analyzerOpt = Analyzer.fromTitle(entity.getName());
            if ( !analyzerOpt.isPresent()) {
                LOGGER.warn("The database name for analyzer ["+entity.getName()+"] does not exist in the code");
                continue;
            }

            AnalyzerWeightV2 weight = new AnalyzerWeightV2(analyzerOpt.get(), entity);
            if ( analyzerWeights.containsKey(weight.getAnalyzer())) {
                throw new ServiceException("There is already an analyzer weight - this should not happen. Weight found: ["+weight+"]");
            }
            analyzerWeights.put(weight.getAnalyzer(), weight);
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

            AnalyzerWeightV2 weight = analyzerWeights.get(analyzer);

            if ( weight.getLowest().compareTo(weight.getCenter()) > 0) {
                throw new ServiceException("Invalid analyzer weight found. Lowest cannot be greater than center for weight ["+weight+"]");
            }
            if ( weight.getHighest().compareTo(weight.getCenter()) < 0 ) {
                throw new ServiceException("Invalid analyzer weight found. Highest cannot be less than center for weight ["+weight+"]");
            }
        }

        // Check we have the same amount of analyzers
        if ( databaseAnalyzers.size() != Analyzer.values().length) {
            LOGGER.warn("\n\n\nThe amount of database analyzers [{}] does not equal to the amount of code analyzers [{}]", databaseAnalyzers.size(), Analyzer.values().length);
//            throw new ServiceException("Amount of database analyzers ["+databaseAnalyzers.size()+"] does not equal to the amount of code analyzers ["+Analyzer.values().length+"]");
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
        return Optional.ofNullable(analyzerWeights.get(analyzer).calculateWeight(value));
    }

}
