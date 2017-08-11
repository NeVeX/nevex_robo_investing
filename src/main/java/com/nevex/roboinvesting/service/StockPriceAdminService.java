package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.api.ApiStockPrice;
import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.entity.StockPricesEntity;
import com.nevex.roboinvesting.database.entity.StockPricesHistoricalEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class StockPriceAdminService extends StockPriceService {

    private final static Logger LOGGER = LoggerFactory.getLogger(StockPriceAdminService.class);

    public StockPriceAdminService(StockPricesRepository stockPricesRepository, StockPricesHistoricalRepository stockPricesHistoricalRepository) {
        super(stockPricesRepository, stockPricesHistoricalRepository);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewCurrentPrice(String symbol, ApiStockPrice price) {
        // save this to both the historical price and the current price
        saveHistoricalPrice(symbol, price);

        StockPricesEntity newCurrentPrice = convertToCurrentEntity(symbol, price);
        // Get the current one and update it
        Optional<StockPricesEntity> existingCurrentPriceOpt = stockPricesRepository.findBySymbol(symbol);
        if ( existingCurrentPriceOpt.isPresent()) {
            StockPricesEntity existingCurrentPrice = existingCurrentPriceOpt.get();
            existingCurrentPrice.merge(newCurrentPrice); // merge in any changes
            newCurrentPrice = existingCurrentPrice; // swap!
        }

        if ( stockPricesRepository.save(newCurrentPrice) == null) {
            LOGGER.warn("Could not save the current stock price entity [{}]", newCurrentPrice);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <E extends ApiStockPrice> void saveHistoricalPrices(String symbol, Set<E> historicalPrices) {

        // delete all records we currently have
        stockPricesHistoricalRepository.deleteBySymbol(symbol);

        Set<StockPricesHistoricalEntity> newEntitiesToSave = new HashSet<>();

        // We have data to save each one
        newEntitiesToSave.addAll(
                historicalPrices.stream().map(tPrice -> convertToHistoricalEntity(symbol, tPrice))
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
        // get the first one, that will be the "latest" using the comparable
        orderPrices.stream().findFirst().ifPresent(p -> saveNewCurrentPrice(symbol, p));
    }

    private void saveHistoricalPrice(String symbol, ApiStockPrice price) {
        // now insert all our records
        StockPricesHistoricalEntity newHistoryEntity = convertToHistoricalEntity(symbol, price);

        // check if we already have this saved
        Optional<StockPricesHistoricalEntity> existingHistPriceOpt =
                stockPricesHistoricalRepository.findBySymbolAndDate(symbol, price.getDate().toLocalDate());

        if ( existingHistPriceOpt.isPresent()) {
            StockPricesHistoricalEntity existingHistPrice = existingHistPriceOpt.get();
            LOGGER.info("Merging found existing entity for historical price [{}]", existingHistPrice);
            existingHistPrice.merge(newHistoryEntity);
            newHistoryEntity = existingHistPrice; // swap what we should save
        }

        if ( stockPricesHistoricalRepository.save(newHistoryEntity) == null) {
            LOGGER.warn("Could not save the historical stock price entity [{}]", newHistoryEntity);
        }
    }

    // TODO: merge these 2 entity models
    private StockPricesHistoricalEntity convertToHistoricalEntity(String symbol, ApiStockPrice tPrice) {

        StockPricesHistoricalEntity entity = new StockPricesHistoricalEntity();
        entity.setSymbol(symbol);
        entity.setDate(tPrice.getDate().toLocalDate());

        entity.setClose(tPrice.getClose());
        entity.setOpen(tPrice.getOpen());
        entity.setHigh(tPrice.getHigh());
        entity.setLow(tPrice.getLow());
        entity.setVolume(tPrice.getVolume());

        entity.setAdjClose(tPrice.getAdjClose());
        entity.setAdjHigh(tPrice.getAdjHigh());
        entity.setAdjLow(tPrice.getAdjLow());
        entity.setAdjOpen(tPrice.getAdjOpen());
        entity.setAdjVolume(tPrice.getAdjVolume());

        entity.setDividendCash(tPrice.getDivCash());
        entity.setSplitFactor(tPrice.getSplitFactor());
        return entity;

    }

    private StockPricesEntity convertToCurrentEntity(String symbol, ApiStockPrice tPrice) {

        StockPricesEntity entity = new StockPricesEntity();
        entity.setSymbol(symbol);
        entity.setDate(tPrice.getDate().toLocalDate());

        entity.setClose(tPrice.getClose());
        entity.setOpen(tPrice.getOpen());
        entity.setHigh(tPrice.getHigh());
        entity.setLow(tPrice.getLow());
        entity.setVolume(tPrice.getVolume());

        entity.setAdjClose(tPrice.getAdjClose());
        entity.setAdjHigh(tPrice.getAdjHigh());
        entity.setAdjLow(tPrice.getAdjLow());
        entity.setAdjOpen(tPrice.getAdjOpen());
        entity.setAdjVolume(tPrice.getAdjVolume());

        entity.setDividendCash(tPrice.getDivCash());
        entity.setSplitFactor(tPrice.getSplitFactor());
        return entity;

    }


}
