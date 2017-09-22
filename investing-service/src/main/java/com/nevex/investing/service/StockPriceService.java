package com.nevex.investing.service;

import com.nevex.investing.database.StockPricesHistoricalRepository;
import com.nevex.investing.database.StockPricesRepository;
import com.nevex.investing.database.entity.StockPriceEntity;
import com.nevex.investing.database.entity.StockPriceHistoricalEntity;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.StockPrice;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
@Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
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
        return getHistoricalPrices(tickerId, LocalDate.now(), maxDays);
    }

    public List<StockPrice> getHistoricalPrices(String symbol, int maxDays) throws TickerNotFoundException {
        int tickerId = tickerService.getIdForSymbol(symbol);
        return getHistoricalPrices(tickerId, maxDays);
    }

    public List<StockPrice> getHistoricalPrices(int tickerId, LocalDate asOfDate, int maxDays) throws TickerNotFoundException {
        String symbol = tickerService.getSymbolForId(tickerId);
        return getHistoricalPrices(tickerId, symbol, asOfDate, maxDays);
    }

//    public List<StockPrice> getHistoricalPrices(String symbol, LocalDate asOfDate, int maxDays) throws TickerNotFoundException {
//        int tickerId = tickerService.getIdForSymbol(symbol);
//        return getHistoricalPrices(tickerId, asOfDate, maxDays);
//    }

    private List<StockPrice> getHistoricalPrices(int tickerId, String tickerSymbol, LocalDate asOfDate, int maxDays) {
        Pageable pageable = new PageRequest(0, maxDays, new Sort(Sort.Direction.DESC, StockPriceHistoricalEntity.DATE_COLUMN));
        List<StockPriceHistoricalEntity> historicalEntities = stockPricesHistoricalRepository.findAllByTickerIdAndDateLessThanEqual(tickerId, asOfDate, pageable);
        if ( historicalEntities == null || historicalEntities.isEmpty()) {
            return new ArrayList<>();
        }
        List<StockPrice> stockPrices = new ArrayList<>();
        historicalEntities.stream().forEach( h -> stockPrices.add(new StockPrice(tickerSymbol, h)));
        return stockPrices;
    }
}
