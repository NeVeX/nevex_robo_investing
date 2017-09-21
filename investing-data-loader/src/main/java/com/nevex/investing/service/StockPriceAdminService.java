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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

        StockPriceEntity newCurrentPrice = convertToCurrentEntity(tickerId, price);
        // Get the current one, if there is one
        Optional<StockPriceEntity> existingCurrentPriceOpt = stockPricesRepository.findByTickerId(tickerId);
        if ( existingCurrentPriceOpt.isPresent()) {
            StockPriceEntity existingCurrentPrice = existingCurrentPriceOpt.get();

            // See if the existing price is newer than what we are trying to save
            if ( existingCurrentPrice.getDate().isAfter(newCurrentPrice.getDate()) ) {
                // this is bad - we don't want to overwrite a future price with an older price
                LOGGER.warn("Will not save current price for [{}] since the current price in the db is newer than the price trying to save", symbol);
                return;
            }

            existingCurrentPrice.merge(newCurrentPrice); // merge in any changes
            newCurrentPrice = existingCurrentPrice; // swap!
        }

        try {
            stockPricesRepository.save(newCurrentPrice);
        } catch (Exception e) {
            throw new ServiceException("Could not save the current stock price entity ["+ newCurrentPrice +"]", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <E extends ApiStockPrice> void saveHistoricalPrices(String symbol, Set<E> historicalPrices) throws TickerNotFoundException, ServiceException {

        int tickerId = tickerService.getIdForSymbol(symbol);

        // delete all records we currently have
        stockPricesHistoricalRepository.deleteByTickerId(tickerId);

        Set<StockPriceHistoricalEntity> newEntitiesToSave = new HashSet<>();

        // We have data to save each one - duplicates will be removed by the entity equals/hash
        newEntitiesToSave.addAll(
                historicalPrices.stream().map(tPrice -> convertToHistoricalEntity(tickerId, tPrice))
                .collect(Collectors.toList())
        );

        if ( newEntitiesToSave.size() < historicalPrices.size()) {
            LOGGER.warn("Some [{}] duplicate historical prices were removed from the given API stock prices",
                    historicalPrices.size() - newEntitiesToSave.size());
        }

        // Bulk save
        stockPricesHistoricalRepository.save(newEntitiesToSave);

        // Order the prices to get the latest one
        TreeSet<E> orderPrices = new TreeSet<>(historicalPrices);
        // get the first one, that will be the "latest" using the comparable - can't use streams due to exceptions
        E currentPrice = orderPrices.first();
        if ( currentPrice != null ) { saveNewCurrentPrice(symbol, currentPrice); }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePriceChanges(int tickerId, TimePeriod timePeriod, StockPriceSummary summary) throws ServiceException {

        String periodName = timePeriod.getTitle();
        StockPricePeriodSummaryEntity newEntity = new StockPricePeriodSummaryEntity(tickerId, timePeriod, summary, LocalDate.now());

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
            LOGGER.warn("Could not save the historical stock price entity [{}]", dataEx.getDataFailedToSave());
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
