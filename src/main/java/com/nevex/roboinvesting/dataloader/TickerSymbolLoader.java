package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.database.StockExchangesRepository;
import com.nevex.roboinvesting.database.TickersRepository;
import com.nevex.roboinvesting.database.entity.StockExchangesEntity;
import com.nevex.roboinvesting.database.entity.TickersEntity;
import com.nevex.roboinvesting.model.StockExchange;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
public class TickerSymbolLoader {

    private final static Logger LOGGER = LoggerFactory.getLogger(TickerSymbolLoader.class);
    private final StockExchangesRepository stockExchangesRepository;
    private final TickersRepository tickersRepository;

    public TickerSymbolLoader(StockExchangesRepository stockExchangesRepository, TickersRepository tickersRepository) {
        if ( stockExchangesRepository == null ) { throw new IllegalArgumentException("stock exchange repository is null"); }
        if ( tickersRepository == null ) { throw new IllegalArgumentException("tickers repository is null"); }
        this.tickersRepository = tickersRepository;
        this.stockExchangesRepository = stockExchangesRepository;

        printAllExchangesAvailable();
    }

    private void printAllExchangesAvailable() {
        StringBuilder sb = new StringBuilder("The list of exchanges available to the application are:\n");
        Iterable<StockExchangesEntity> stockExchanges = stockExchangesRepository.findAll();
        stockExchanges.forEach(e -> sb.append( "  - " + e.getId() + " : " + e.getName()).append("\n") );
        LOGGER.info(sb.toString());
    }

    @Transactional
    public DataLoadSummary loadTickers(StockExchange stockExchange, String fileLocation) throws DataLoadException {
        LOGGER.info("Will attempt to load all tickers in file [{}] for exchange [{}]", fileLocation, stockExchange);
        if ( !doesStockExchangeExists(stockExchange)) {
            throw new DataLoadException("Stock exchange ["+stockExchange+"] does not exist in the database");
        }
        File file = new File(fileLocation);
        if ( !file.exists()) {
            throw new DataLoadException("Could not find the file ["+fileLocation+"] to load");
        }
        return loadTickers(stockExchange.getId(), file);
    }

    // TODO: Support updates to existing tickers!
    private DataLoadSummary loadTickers(short stockExchangeId, File tickersFile) throws DataLoadException {
        try {
            int totalRecordsAdded = 0;
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
                ParsedTicker parsedTicker = new ParsedTicker(symbol, name, sector, industry);
                if ( parsedTicker.isValid() ) {
                    saveTicker(stockExchangeId, parsedTicker);
                    totalRecordsAdded++;
                } else {
                    LOGGER.warn("CSV Ticker row is invalid: [{}]. Raw Value [{}]", parsedTicker, csvRecord.toString());
                }
            }
            LOGGER.info("Successfully loaded a total of [{}] ticker records into the database from the file [{}]", totalRecordsAdded, tickersFile.getAbsolutePath());
            return new DataLoadSummary(totalRecordsAdded);
        } catch (Exception e ) {
            throw new DataLoadException("Could not load file ["+tickersFile+"]", e);
        }
    }

    private void saveTicker(short stockExchangeId, ParsedTicker parsedTicker) throws DataLoadException {
        TickersEntity entity = new TickersEntity();
        entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        entity.setStockExchange(stockExchangeId);
        entity.setSymbol(parsedTicker.symbol);
        entity.setSector(parsedTicker.sector);
        entity.setName(parsedTicker.name);
        entity.setIndustry(parsedTicker.industry);

        boolean didSave;
        try {
            didSave = tickersRepository.save(entity) != null;
        } catch (Exception ex ) {
            throw new DataLoadException("Failed to save ticker entity ["+entity+"]", ex);
        }

        if ( !didSave) {
            throw new DataLoadException("The new ticker entity was not saved. ["+entity+"]");
        }
    }

    private boolean doesStockExchangeExists(StockExchange stockExchange) {
        return stockExchangesRepository.findOne(stockExchange.getId()) != null;
    }

    // Simple class to hold the values of the parsed csv for us
    private static class ParsedTicker {
        private final String symbol;
        private final String name;
        private final String sector;
        private final String industry;

        ParsedTicker(String symbol, String name, String sector, String industry) {
            this.symbol = symbol;
            this.name = name;
            this.sector = sector;
            this.industry = industry;
        }

        boolean isValid() {
            return StringUtils.isNotBlank(symbol) &&  StringUtils.isNotBlank(name) && StringUtils.isNotBlank(sector);
        }

        @Override
        public String toString() {
            return "ParsedTicker{" +
                    "symbol='" + symbol + '\'' +
                    ", name='" + name + '\'' +
                    ", sector='" + sector + '\'' +
                    ", industry='" + industry + '\'' +
                    '}';
        }
    }


}
