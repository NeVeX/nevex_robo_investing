package com.nevex.investing.service;

import com.nevex.investing.analyzer.model.AnalyzerPricePerformance;
import com.nevex.investing.database.AnalyzerPricePerformanceRepository;
import com.nevex.investing.database.entity.AnalyzerPricePerformanceEntity;
import com.nevex.investing.database.model.DataSaveException;
import com.nevex.investing.database.utils.RepositoryUtils;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Mark Cunningham on 10/1/2017.
 */
public class AnalyzerPricePerformanceAdminService extends AnalyzerPricePerformanceService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AnalyzerPricePerformanceAdminService.class);

    public AnalyzerPricePerformanceAdminService(AnalyzerPricePerformanceRepository analyzerPricePerformanceRepository, TickerService tickerService, TickerAnalyzersAdminService tickerAnalyzersAdminService) {
        super(analyzerPricePerformanceRepository, tickerService, tickerAnalyzersAdminService);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewPerformancePriceAnalyzer(AnalyzerPricePerformance pricePerformance) throws ServiceException {

        AnalyzerPricePerformanceEntity entity = new AnalyzerPricePerformanceEntity(
                pricePerformance.getTickerId(),
                pricePerformance.getDate(),
                pricePerformance.isPriceDirectionAsExpected(),
                pricePerformance.getPriceDifference(),
                pricePerformance.getPercentDifference()
        );

        try {
            RepositoryUtils.createOrUpdate(analyzerPricePerformanceRepository, entity,
                    () -> analyzerPricePerformanceRepository.findByTickerIdAndDate(entity.getTickerId(), entity.getDate()));
        } catch (DataSaveException dataEx) {
            throw new ServiceException("Could not save or update analyzer price performance ["+pricePerformance+"]", dataEx);
        }
    }

}
