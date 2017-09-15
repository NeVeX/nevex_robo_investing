package com.nevex.investing.service;

import com.nevex.investing.api.usfundamentals.model.UsFundamentalIndicatorDto;
import com.nevex.investing.api.usfundamentals.model.UsFundamentalsResponseDto;
import com.nevex.investing.database.TickerFundamentalsRepository;
import com.nevex.investing.database.entity.TickerFundamentalsEntity;
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
public class TickerFundamentalsAdminService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerFundamentalsAdminService.class);
    private final TickerFundamentalsRepository tickerFundamentalsRepository;

    public TickerFundamentalsAdminService(TickerFundamentalsRepository tickerFundamentalsRepository) {
        if ( tickerFundamentalsRepository == null ) { throw new IllegalArgumentException("Provided tickerFundamentalsRepository is null"); }
        this.tickerFundamentalsRepository = tickerFundamentalsRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHistoricalFundamentals(int tickerId, UsFundamentalsResponseDto usFundamentalsResponseDto) throws ServiceException {

        Set<TickerFundamentalsEntity> quarterlyEntities = parseFundamentalsIntoEntities(tickerId, 'q', usFundamentalsResponseDto.getQuarterlyIndicators());
        Set<TickerFundamentalsEntity> yearlyEntities = parseFundamentalsIntoEntities(tickerId, 'y', usFundamentalsResponseDto.getYearlyIndicators());

        saveEntities(quarterlyEntities);
        saveEntities(yearlyEntities);
    }

    private void saveEntities(Set<TickerFundamentalsEntity> entities) throws ServiceException {
        for ( TickerFundamentalsEntity entity : entities ) {
            saveEntity(entity);
        }
    }

    private void saveEntity(TickerFundamentalsEntity tickerFundamentalsEntity) throws ServiceException {
        // check if it already exists
        Optional<TickerFundamentalsEntity> existingEntityOpt = tickerFundamentalsRepository.findByTickerIdAndPeriodEndAndPeriodType(
                tickerFundamentalsEntity.getTickerId(), tickerFundamentalsEntity.getPeriodEnd(), tickerFundamentalsEntity.getPeriodType());

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

    private TreeSet<TickerFundamentalsEntity> parseFundamentalsIntoEntities(int tickerId, char periodType, Set<UsFundamentalIndicatorDto> indicators) {
        return indicators.stream()
                .map( ind -> new TickerFundamentalsEntity(
                    tickerId, ind.getEndPeriod(), periodType, ind.getEarningsPerShareBasic(),
                        ind.getCommonStockSharesOutstanding(), ind.getStockHoldersEquity(),
                        ind.getAssets(), ind.getLiabilities(), ind.getCashAndCashEquivalentsAtCarryingValue()
                ))
                .collect(Collectors.toCollection(TreeSet<TickerFundamentalsEntity>::new));
    }
}
