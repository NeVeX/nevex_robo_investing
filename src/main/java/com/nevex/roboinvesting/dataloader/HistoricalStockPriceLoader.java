package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.api.tiingo.TiingoApiClient;
import com.nevex.roboinvesting.api.tiingo.model.TiingoPriceDto;
import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.StockPricesHistoricalEntity;
import com.nevex.roboinvesting.database.entity.TickersEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class HistoricalStockPriceLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(HistoricalStockPriceLoader.class);
    private final static int DEFAULT_MAX_DAYS_HISTORICAL = 365;
    private final TickersRepository tickersRepository;
    private final TiingoApiClient tiingoApiClient;
    private final StockPricesHistoricalRepository stockPricesHistoricalRepository;

    public HistoricalStockPriceLoader(TickersRepository tickersRepository,
                                      TiingoApiClient tiingoApiClient,
                                      StockPricesHistoricalRepository stockPricesHistoricalRepository) {
        if ( tickersRepository == null) { throw new IllegalArgumentException("Provided tickers repository is null"); }
        if ( tiingoApiClient == null) { throw new IllegalArgumentException("Provided tiingoApiClient is null"); }
        if ( stockPricesHistoricalRepository == null) { throw new IllegalArgumentException("Provided stockPricesHistoricalRepository is null"); }
        this.tickersRepository = tickersRepository;
        this.tiingoApiClient = tiingoApiClient;
        this.stockPricesHistoricalRepository = stockPricesHistoricalRepository;
    }

    @Override
    @Transactional
    public void doWork() {
        LOGGER.info("{} will start to do it's work", this.getClass());

        LOGGER.info("{} has completed all it's work", this.getClass());
    }

    public void loadHistoricalPricesForSymbol(String inputSymbol) {

        String symbol = inputSymbol.toUpperCase();
        Optional<TickersEntity> tickersEntityOpt = tickersRepository.findBySymbol(symbol);
        if ( !tickersEntityOpt.isPresent() ) {
            LOGGER.warn("Cannot load historical data for unknown symbol [{}]", symbol);
            return;
        }
        TickersEntity tickersEntity = tickersEntityOpt.get();
        List<TiingoPriceDto> historicalPrices;

        try {
            historicalPrices = tiingoApiClient.getHistoricalPricesForSymbol(tickersEntity.getSymbol(), DEFAULT_MAX_DAYS_HISTORICAL);
        } catch (Exception e ) {
            LOGGER.error("Could not get historical prices for symbol [{}]", tickersEntity.getSymbol(), e);
            return;
        }

        if ( historicalPrices == null || historicalPrices.isEmpty()) {
            LOGGER.warn("Received no historical prices for stock [{}]", tickersEntity.getSymbol());
            return;
        }

        // We have data to save each one
        for ( TiingoPriceDto tPrice : historicalPrices ) {
            StockPricesHistoricalEntity historyEntity = covertToEntity(tickersEntity.getSymbol(), tPrice);
            if ( stockPricesHistoricalRepository.save(historyEntity) == null) {
                LOGGER.warn("Could not save the historical stock price entity [{}]", historyEntity);
            }
        }

        LOGGER.info("Successfully loaded [{}] historical prices for [{}]", historicalPrices.size(), symbol);

    }

    private StockPricesHistoricalEntity covertToEntity(String symbol, TiingoPriceDto tPrice) {

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

    @Override
    public int orderNumber() {
        return DataLoaderOrder.STOCK_PRICE_HISTORICAL_LOADER;
    }

}
