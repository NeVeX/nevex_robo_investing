package com.nevex.investing.service;

import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.service.model.PageableData;
import com.nevex.investing.service.model.StockExchange;
import com.nevex.investing.service.model.Ticker;
import org.springframework.data.domain.PageImpl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    protected int refreshAllTickers() {
        return 0;
    }

    /**
     * Returns found tickers using a default pageable result
     */
    public PageableData<Ticker> getActiveTickers() {
        return new PageableData<>(mockTickers, new PageImpl<>(mockTickers));
    }

    public PageableData<Ticker> getActiveTickers(int page) {
        return getActiveTickers();
    }

    public Optional<Ticker> getTickerInformation(String inputSymbol) {
        return Optional.ofNullable(mockTickers.get(0));
    }

}
