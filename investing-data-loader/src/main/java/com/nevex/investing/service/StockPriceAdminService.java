package com.nevex.investing.service;

import com.nevex.investing.api.ApiStockPrice;
import com.nevex.investing.database.StockPricePeriodSummaryRepository;
import com.nevex.investing.database.StockPricesHistoricalRepository;
import com.nevex.investing.database.StockPricesRepository;
import com.nevex.investing.database.entity.StockPriceBaseEntity;
import com.nevex.investing.database.entity.StockPricePeriodSummaryEntity;
import com.nevex.investing.database.entity.StockPriceEntity;
import com.nevex.investing.database.entity.StockPriceHistoricalEntity;
import com.nevex.investing.database.model.DataSaveException;
import com.nevex.investing.database.utils.RepositoryUtils;
import com.nevex.investing.model.TimePeriod;
import com.nevex.investing.model.StockPriceSummary;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class StockPriceAdminService extends StockPriceService {

    private final static Logger LOGGER = LoggerFactory.getLogger(StockPriceAdminService.class);
    private final StockPricePeriodSummaryRepository stockPricePeriodSummaryRepository;

    public StockPriceAdminService(TickerService tickerService,
                                  StockPricesRepository stockPricesRepository,
                                  StockPricesHistoricalRepository stockPricesHistoricalRepository,
                                  StockPricePeriodSummaryRepository stockPricePeriodSummaryRepository) {
        super(tickerService, stockPricesRepository, stockPricesHistoricalRepository);
        if ( stockPricePeriodSummaryRepository == null ) { throw new IllegalArgumentException("Provided stockPriceChangeTrackerRepository is null"); }
        this.stockPricePeriodSummaryRepository = stockPricePeriodSummaryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewCurrentPrice(String symbol, ApiStockPrice price) throws TickerNotFoundException, ServiceException {
        int tickerId = tickerService.getIdForSymbol(symbol);
        // save this to both the historical price and the current price
        saveHistoricalPrice(tickerId, price);
        updateCurrentPriceAfterHistoricalPriceChanges(tickerId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <E extends ApiStockPrice> void saveHistoricalPrices(String symbol, Set<E> historicalPrices) throws TickerNotFoundException, ServiceException {
        int tickerId = tickerService.getIdForSymbol(symbol);
        historicalPrices.stream().forEach(price -> saveHistoricalPrice(tickerId, price));

        updateCurrentPriceAfterHistoricalPriceChanges(tickerId);
    }

    private void updateCurrentPriceAfterHistoricalPriceChanges(int tickerId) throws ServiceException {

        Optional<StockPriceHistoricalEntity> latestHistoricalPriceOpt = stockPricesHistoricalRepository.findTopByTickerIdOrderByDateDesc(tickerId);

        if ( !latestHistoricalPriceOpt.isPresent()) {
            LOGGER.warn("Could not get latest historical price for ticker [{}] to update it's current price", tickerId);
            return; // nothing to do
        }

        StockPriceHistoricalEntity latestHistoricalPrice = latestHistoricalPriceOpt.get();

        Optional<StockPriceEntity> latestCurrentPriceOpt = stockPricesRepository.findByTickerId(tickerId);
        StockPriceEntity latestPrice;

        if ( !latestCurrentPriceOpt.isPresent()) {
            latestPrice = StockPriceEntity.of(latestHistoricalPrice);
        } else {
            latestPrice = latestCurrentPriceOpt.get();
            latestPrice.from(latestHistoricalPrice);
        }

        try {
            stockPricesRepository.save(latestPrice);
        } catch (Exception e) {
            throw new ServiceException("Could not save the current stock price entity ["+ latestPrice +"]", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePriceChanges(int tickerId, TimePeriod timePeriod, StockPriceSummary summary) throws ServiceException {

        String periodName = timePeriod.getTitle();
        StockPricePeriodSummaryEntity newEntity = new StockPricePeriodSummaryEntity(tickerId, timePeriod, summary);

        try {
            RepositoryUtils.createOrUpdate(stockPricePeriodSummaryRepository, newEntity,
                    () -> stockPricePeriodSummaryRepository.findByTickerIdAndPeriodName(tickerId, periodName));
        } catch (DataSaveException dataEx) {
            throw new ServiceException("Could not save stock price change entity ["+dataEx.getDataFailedToSave()+"]", dataEx);
        }
    }

    private void saveHistoricalPrice(int tickerId, ApiStockPrice price) {
        // now insert all our records
        StockPriceHistoricalEntity newHistoryEntityToSave = convertToHistoricalEntity(tickerId, price);
        try {
            RepositoryUtils.createOrUpdate(stockPricesHistoricalRepository, newHistoryEntityToSave,
                    () -> stockPricesHistoricalRepository.findByTickerIdAndDate(tickerId, price.getDate()));
        } catch (DataSaveException dataEx) {
            LOGGER.warn("Could not create or update historical price for entity [{}]. Reason: [{}]", dataEx.getDataFailedToSave(), dataEx.getMessage());
        }
    }

    private StockPriceHistoricalEntity convertToHistoricalEntity(int tickerId, ApiStockPrice tPrice) {
        StockPriceHistoricalEntity entity = new StockPriceHistoricalEntity();
        populateEntity(entity, tickerId, tPrice);
        return entity;
    }

    private StockPriceEntity convertToCurrentEntity(int tickerId, ApiStockPrice tPrice) {
        StockPriceEntity entity = new StockPriceEntity();
        populateEntity(entity, tickerId, tPrice);
        return entity;
    }

    // TODO: Move this into the entity
    private void populateEntity(StockPriceBaseEntity baseEntity, int tickerId, ApiStockPrice tPrice) {
        baseEntity.setTickerId(tickerId);
        baseEntity.setDate(tPrice.getDate());
        baseEntity.setClose(tPrice.getClose());
        baseEntity.setOpen(tPrice.getOpen());
        baseEntity.setHigh(tPrice.getHigh());
        baseEntity.setLow(tPrice.getLow());
        baseEntity.setVolume(tPrice.getVolume());
        baseEntity.setAdjClose(tPrice.getAdjustedClose());
    }
}
