package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickersEntity;
import com.nevex.roboinvesting.service.model.PageableData;
import com.nevex.roboinvesting.service.model.StockExchange;
import com.nevex.roboinvesting.service.model.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class TickerService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerService.class);
    private final TickersRepository tickersRepository;
    private final static int DEFAULT_PAGE_ELEMENT_SIZE = 20;
    private final static PageRequest DEFAULT_PAGE_REQUEST = new PageRequest(0, DEFAULT_PAGE_ELEMENT_SIZE);

    public TickerService(TickersRepository tickersRepository) {
        if ( tickersRepository == null ) { throw new IllegalArgumentException("Provided ticker repository is null"); }
        this.tickersRepository = tickersRepository;
    }

    /**
     * Returns found tickers using a default pageable result
     */
    public PageableData<Ticker> getAllTickers() {
        return getAllTickers(DEFAULT_PAGE_REQUEST);
    }

    /**
     * Returns found tickers using the given page as the start
     */
    public PageableData<Ticker> getAllTickers(int page) {
        return getAllTickers(new PageRequest(page, DEFAULT_PAGE_ELEMENT_SIZE));
    }

    private PageableData<Ticker> getAllTickers(PageRequest pageRequest) {
        Page<TickersEntity> foundTickers = tickersRepository.findAll(pageRequest);

        if ( foundTickers == null || !foundTickers.hasContent()) {
            return PageableData.empty();
        }
        // Convert the tickers into our service domain
        List<Ticker> tickers = foundTickers.getContent().stream()
                .filter(tic -> StockExchange.fromId(tic.getStockExchange()).isPresent())
                .map(tic -> new Ticker(tic, StockExchange.fromId(tic.getStockExchange()).get()))
                .collect(Collectors.toList());
        return new PageableData<>(tickers, foundTickers);
    }

    /**
     * Tries to find the Ticker symbol for the given symbol
     */
    public Optional<Ticker> getTickerInformation(String inputSymbol) {
        String symbol = inputSymbol.toUpperCase(); // all symbols are upper case
        Optional<TickersEntity> foundTickerOpt = tickersRepository.findBySymbol(symbol);
        if ( foundTickerOpt.isPresent() ) {
            TickersEntity foundTicker = foundTickerOpt.get();
            Optional<StockExchange> stockExchangeOpt = StockExchange.fromId(foundTicker.getStockExchange());
            if ( stockExchangeOpt.isPresent() ) {
                Ticker ticker = new Ticker(foundTicker, stockExchangeOpt.get());
                return Optional.of(ticker);
            } else {
                LOGGER.warn("Could not parse stock exchange from ticker entity - this should not happen. [{}]", foundTicker);
            }
        }
        return Optional.empty();
    }

}
