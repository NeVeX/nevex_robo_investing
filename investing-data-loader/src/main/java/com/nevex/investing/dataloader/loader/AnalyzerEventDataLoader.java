package com.nevex.investing.dataloader.loader;

import com.nevex.investing.config.property.AnalyzerProperties;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.event.EventManager;
import com.nevex.investing.event.type.Event;
import com.nevex.investing.event.type.StockFinancialsUpdatedEvent;
import com.nevex.investing.event.type.StockPriceUpdatedEvent;
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
            processed = super.processAllPagesIndividuallyForIterable(tickerService::getTickers, this::sendEvents, 0);
        }
        return new DataLoaderWorkerResult(processed);
    }

    private void sendEvents(TickerEntity ticker) {
        if ( analyzerProperties.getStockPriceChangeAnalyzer().getSendEventsOnStartup()) {
            sendEvents(ticker,
                    analyzerProperties.getStockPriceChangeAnalyzer().getSendEventsOnStartupStartingFromDate(),
                    (t, date) -> new StockPriceUpdatedEvent(t.getId(), date)
            );
        }
        if ( analyzerProperties.getStockFinancialsAnalyzer().getSendEventsOnStartup()) {
            sendEvents(ticker,
                    analyzerProperties.getStockFinancialsAnalyzer().getSendEventsOnStartupStartingFromDate(),
                    (t, date) -> new StockFinancialsUpdatedEvent(t.getId(), date)
            );
        }
    }

    private int sendEvents(TickerEntity ticker, LocalDate startDate, EventCreator eventCreator) {
        LocalDate currentEventDate = startDate;
        int processed = 0;
        while ( currentEventDate.compareTo(super.getWorkerStartDate()) < 1) {
            EventManager.sendEvent(eventCreator.createEvent(ticker, currentEventDate));
            currentEventDate = currentEventDate.plusDays(1); // move forward a day
            processed++;
        }
        return processed;
    }

    @FunctionalInterface
    private interface EventCreator {
        Event createEvent(TickerEntity ticker, LocalDate date);
    }
}
