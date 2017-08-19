package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.entity.StockPriceEntity;
import com.nevex.roboinvesting.database.entity.StockPriceHistoricalEntity;
import com.nevex.roboinvesting.service.exception.TickerNotFoundException;
import com.nevex.roboinvesting.service.model.StockPrice;
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
    final TickerService tickerService;

    public StockPriceService(
            TickerService tickerService,
            StockPricesRepository stockPricesRepository,
            StockPricesHistoricalRepository stockPricesHistoricalRepository) {
        if ( stockPricesRepository == null) { throw new IllegalArgumentException("Provided stock price repository is null"); }
        if ( stockPricesHistoricalRepository == null) { throw new IllegalArgumentException("Provided stock price historical repository is null"); }
        if ( tickerService == null) { throw new IllegalArgumentException("Provided tickerService is null"); }
        this.stockPricesRepository = stockPricesRepository;
        this.stockPricesHistoricalRepository = stockPricesHistoricalRepository;
        this.tickerService = tickerService;
    }

    public Optional<StockPrice> getCurrentPrice(String symbol) throws TickerNotFoundException {
        int tickerId = tickerService.getIdForSymbol(symbol);
        Optional<StockPriceEntity> stockPriceOpt = stockPricesRepository.findByTickerId(tickerId);
        if ( !stockPriceOpt.isPresent()) {
            return Optional.empty();
        }
        StockPriceEntity pricesEntity = stockPriceOpt.get();
        StockPrice stockPrice = new StockPrice(symbol, pricesEntity);
        return Optional.of(stockPrice);
    }

    // TODO: Limit the search
    public List<StockPrice> getHistoricalPrices(String symbol) throws TickerNotFoundException {
        int tickerId = tickerService.getIdForSymbol(symbol);
        List<StockPriceHistoricalEntity> historicalEntities = stockPricesHistoricalRepository.findAllByTickerId(tickerId);
        if ( historicalEntities == null || historicalEntities.isEmpty()) {
            return new ArrayList<>();
        }
        List<StockPrice> stockPrices = new ArrayList<>();
        historicalEntities.stream().forEach( h -> stockPrices.add(new StockPrice(symbol, h)));
        return stockPrices;
    }

}
