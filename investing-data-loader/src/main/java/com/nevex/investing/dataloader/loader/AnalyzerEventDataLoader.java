package com.nevex.investing.dataloader.loader;

import com.nevex.investing.config.property.AnalyzerProperties;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.*;
import com.nevex.investing.service.TickerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.nevex.investing.dataloader.loader.DataLoaderOrder.ANALYZER_EVENT_DATA_LOADER;

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
        List<String> eventNamesToTrigger = new ArrayList<>();

        if ( analyzerProperties.getStockPriceChangeAnalyzer().getSendEventsOnStartup()) {
            eventNamesToTrigger.add(StockPriceUpdatedEvent.class.getSimpleName());
        }
        if ( analyzerProperties.getStockFinancialsAnalyzer().getSendEventsOnStartup()) {
            eventNamesToTrigger.add(StockFinancialsUpdatedEvent.class.getSimpleName());
        }
        if ( analyzerProperties.getAnalyzerPreviousPricePerformanceAnalyzer().getSendEventsOnStartup()) {
            eventNamesToTrigger.add(AllAnalyzerSummaryUpdatedEvent.class.getSimpleName());
        }

        String message;
        if (eventNamesToTrigger.isEmpty()) {
            message = "No events will be triggered of this job";
        } else {
            StringBuilder sb = new StringBuilder("The following events will be triggered:");
            eventNamesToTrigger.stream().forEach( s -> sb.append("\n    ").append(s));
            message = sb.toString();
        }

        LOGGER.info("\n\n{} - {}\n\n", getName(), message);
        int processed = 0;
        if ( !eventNamesToTrigger.isEmpty()) {
            processed = super.processAllPagesIndividuallyForIterable(tickerService::getActiveTickers, this::sendEvents, 0, analyzerProperties.getThreadCountForStartup());
        }
        return new DataLoaderWorkerResult(processed);
    }

    private void sendEvents(TickerEntity ticker) {
        int processed = 0;
        processed += sendEvents(ticker, analyzerProperties.getStockPriceChangeAnalyzer(), (t, date) -> new StockPriceUpdatedEvent(t.getId(), date));
        processed += sendEvents(ticker, analyzerProperties.getStockFinancialsAnalyzer(), (t, date) -> new StockFinancialsUpdatedEvent(t.getId(), date));
        processed += sendEvents(ticker, analyzerProperties.getAnalyzerPreviousPricePerformanceAnalyzer(), (t, date) -> new AllAnalyzerSummaryUpdatedEvent(t.getId(), date));
        LOGGER.debug("{} - sent [{}] events for ticker [{}]", getName(), processed, ticker.getSymbol());
    }

    private int sendEvents(TickerEntity ticker, AnalyzerProperties.BaseAnalyzerProperties analyzerProperties, EventCreator eventCreator) {
        int processed = 0;
        if ( !analyzerProperties.getSendEventsOnStartup() ) {
            return processed;
        }
        LocalDate fromDate = analyzerProperties.getSendEventsOnStartupStartingFromDate();
        LocalDate toDate = analyzerProperties.getSendEventsOnStartupEndingOnLocalDate();

        while ( !fromDate.isAfter(toDate) ) {
            EventManager.sendEvent(eventCreator.createEvent(ticker, fromDate));
            fromDate = fromDate.plusDays(1); // move forward a day
            processed++;
        }

        return processed;
    }

    @FunctionalInterface
    private interface EventCreator {
        Event createEvent(TickerEntity ticker, LocalDate date);
    }
}
