package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.TickerCache;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickerEntity;
import com.nevex.roboinvesting.service.model.PageableData;
import com.nevex.roboinvesting.service.model.StockExchange;
import com.nevex.roboinvesting.service.model.Ticker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Transactional(readOnly = true)
public class TickerService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerService.class);
    private final TickersRepository tickersRepository;
    private final static int DEFAULT_PAGE_ELEMENT_SIZE = 20;
    private final static PageRequest DEFAULT_PAGE_REQUEST = new PageRequest(0, DEFAULT_PAGE_ELEMENT_SIZE);
    private Set<Ticker> allTickers = new HashSet<>();
    private final ReadWriteLock tickersLock = new ReentrantReadWriteLock();

    public TickerService(TickersRepository tickersRepository) {
        if ( tickersRepository == null ) { throw new IllegalArgumentException("Provided ticker repository is null"); }
        this.tickersRepository = tickersRepository;

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable refreshTickersTask = new Runnable() {
            public void run() {
                refreshAllTickers();
            }
        };
        executor.scheduleAtFixedRate(refreshTickersTask, 10, TimeUnit.MINUTES.toSeconds(10), TimeUnit.SECONDS);
    }

    public void refreshAllTickers() {
        Map<String, Integer> symbolToTickerIdMap = new HashMap<>();
        Set<Ticker> newTickers = new HashSet<>();
        for ( TickerEntity te : tickersRepository.findAll()) {
            symbolToTickerIdMap.put(te.getSymbol(), te.getId());
            newTickers.add(new Ticker(te, StockExchange.fromId(te.getStockExchange()).get()));
        }
        TickerCache.update(symbolToTickerIdMap);
        try {
            tickersLock.writeLock().lock();
            allTickers = newTickers;
        } finally {
            tickersLock.writeLock().unlock();
        }
        LOGGER.info("Refreshed all tickers");
    }

    public List<Ticker> searchForTicker(String name) {
        try {
            if ( tickersLock.readLock().tryLock(3, TimeUnit.SECONDS) ) {
                return allTickers.parallelStream()
                        .filter(t -> StringUtils.containsIgnoreCase(t.getSymbol(), name) || StringUtils.containsIgnoreCase(t.getName(), name))
                        .collect(Collectors.toList());
            }
        } catch (Exception e ) {
            // for now nothing...
        } finally {
            tickersLock.readLock().unlock();
        }
        return new ArrayList<>();
    }

    /**
     * Returns found tickers using a default pageable result
     */
    public PageableData<Ticker> getTickers() {
        return getTickers(DEFAULT_PAGE_REQUEST);
    }

    /**
     * Returns found tickers using the given page as the start
     */
    public PageableData<Ticker> getTickers(int page) {
        return getTickers(new PageRequest(page, DEFAULT_PAGE_ELEMENT_SIZE));
    }

    private PageableData<Ticker> getTickers(PageRequest pageRequest) {
        Page<TickerEntity> foundTickers = tickersRepository.findAll(pageRequest);

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
        Optional<TickerEntity> foundTickerOpt = tickersRepository.findBySymbol(symbol);
        if ( foundTickerOpt.isPresent() ) {
            TickerEntity foundTicker = foundTickerOpt.get();
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
