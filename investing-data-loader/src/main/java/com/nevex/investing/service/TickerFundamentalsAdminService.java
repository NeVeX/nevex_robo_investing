package com.nevex.investing.service;

import com.nevex.investing.api.usfundamentals.model.UsFundamentalIndicatorDto;
import com.nevex.investing.api.usfundamentals.model.UsFundamentalsResponseDto;
import com.nevex.investing.database.TickerFundamentalsRepository;
import com.nevex.investing.database.TickerFundamentalsSyncRepository;
import com.nevex.investing.database.entity.TickerFundamentalsEntity;
import com.nevex.investing.database.entity.TickerFundamentalsSyncEntity;
import com.nevex.investing.service.model.TickerFundamentalsSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final TickerFundamentalsSyncRepository tickerFundamentalsSyncRepository;

    public TickerFundamentalsAdminService(TickerFundamentalsRepository tickerFundamentalsRepository, TickerFundamentalsSyncRepository tickerFundamentalsSyncRepository) {
        if ( tickerFundamentalsRepository == null ) { throw new IllegalArgumentException("Provided tickerFundamentalsRepository is null"); }
        if ( tickerFundamentalsSyncRepository == null ) { throw new IllegalArgumentException("Provided tickerFundamentalsSyncRepository is null"); }
        this.tickerFundamentalsRepository = tickerFundamentalsRepository;
        this.tickerFundamentalsSyncRepository = tickerFundamentalsSyncRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public Optional<TickerFundamentalsEntity> getFundamentals(int tickerId, LocalDate localDate, char periodType) {
        return tickerFundamentalsRepository.findByTickerIdAndPeriodEndAndPeriodType(tickerId, localDate, periodType);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHistoricalFundamentals(int tickerId, UsFundamentalsResponseDto usFundamentalsResponseDto) throws ServiceException {

        Set<TickerFundamentalsEntity> quarterlyEntities = parseFundamentalsIntoEntities(tickerId, 'q', usFundamentalsResponseDto.getQuarterlyIndicators());
        Set<TickerFundamentalsEntity> yearlyEntities = parseFundamentalsIntoEntities(tickerId, 'y', usFundamentalsResponseDto.getYearlyIndicators());

        saveEntities(quarterlyEntities);
        saveEntities(yearlyEntities);
    }

//    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
//    public Optional<TickerFundamentalsSync> getFundamentalsSyncForTickerId(int tickerId) {
//        Optional<TickerFundamentalsSyncEntity> foundEntity = tickerFundamentalsSyncRepository.findByTickerId(tickerId);
//        if ( !foundEntity.isPresent()) {
//            return Optional.empty();
//        }
//        TickerFundamentalsSyncEntity entity = foundEntity.get();
//        TickerFundamentalsSync syncData = new TickerFundamentalsSync(entity.getId(), entity.getTickerId(), entity.getInitialDownloadNano(), entity.getLastUpdateId());
//        return Optional.of(syncData);
//    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public Optional<Long> getLatestNanoTime() {
        PageRequest earliestNanoRequest = new PageRequest(0, 1, new Sort(Sort.Direction.DESC, "id"));

        Page<TickerFundamentalsSyncEntity> page = tickerFundamentalsSyncRepository.findAll(earliestNanoRequest);
        if ( page != null && page.hasContent()) {
            Optional<TickerFundamentalsSyncEntity> firstEntity = page.getContent().stream().findFirst();
            if ( firstEntity.isPresent()) {
                return Optional.of(firstEntity.get().getNanoSeconds());
            }
        }
        return Optional.empty();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSync(long nanoSeconds) throws ServiceException {
        TickerFundamentalsSyncEntity syncEntity = new TickerFundamentalsSyncEntity(nanoSeconds);
        try {
            tickerFundamentalsSyncRepository.save(syncEntity);
        } catch (Exception e) {
            throw new ServiceException("Could not save sync entity for fundamentals ["+syncEntity+"]", e);
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

    private TreeSet<TickerFundamentalsEntity> parseFundamentalsIntoEntities(int tickerId, char periodType, Set<UsFundamentalIndicatorDto> indicators) {
        return indicators.stream()
                .map( ind -> new TickerFundamentalsEntity(
                    tickerId, ind.getEndPeriod(), periodType, ind.getEarningsPerShareBasic(),
                        ind.getCommonStockSharesOutstanding(), ind.getStockHoldersEquity()
                ))
                .collect(Collectors.toCollection(TreeSet<TickerFundamentalsEntity>::new));
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void updateFundamentalsSync(TickerFundamentalsSync tickerFundamentalsSync) throws ServiceException {
//        TickerFundamentalsSyncEntity syncEntity = new TickerFundamentalsSyncEntity(tickerFundamentalsSync);
//        try {
//            tickerFundamentalsSyncRepository.save(syncEntity);
//        } catch (Exception e) {
//            throw new ServiceException("Could not save updated sync entity ["+syncEntity+"]", e);
//        }
//    }
}
