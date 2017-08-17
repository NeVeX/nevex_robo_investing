package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.entity.StockPricesEntity;
import com.nevex.roboinvesting.database.entity.StockPricesHistoricalEntity;
import com.nevex.roboinvesting.service.model.StockPrice;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class StockPriceService {

    protected final StockPricesHistoricalRepository stockPricesHistoricalRepository;
    protected final StockPricesRepository stockPricesRepository;

    public StockPriceService(StockPricesRepository stockPricesRepository,
                             StockPricesHistoricalRepository stockPricesHistoricalRepository) {
        if ( stockPricesRepository == null) { throw new IllegalArgumentException("Provided stock price repository is null"); }
        if ( stockPricesHistoricalRepository == null) { throw new IllegalArgumentException("Provided stock price historical repository is null"); }
        this.stockPricesRepository = stockPricesRepository;
        this.stockPricesHistoricalRepository = stockPricesHistoricalRepository;
    }

    @Transactional(readOnly = true)
    public Optional<StockPrice> getCurrentPrice(String inputSymbol) {
        String ticker = inputSymbol.toUpperCase();
        Optional<StockPricesEntity> stockPriceOpt = stockPricesRepository.findBySymbol(ticker);
        if ( !stockPriceOpt.isPresent()) {
            return Optional.empty();
        }
        StockPricesEntity pricesEntity = stockPriceOpt.get();
        StockPrice stockPrice = new StockPrice(pricesEntity);
        return Optional.of(stockPrice);
    }

    // TODO: Limit the search
    @Transactional(readOnly = true)
    public List<StockPrice> getHistoricalPrices(String inputSymbol) {
        String ticker = inputSymbol.toUpperCase();
        List<StockPricesHistoricalEntity> historicalEntities = stockPricesHistoricalRepository.findAllBySymbol(ticker);
        if ( historicalEntities == null || historicalEntities.isEmpty()) {
            return new ArrayList<>();
        }
        List<StockPrice> stockPrices = new ArrayList<>();
        historicalEntities.stream().forEach( h -> stockPrices.add(new StockPrice(h)));
        return stockPrices;
    }

}
