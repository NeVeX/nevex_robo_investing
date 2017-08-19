package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.TickerEntity;
import com.nevex.roboinvesting.service.exception.TickerNotFoundException;
import com.nevex.roboinvesting.service.model.PageableData;
import com.nevex.roboinvesting.service.model.StockExchange;
import com.nevex.roboinvesting.service.model.Ticker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
public class MockTickerService extends TickerService {

    private final List<Ticker> mockTickers = new ArrayList<>();

    public MockTickerService(TickersRepository tickersRepository) {
        super(tickersRepository);
        createMockData();
    }

    private void createMockData() {

        List<TickerEntity> tickerEntities = new ArrayList<>();
        for ( int i = 1; i < 100; i++) {
            TickerEntity tickerEntity = new TickerEntity();
            tickerEntity.setSymbol("MOCK"+i);
            tickerEntity.setName("Mock Ticker " + i);
            tickerEntity.setId(i);
            tickerEntity.setCreatedDate(OffsetDateTime.now().minusYears(1));
            tickerEntity.setIndustry("Mock Industry " + i);
            tickerEntity.setIsTradable(i % 2 == 0);
            tickerEntity.setSector("Mock Sector " + i);
            tickerEntity.setStockExchange((short) ((i % StockExchange.values().length) + 1));
            tickerEntity.setTradingEndDate(null);
            tickerEntities.add(tickerEntity);
        }

        for ( TickerEntity te : tickerEntities) {
            mockTickers.add(new Ticker(te, StockExchange.fromId(te.getStockExchange()).get()));
        }

        refreshAllTickers(tickerEntities);
    }

    @Override
    protected void refreshAllTickers() {

    }

    /**
     * Returns found tickers using a default pageable result
     */
    public PageableData<Ticker> getTickers() {
        return new PageableData<>(mockTickers, new PageImpl<>(mockTickers));
    }

    public PageableData<Ticker> getTickers(int page) {
        return getTickers();
    }

    public Optional<Ticker> getTickerInformation(String inputSymbol) {
        return Optional.ofNullable(mockTickers.get(0));
    }

}
