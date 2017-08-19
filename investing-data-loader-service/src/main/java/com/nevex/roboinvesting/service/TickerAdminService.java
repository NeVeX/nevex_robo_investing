package com.nevex.roboinvesting.service;

import com.nevex.roboinvesting.api.ApiStockPrice;
import com.nevex.roboinvesting.database.StockPricesHistoricalRepository;
import com.nevex.roboinvesting.database.StockPricesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.StockPriceEntity;
import com.nevex.roboinvesting.database.entity.StockPriceHistoricalEntity;
import com.nevex.roboinvesting.service.exception.TickerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class TickerAdminService extends TickerService {

    public TickerAdminService(TickersRepository tickersRepository) {
        super(tickersRepository);
    }

    public void refreshAllTickers() {
        super.refreshAllTickers();
    }

}
