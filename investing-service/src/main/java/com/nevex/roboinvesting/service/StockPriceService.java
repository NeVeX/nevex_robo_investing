package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.TickerCache;
import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.entity.StockPriceEntity;
import com.nevex.roboinvesting.database.entity.StockPriceHistoricalEntity;
import com.nevex.roboinvesting.service.exception.TickerNotFoundException;
import com.nevex.roboinvesting.service.model.StockPrice;
import com.nevex.roboinvesting.service.model.Ticker;
import org.omg.CORBA.TIMEOUT;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
@Transactional(readOnly = true)
public class StockPriceService {

    final StockPricesHistoricalRepository stockPricesHistoricalRepository;
    final StockPricesRepository stockPricesRepository;

    public StockPriceService(StockPricesRepository stockPricesRepository,
                             StockPricesHistoricalRepository stockPricesHistoricalRepository) {
        if ( stockPricesRepository == null) { throw new IllegalArgumentException("Provided stock price repository is null"); }
        if ( stockPricesHistoricalRepository == null) { throw new IllegalArgumentException("Provided stock price historical repository is null"); }
        this.stockPricesRepository = stockPricesRepository;
        this.stockPricesHistoricalRepository = stockPricesHistoricalRepository;
    }

    public Optional<StockPrice> getCurrentPrice(String symbol) throws TickerNotFoundException {
        int tickerId = getIdForSymbol(symbol);
        Optional<StockPriceEntity> stockPriceOpt = stockPricesRepository.findByTickerId(tickerId);
        if ( !stockPriceOpt.isPresent()) {
            return Optional.empty();
        }
        StockPriceEntity pricesEntity = stockPriceOpt.get();
        StockPrice stockPrice = new StockPrice(pricesEntity);
        return Optional.of(stockPrice);
    }

    // TODO: Limit the search
    public List<StockPrice> getHistoricalPrices(String symbol) throws TickerNotFoundException {
        int tickerId = getIdForSymbol(symbol);
        List<StockPriceHistoricalEntity> historicalEntities = stockPricesHistoricalRepository.findAllByTickerId(tickerId);
        if ( historicalEntities == null || historicalEntities.isEmpty()) {
            return new ArrayList<>();
        }
        List<StockPrice> stockPrices = new ArrayList<>();
        historicalEntities.stream().forEach( h -> stockPrices.add(new StockPrice(h)));
        return stockPrices;
    }

    // Returns the tickerId for symbol, or throws an exception if not found
    private int getIdForSymbol(String symbol) throws TickerNotFoundException {
        Optional<Integer> foundIdOpt = TickerCache.getIdForSymbol(symbol);
        if (foundIdOpt.isPresent()) {
            return foundIdOpt.get();
        }
        throw new TickerNotFoundException(symbol);
    }

}
