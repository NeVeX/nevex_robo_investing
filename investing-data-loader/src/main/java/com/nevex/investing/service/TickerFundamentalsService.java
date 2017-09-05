package com.nevex.investing.service;

import com.nevex.investing.api.usfundamentals.model.UsFundamentalIndicator;
import com.nevex.investing.api.usfundamentals.model.UsFundamentalsResponse;
import com.nevex.investing.database.TickerFundamentalsRepository;
import com.nevex.investing.database.TickerFundamentalsSyncRepository;
import com.nevex.investing.database.entity.TickerFundamentalsEntity;
import com.nevex.investing.database.entity.TickerFundamentalsSyncEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class TickerFundamentalsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerFundamentalsService.class);
    private final TickerFundamentalsRepository tickerFundamentalsRepository;
    private final TickerFundamentalsSyncRepository tickerFundamentalsSyncRepository;

    public TickerFundamentalsService(TickerFundamentalsRepository tickerFundamentalsRepository, TickerFundamentalsSyncRepository tickerFundamentalsSyncRepository) {
        if ( tickerFundamentalsRepository == null ) { throw new IllegalArgumentException("Provided tickerFundamentalsRepository is null"); }
        if ( tickerFundamentalsSyncRepository == null ) { throw new IllegalArgumentException("Provided tickerFundamentalsSyncRepository is null"); }
        this.tickerFundamentalsRepository = tickerFundamentalsRepository;
        this.tickerFundamentalsSyncRepository = tickerFundamentalsSyncRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHistoricalFundamentals(int tickerId, UsFundamentalsResponse usFundamentalsResponse) throws ServiceException {

        Set<TickerFundamentalsEntity> quarterlyEntities = parseFundamentalsIntoEntities(tickerId, 'q', usFundamentalsResponse.getQuarterlyIndicators());
        Set<TickerFundamentalsEntity> yearlyEntities = parseFundamentalsIntoEntities(tickerId, 'y', usFundamentalsResponse.getYearlyIndicators());

        saveEntities(quarterlyEntities);
        saveEntities(yearlyEntities);

        saveInitialSyncData(tickerId, usFundamentalsResponse.getDownloadStartTimeNanoseconds());
    }

    private void saveInitialSyncData(int tickerId, long downloadStartTimeNanoseconds) throws ServiceException {
        TickerFundamentalsSyncEntity syncEntity = new TickerFundamentalsSyncEntity(tickerId, downloadStartTimeNanoseconds);
        TickerFundamentalsSyncEntity savedEntity;
        try {
            savedEntity = tickerFundamentalsSyncRepository.save(syncEntity);
        } catch (Exception e) {
            throw new ServiceException("Could not save sync entity for fundamentals ["+syncEntity+"]", e);
        }

        if ( savedEntity == null) {
            throw new ServiceException("The sync entity did not get saved into the database ["+syncEntity+"]");
        }

    }

    private void saveEntities(Set<TickerFundamentalsEntity> entities) throws ServiceException {
        for ( TickerFundamentalsEntity entity : entities ) {
            saveEntity(entity);
        }
    }

    private void saveEntity(TickerFundamentalsEntity tickerFundamentalsEntity) throws ServiceException {
        // check if it already exists
        Optional<TickerFundamentalsEntity> existingEntityOpt = tickerFundamentalsRepository.findByTickerIdAndPeriodEndAndPeriodType(
                tickerFundamentalsEntity.getId(), tickerFundamentalsEntity.getPeriodEnd(), tickerFundamentalsEntity.getPeriodType());

        final TickerFundamentalsEntity entityToSave;
        if ( existingEntityOpt.isPresent()) {
            TickerFundamentalsEntity existingEntity = existingEntityOpt.get();
            // merge the two data sets
            existingEntity.merge(tickerFundamentalsEntity);
            entityToSave = existingEntity;
            LOGGER.info("Found existing fundamentals data for ticker [{}] - will merge the two data sets", existingEntity.getTickerId());
        } else {
            entityToSave = tickerFundamentalsEntity;
        }

        TickerFundamentalsEntity savedEntity;
        try {
            savedEntity = tickerFundamentalsRepository.save(entityToSave);
        } catch (Exception e) {
            throw new ServiceException("Could not save fundamentals entity ["+entityToSave+"]", e);
        }

        if ( savedEntity == null ) {
            throw new ServiceException("Entity was not actually saved to the database ["+entityToSave+"]");
        }
    }

    private TreeSet<TickerFundamentalsEntity> parseFundamentalsIntoEntities(int tickerId, char periodType, Set<UsFundamentalIndicator> indicators) {
        return indicators.stream()
                .map( ind -> new TickerFundamentalsEntity(
                    tickerId, ind.getEndPeriod(), periodType, ind.getEarningsPerShareBasic(),
                        ind.getCommonStockSharesOutstanding(), ind.getStockHoldersEquity()
                ))
                .collect(Collectors.toCollection(TreeSet<TickerFundamentalsEntity>::new));
    }

}
