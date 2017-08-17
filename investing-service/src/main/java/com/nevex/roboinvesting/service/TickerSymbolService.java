package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickersEntity;
import com.nevex.roboinvesting.service.model.StockExchange;
import com.nevex.roboinvesting.service.model.TickerSymbol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class TickerSymbolService {

    private final TickersRepository tickersRepository;

    public TickerSymbolService(TickersRepository tickersRepository) {
        if ( tickersRepository == null ) { throw new IllegalArgumentException("Provided ticker repository is null"); }
        this.tickersRepository = tickersRepository;
    }

    // TODO: Return page data etc....and implement this much better
    public List<TickerSymbol> getAllTickers() {
        List<TickerSymbol> tickers = new ArrayList<>();
        Pageable pageable = new PageRequest(0, 20);
        Page<TickersEntity> foundTickers = tickersRepository.findAll(pageable);
        if ( foundTickers != null && foundTickers.hasContent()) {
            // TODO: don't use the foreach due to side affects
            foundTickers.forEach(t -> tickers.add(new TickerSymbol(t, StockExchange.fromId(t.getStockExchange()))));
        }
        return tickers;
    }

    /**
     * Tries to find the Ticker symbol for the given symbol
     */
    public Optional<TickerSymbol> getTickerInformation(String inputSymbol) {
        String symbol = inputSymbol.toUpperCase(); // all symbols are upper case
        Optional<TickersEntity> foundTickerOpt = tickersRepository.findBySymbol(symbol);
        if ( foundTickerOpt.isPresent() ) {
            TickersEntity foundTicker = foundTickerOpt.get();
            TickerSymbol tickerSymbol = new TickerSymbol(foundTicker, StockExchange.fromId(foundTicker.getStockExchange()));
            return Optional.of(tickerSymbol);
        }
        return Optional.empty();
    }



}
