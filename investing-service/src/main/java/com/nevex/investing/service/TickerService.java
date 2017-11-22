package com.nevex.investing.service;

import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.service.exception.TickerNotFoundException;
import com.nevex.investing.service.model.PageableData;
import com.nevex.investing.service.model.StockExchange;
import com.nevex.investing.service.model.Ticker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
public class TickerService implements ApplicationListener<ApplicationReadyEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerService.class);
    private final ConcurrentHashMap<String, Integer> symbolToTickerIdMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, String> tickerIdToSymbolMap = new ConcurrentHashMap<>();
    private final static int DEFAULT_PAGE_ELEMENT_SIZE = 20;
    private final static Pageable DEFAULT_PAGEABLE = new PageRequest(0, DEFAULT_PAGE_ELEMENT_SIZE);
    protected final TickersRepository tickersRepository;
    private final ReadWriteLock tickersLock = new ReentrantReadWriteLock();
    private Set<Ticker> allTickers = new HashSet<>();

    public TickerService(TickersRepository tickersRepository) {
        if ( tickersRepository == null ) { throw new IllegalArgumentException("Provided ticker repository is null"); }
        this.tickersRepository = tickersRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        refreshAllTickers();
    }

    public Set<Ticker> getCachedTickers() {
        return allTickers;
    }

    public Optional<Integer> tryGetIdForSymbol(String symbol) {
        String ticker = symbol.toUpperCase();
        Integer tickerId = symbolToTickerIdMap.get(ticker);
        return Optional.ofNullable(tickerId);
    }

    public Optional<String> tryGetSymbolForId(int tickerId) {
        String tickerSymbol = tickerIdToSymbolMap.get(tickerId);
        return Optional.ofNullable(tickerSymbol);
    }

    // Returns the tickerId for symbol, or throws an exception if not found
    public String getSymbolForId(int tickerId) throws TickerNotFoundException {
        Optional<String> foundIdOpt = tryGetSymbolForId(tickerId);
        if (foundIdOpt.isPresent()) {
            return foundIdOpt.get();
        }
        throw new TickerNotFoundException(tickerId);
    }

    // Returns the tickerId for symbol, or throws an exception if not found
    public int getIdForSymbol(String symbol) throws TickerNotFoundException {
        Optional<Integer> foundIdOpt = tryGetIdForSymbol(symbol);
        if (foundIdOpt.isPresent()) {
            return foundIdOpt.get();
        }
        throw new TickerNotFoundException(symbol);
    }

    void updateInternalTickerMaps(Map<String, Integer> newSymbolsToIds) {
        Map<Integer, String> tickerIdToSymbols = newSymbolsToIds
                .entrySet()
                .stream().map( e -> new AbstractMap.SimpleEntry<>(e.getValue(), e.getKey()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        symbolToTickerIdMap.putAll(newSymbolsToIds);
        tickerIdToSymbolMap.putAll(tickerIdToSymbols);
    }

    protected int refreshAllTickers() {
        return refreshAllTickers(tickersRepository.findAllByIsTradableTrue());
    }

    protected int refreshAllTickers(Iterable<TickerEntity> tickerIter) {
        Map<String, Integer> symbolToTickerIdMap = new HashMap<>();
        Set<Ticker> newTickers = new HashSet<>();

        for ( TickerEntity te : tickerIter) {
            symbolToTickerIdMap.put(te.getSymbol(), te.getId());
            newTickers.add(new Ticker(te, StockExchange.fromId(te.getStockExchange()).get()));
        }

        try {
            // Since we need to blindly update the full list, we'll just get a write lock and well, blindly update the set
            tickersLock.writeLock().lock();
            int previousCount = allTickers.size();
            allTickers = newTickers;
            updateInternalTickerMaps(symbolToTickerIdMap);
            LOGGER.info("Refreshed [{}] tickers into the cache of the previous size of [{}]", allTickers.size(), previousCount);
        } finally {
            tickersLock.writeLock().unlock();
        }
        LOGGER.info("Refreshed all tickers");
        return allTickers.size();
    }

    // TODO: Need to search by ticker first
    public Set<Ticker> searchForTicker(String name) {
        int limit = 10;
        try {
            // Try to get a read lock
            if ( tickersLock.readLock().tryLock(3, TimeUnit.SECONDS) ) {
                Set<Ticker> tickersFound = performSearchOnTickers(t -> StringUtils.containsIgnoreCase(t.getSymbol(), name), 10);
                if ( tickersFound.size() < limit) {
                    // Try and fill up the search with a filter on the name - only getting enough to fill to our defined limit
                    tickersFound.addAll(performSearchOnTickers(t -> StringUtils.containsIgnoreCase(t.getName(), name), limit - tickersFound.size()));
                }
                return tickersFound;
            }
        } catch (Exception e ) {
            LOGGER.error("An exception occurred while searching for ticker with name [{}]", name, e);
        } finally {
            tickersLock.readLock().unlock();
        }
        return new HashSet<>();
    }

    private Set<Ticker> performSearchOnTickers(Predicate<Ticker> predicate, int limit) {
        return allTickers.parallelStream()
                .filter(predicate)
                .limit(limit)
                .collect(Collectors.toSet());
    }

    /**
     * Returns found tickers using a default pageable result
     */
    public PageableData<Ticker> getActiveTickers() {
        return getActiveTickersForPageable(DEFAULT_PAGEABLE);
    }

    /**
     * Returns found tickers using the given page as the start
     */
    public PageableData<Ticker> getActiveTickers(int page) {
        return getActiveTickersForPageable(new PageRequest(page, DEFAULT_PAGE_ELEMENT_SIZE));
    }

    public Page<TickerEntity> getActiveTickers(Pageable pageable) {
        return tickersRepository.findAllByIsTradableTrue(pageable);
    }

    private PageableData<Ticker> getActiveTickersForPageable(Pageable pageable) {
        Page<TickerEntity> foundTickers = getActiveTickers(pageable);

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
                LOGGER.warn("Could not parse stock exchange of ticker entity - this should not happen. [{}]", foundTicker);
            }
        }
        return Optional.empty();
    }
}
