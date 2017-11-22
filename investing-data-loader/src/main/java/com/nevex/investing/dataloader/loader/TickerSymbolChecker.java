package com.nevex.investing.dataloader.loader;

import com.nevex.iextrading.reference.stock.Symbol;
import com.nevex.investing.api.iextrading.IEXTradingClient;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.service.TickerAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
public class TickerSymbolChecker extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerSymbolChecker.class);
    private final TickerAdminService tickerAdminService;
    private final IEXTradingClient iexTradingClient;

    public TickerSymbolChecker(TickerAdminService tickerAdminService,
                               DataLoaderService dataLoaderService,
                               IEXTradingClient iexTradingClient) {

        super(dataLoaderService);
        if ( tickerAdminService == null ) { throw new IllegalArgumentException("tickerAdminService is null"); }
        if ( iexTradingClient == null ) { throw new IllegalArgumentException("iexTradingClient is null"); }
        this.tickerAdminService = tickerAdminService;
        this.iexTradingClient = iexTradingClient;
    }

    @Override
    public String getName() {
        return "ticker-symbol-checker";
    }

    @Override
    public int getOrderNumber() {
        return DataLoaderOrder.TICKER_SYMBOL_CHECKER;
    }

    @Override
    @Transactional
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        // get all the symbols
        Set<Symbol> symbolsFromApi;
        try {
            symbolsFromApi = iexTradingClient.getAllSymbols();
        } catch (Exception e) {
            throw new DataLoaderWorkerException("Could not get the stock symbols from the API to check", e);
        }
        Set<String> symbolsEnabled = symbolsFromApi.stream().filter(Symbol::isEnabled).map(s -> s.getSymbol().toUpperCase()).collect(Collectors.toSet());
        onNewCollectionOfEnabledSymbolsReceived(symbolsEnabled);
        return new DataLoaderWorkerResult(symbolsFromApi.size());
    }

    private void onNewCollectionOfEnabledSymbolsReceived(Set<String> symbolsEnabled) {
        // get all the symbols we have
        boolean moreTickersToProcess = true;
        int page = 0;
        PageRequest pageRequest;
        List<TickerEntity> tickerEntitiesToDisable = new ArrayList<>();
        while (moreTickersToProcess) {
            pageRequest = new PageRequest(page++, 500);
            Page<TickerEntity> dbTickersPage = tickerAdminService.getActiveTickers(pageRequest);
            if ( dbTickersPage.hasContent()) {

                // remove tickers that are simply not in the list of enabled symbols given
                tickerEntitiesToDisable.addAll(dbTickersPage.getContent()
                        .stream()
                        .filter(TickerEntity::getIsTradable) // ignore tickers we've already disabled
                        .filter(t -> !symbolsEnabled.contains(t.getSymbol().toUpperCase()))
                        .collect(Collectors.toList()));
            }
            moreTickersToProcess = dbTickersPage.hasNext();
        }
        
        disableTickers(tickerEntitiesToDisable);
        tickerAdminService.refreshAllTickers(); // not necessary to do this here - but leaving it here anyhow
    }

    private void disableTickers(List<TickerEntity> tickerEntitiesToDisable) {
        LOGGER.warn("Will disable a total of [{}] tickers", tickerEntitiesToDisable.size());
        for ( TickerEntity te : tickerEntitiesToDisable) {
            LOGGER.info("Disabling ticker [{} - {}] since the symbol was not found from the API", te.getSymbol(), te.getName());
            try {
                te.setIsTradable(false);
                te.setTradingEndDate(LocalDate.now());
                tickerAdminService.saveTicker(te);
            } catch (Exception exception) {
                LOGGER.warn("Could not disable ticker [{}]", te, exception);
            }
        }
    }

}
