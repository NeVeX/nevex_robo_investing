package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.entity.StockPriceEntity;
import com.nevex.roboinvesting.database.entity.StockPriceHistoricalEntity;
import com.nevex.roboinvesting.service.exception.TickerNotFoundException;
import com.nevex.roboinvesting.service.model.StockPrice;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class MockStockPriceService extends StockPriceService {

    List<StockPrice> mockPrices = new ArrayList<>();

    public MockStockPriceService(
            TickerService tickerService,
            StockPricesRepository stockPricesRepository,
            StockPricesHistoricalRepository stockPricesHistoricalRepository) {
        super(tickerService, stockPricesRepository, stockPricesHistoricalRepository);
        createMockData();
    }

    private void createMockData() {

        for ( int i = 1; i < 400; i++) {
            StockPriceEntity entity = new StockPriceEntity();
            entity.setTickerId(666);

            entity.setClose(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));
            entity.setHigh(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));
            entity.setLow(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));
            entity.setOpen(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));
            entity.setVolume(RandomUtils.nextLong(12345, 5656565L));

            entity.setAdjClose(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));
            entity.setAdjHigh(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));
            entity.setAdjLow(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));
            entity.setAdjOpen(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));
            entity.setAdjVolume(RandomUtils.nextLong(12345, 5656565L));

            entity.setDate(LocalDate.now().minusDays(i));
            entity.setDividendCash(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));
            entity.setSplitFactor(new BigDecimal(RandomUtils.nextInt(4, 20)+".00"));

            mockPrices.add(new StockPrice("MOCK", entity));
        }
    }

    @Override
    public Optional<StockPrice> getCurrentPrice(String symbol) throws TickerNotFoundException {
        return Optional.of(mockPrices.get(0));
    }

    // TODO: Limit the search
    public List<StockPrice> getHistoricalPrices(String symbol) throws TickerNotFoundException {
        return mockPrices;
    }

}
