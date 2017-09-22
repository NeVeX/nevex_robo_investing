package com.nevex.investing.service;

import com.nevex.investing.analyzer.model.AnalyzerSummaryResult;
import com.nevex.investing.database.TickerAnalyzersRepository;
import com.nevex.investing.database.TickerAnalyzersSummaryRepository;
import com.nevex.investing.database.entity.TickerAnalyzerEntity;
import com.nevex.investing.database.entity.TickerAnalyzerSummaryEntity;
import com.nevex.investing.database.model.DataSaveException;
import com.nevex.investing.database.utils.RepositoryUtils;
import com.nevex.investing.analyzer.model.AnalyzerResult;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
public class TickerAnalyzersAdminService extends TickerAnalyzersService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerAnalyzersAdminService.class);

    public TickerAnalyzersAdminService(TickerAnalyzersRepository tickerAnalyzersRepository, TickerAnalyzersSummaryRepository tickerAnalyzersSummaryRepository) {
        super(tickerAnalyzersRepository, tickerAnalyzersSummaryRepository);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewAnalyzers(final Collection<AnalyzerResult> analyzerResults) throws ServiceException {
        for ( AnalyzerResult analyzerResult : analyzerResults) {
            saveNewAnalyzer(analyzerResult);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewAnalyzer(final AnalyzerResult analyzerResult) throws ServiceException {
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewAnalyzer(AnalyzerSummaryResult summaryResult) throws ServiceException {
        TickerAnalyzerSummaryEntity entity = new TickerAnalyzerSummaryEntity(summaryResult.getTickerId(),
                summaryResult.getDate(), summaryResult.getAnalyzerCount(), summaryResult.getAverageWeight(), summaryResult.getAdjustedWeight());
        try {
            RepositoryUtils.createOrUpdate(tickerAnalyzersSummaryRepository, entity,
                    () -> tickerAnalyzersSummaryRepository.findByTickerIdAndDate(summaryResult.getTickerId(), summaryResult.getDate()));
        } catch (DataSaveException dataEx) {
            throw new ServiceException("Could not save analyzer summary data ["+ dataEx.getDataFailedToSave()+"]", dataEx);
        }
    }

}
