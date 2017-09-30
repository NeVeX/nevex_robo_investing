package com.nevex.investing.dataloader.loader;

import com.nevex.investing.database.StockExchangesRepository;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.StockExchangeEntity;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.service.TickerAdminService;
import com.nevex.investing.service.model.StockExchange;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
public class TickerSymbolLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerSymbolLoader.class);
    private final StockExchangesRepository stockExchangesRepository;
    private final TickersRepository tickersRepository;
    private final TickerAdminService tickerAdminService;
    private final Map<StockExchange, String> tickersToLoad = new HashMap<>();

    public TickerSymbolLoader(TickerAdminService tickerAdminService, StockExchangesRepository stockExchangesRepository,
                              TickersRepository tickersRepository, DataLoaderService dataLoaderService) {
        super(dataLoaderService);
        if ( tickerAdminService == null ) { throw new IllegalArgumentException("ticker admin service is null"); }
        if ( stockExchangesRepository == null ) { throw new IllegalArgumentException("stock exchange repository is null"); }
        if ( tickersRepository == null ) { throw new IllegalArgumentException("tickers repository is null"); }
        this.tickersRepository = tickersRepository;
        this.stockExchangesRepository = stockExchangesRepository;
        this.tickerAdminService = tickerAdminService;
    }

    public void addTickerFileToLoad(StockExchange stockExchange, String fileLocation) {
        if ( StringUtils.isNotBlank(fileLocation)) {
            tickersToLoad.put(stockExchange, fileLocation);
        } else {
            LOGGER.warn("Will not load tickers for exchange [{}] since file given is blank", stockExchange);
        }
    }

    @Override
    public String getName() {
        return "ticker-symbol-loader";
    }

    private void printAllExchangesAvailable() {
        StringBuilder sb = new StringBuilder("The list of exchanges available to the application are:\n");
        Iterable<StockExchangeEntity> stockExchanges = stockExchangesRepository.findAll();
        stockExchanges.forEach(e -> sb.append( "  - " + e.getId() + " : " + e.getName()).append("\n") );
        LOGGER.info(sb.toString());
    }

    @Override
    public int getOrderNumber() {
        return DataLoaderOrder.TICKER_SYMBOL_LOADER;
    }

    @Override
    @Transactional
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        LOGGER.info("{} will start to do it's work", this.getClass());
        printAllExchangesAvailable();
        int totalTickersAdded = 0;
        for (Map.Entry<StockExchange, String> entry : tickersToLoad.entrySet()) {
            totalTickersAdded += loadTickers(entry.getKey(), entry.getValue());
        }
        tickerAdminService.refreshAllTickers();
        LOGGER.info("{} has completed all it's work", this.getClass());
        return new DataLoaderWorkerResult(totalTickersAdded);
    }

    @Transactional
    private int loadTickers(StockExchange stockExchange, String fileLocation) throws DataLoaderWorkerException {
        LOGGER.info("Will attempt to load all tickers in file [{}] for exchange [{}]", fileLocation, stockExchange);
        if ( !doesStockExchangeExists(stockExchange)) {
            throw new DataLoaderWorkerException("Stock exchange ["+stockExchange+"] does not exist in the database");
        }
        File file = new File(fileLocation);
        if ( !file.exists()) {
            throw new DataLoaderWorkerException("Could not find the file ["+fileLocation+"] to load");
        }
        Set<CSVParsedTicker> parsedTickers = loadTickers(file);
        if ( !parsedTickers.isEmpty()) {
            saveTickers(stockExchange, parsedTickers);
        }
        return parsedTickers.size();
    }

    // TODO: Support updates to existing tickers!
    private Set<CSVParsedTicker> loadTickers(File tickersFile) throws DataLoaderWorkerException {
        try {
            Set<CSVParsedTicker> csvParsedTickers = new HashSet<>();
            FileReader fileReader = new FileReader(tickersFile);
            CSVParser parser = CSVFormat.RFC4180
                    .withFirstRecordAsHeader()
                    .withIgnoreEmptyLines()
                    .withIgnoreSurroundingSpaces()
                    .withTrim()
                    .parse(fileReader);
            for (CSVRecord csvRecord : parser ) {
                String symbol = csvRecord.get("Symbol");
                String name = csvRecord.get("Name");
                String sector = csvRecord.get("Sector");
                String industry = csvRecord.get("industry");
                CSVParsedTicker csvParsedTicker = new CSVParsedTicker(symbol, name, sector, industry);
                if ( csvParsedTicker.isValid() ) {
                    // and check if it's not already added
                    if ( csvParsedTickers.contains(csvParsedTicker)) {
                        LOGGER.warn("There are duplicate entries for the ticker symbol [{}] - ignoring this and subsequent duplicate tickers", csvParsedTicker.symbol);
                    } else {
                        csvParsedTickers.add(csvParsedTicker);
                    }
                } else {
                    LOGGER.warn("CSV Ticker row is invalid: [{}]. Raw Value [{}]", csvParsedTicker, csvRecord.toString());
                }
            }
            LOGGER.info("Successfully loaded a total of [{}] ticker records into the database of the file [{}]", csvParsedTickers.size(), tickersFile.getAbsolutePath());
            return csvParsedTickers;
        } catch (Exception e ) {
            throw new DataLoaderWorkerException("Could not load file ["+tickersFile+"]", e);
        }
    }

    private void saveTickers(StockExchange stockExchange, Set<CSVParsedTicker> csvParsedTickers) throws DataLoaderWorkerException {
        for ( CSVParsedTicker t : csvParsedTickers ) {
            saveTicker(stockExchange.getId(), t);
        }
    }

    private void saveTicker(short stockExchangeId, CSVParsedTicker CSVParsedTicker) throws DataLoaderWorkerException {
        TickerEntity entity = new TickerEntity();
        entity.setCreatedDate(OffsetDateTime.now());
        entity.setStockExchange(stockExchangeId);
        entity.setSymbol(CSVParsedTicker.symbol);
        entity.setSector(CSVParsedTicker.sector);
        entity.setName(CSVParsedTicker.name);
        entity.setIndustry(CSVParsedTicker.industry);
        entity.setIsTradable(true);
        boolean didSave;

        try {
            didSave = tickersRepository.save(entity) != null;
        } catch (Exception ex ) {
            saveExceptionToDatabase("Failed to save ticker entity ["+entity.getSymbol()+"]. Reason: ["+ex.getMessage()+"]");
            throw new DataLoaderWorkerException("Failed to save ticker entity ["+entity+"]", ex);
        }

        if ( !didSave) {
            saveExceptionToDatabase("The ticker entity ["+entity.getSymbol()+"] was not saved into the database");
            throw new DataLoaderWorkerException("The new ticker entity was not saved. ["+entity+"]");
        }
    }

    private boolean doesStockExchangeExists(StockExchange stockExchange) {
        return stockExchangesRepository.findOne(stockExchange.getId()) != null;
    }

    // Simple class to hold the values of the parsed csv for us
    private static class CSVParsedTicker {
        private final String symbol;
        private final String name;
        private final String sector;
        private final String industry;

        CSVParsedTicker(String symbol, String name, String sector, String industry) {
            this.symbol = StringUtils.trim(symbol).toUpperCase();
            this.name = StringUtils.trim(name);
            this.sector = StringUtils.trim(sector);
            this.industry = StringUtils.trim(industry);
        }

        boolean isValid() {
            return StringUtils.isNotBlank(symbol) &&  StringUtils.isNotBlank(name) && StringUtils.isNotBlank(sector);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CSVParsedTicker that = (CSVParsedTicker) o;
            return Objects.equals(symbol, that.symbol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(symbol);
        }

        @Override
        public String toString() {
            return "CSVParsedTicker{" +
                    "symbol='" + symbol + '\'' +
                    ", name='" + name + '\'' +
                    ", sector='" + sector + '\'' +
                    ", industry='" + industry + '\'' +
                    '}';
        }
    }


}
