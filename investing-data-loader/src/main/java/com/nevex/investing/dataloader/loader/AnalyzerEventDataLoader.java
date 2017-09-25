package com.nevex.investing.dataloader.loader;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.edgar.EdgarCikLookupClient;
import com.nevex.investing.config.property.AnalyzerProperties;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.Event;
import com.nevex.investing.event.type.StockFinancialsUpdatedEvent;
import com.nevex.investing.event.type.StockPriceUpdatedEvent;
import com.nevex.investing.service.EdgarAdminService;
import com.nevex.investing.service.TickerService;
import com.nevex.investing.service.model.ServiceException;
import com.nevex.investing.service.model.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.nevex.investing.dataloader.loader.DataLoaderOrder.ANALYZER_EVENT_DATA_LOADER;
import static com.nevex.investing.dataloader.loader.DataLoaderOrder.TICKER_TO_CIK_LOADER;

/**
 * Created by Mark Cunningham on 9/25/2017.
 */
public class AnalyzerEventDataLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(AnalyzerEventDataLoader.class);
    private final TickerService tickerService;
    private final AnalyzerProperties analyzerProperties;

    public AnalyzerEventDataLoader(TickerService tickerService,
                                   DataLoaderService dataLoaderService,
                                   AnalyzerProperties analyzerProperties) {
        super(dataLoaderService);
        if ( tickerService == null ) { throw new IllegalArgumentException("Provided tickerService is null"); }
        if ( analyzerProperties == null ) { throw new IllegalArgumentException("Provided analyzerProperties is null"); }
        this.tickerService = tickerService;
        this.analyzerProperties = analyzerProperties;
    }

    @Override
    public int getOrderNumber() {
        return ANALYZER_EVENT_DATA_LOADER;
    }

    @Override
    public String getName() {
        return "analyzer-event-data-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        int processed = 0;

        if ( analyzerProperties.getStockPriceChangeAnalyzer().getEnabled()) {
            processed += sendEvents( i -> new StockPriceUpdatedEvent(i, super.getWorkerStartTime().toLocalDate()));
        }
        if ( analyzerProperties.getStockFinancialsAnalyzer().getEnabled()) {
            processed += sendEvents( i -> new StockFinancialsUpdatedEvent(i, super.getWorkerStartTime().toLocalDate()));
        }

        return new DataLoaderWorkerResult(processed);
    }

    private int sendEvents(Function<Integer, Event> eventCreator) {
        Set<Ticker> tickers = tickerService.getCachedTickers();
        tickers.stream().forEach( t -> EventManager.sendEvent(eventCreator.apply(t.getTickerId())));
        return tickers.size();
    }

}
