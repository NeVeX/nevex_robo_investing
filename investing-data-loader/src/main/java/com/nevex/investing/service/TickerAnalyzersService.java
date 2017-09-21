package com.nevex.investing.service;

import com.nevex.investing.database.TickerAnalyzersRepository;
import com.nevex.investing.database.entity.TickerAnalyzerEntity;
import com.nevex.investing.database.model.DataSaveException;
import com.nevex.investing.database.utils.RepositoryUtils;
import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
public class TickerAnalyzersService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerAnalyzersService.class);
    private final TickerAnalyzersRepository tickerAnalyzersRepository;

    public TickerAnalyzersService(TickerAnalyzersRepository tickerAnalyzersRepository) {
        if ( tickerAnalyzersRepository == null) { throw new IllegalArgumentException("Provided tickerAnalyzersRepository is null"); }
        this.tickerAnalyzersRepository = tickerAnalyzersRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewAnalyzers(final Collection<AnalyzerResult> analyzerResults) throws ServiceException {
        for ( AnalyzerResult analyzerResult : analyzerResults) {
            saveNewAnalyzers(analyzerResult);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewAnalyzers(final AnalyzerResult analyzerResult) throws ServiceException {
        TickerAnalyzerEntity entity = new TickerAnalyzerEntity(analyzerResult.getTickerId(),
                analyzerResult.getDate(), analyzerResult.getName(), analyzerResult.getWeight());

        try {
            RepositoryUtils.createOrUpdate(tickerAnalyzersRepository, entity,
                    () -> tickerAnalyzersRepository.findByTickerIdAndDateAndName(analyzerResult.getTickerId(), analyzerResult.getDate(), analyzerResult.getName())
            );
        } catch (DataSaveException dataEx) {
            LOGGER.warn("Could not save analyzer entry into the database for entity [{}]", dataEx.getDataFailedToSave());
            throw new ServiceException("Could not save analyzer entry into the database for ticker Id ["+analyzerResult.getTickerId()+"]", dataEx);
        }
    }

}
