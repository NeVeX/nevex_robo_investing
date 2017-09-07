package com.nevex.investing.service;

import com.nevex.investing.database.StockPricesHistoricalRepository;
import com.nevex.investing.database.StockPricesRepository;
import com.nevex.investing.database.entity.StockPriceEntity;
import com.nevex.investing.database.entity.StockPriceHistoricalEntity;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.StockPrice;
import org.springframework.data.domain.PageRequest;
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

    public List<StockPrice> getHistoricalPrices(int tickerId, int maxDays) throws TickerNotFoundException {
        String symbol = tickerService.getSymbolForId(tickerId);
        return getHistoricalPrices(tickerId, symbol, maxDays);
    }

    public List<StockPrice> getHistoricalPrices(String symbol, int maxDays) throws TickerNotFoundException {
        int tickerId = tickerService.getIdForSymbol(symbol);
        return getHistoricalPrices(tickerId, maxDays);
    }

    private List<StockPrice> getHistoricalPrices(int tickerId, String tickerSymbol, int maxDays) {
        PageRequest pageRequest = new PageRequest(0, maxDays); // Get a year's worth
        List<StockPriceHistoricalEntity> historicalEntities = stockPricesHistoricalRepository.findAllByTickerId(tickerId, pageRequest);
        if ( historicalEntities == null || historicalEntities.isEmpty()) {
            return new ArrayList<>();
        }
        List<StockPrice> stockPrices = new ArrayList<>();
        historicalEntities.stream().forEach( h -> stockPrices.add(new StockPrice(tickerSymbol, h)));
        return stockPrices;
    }
}
