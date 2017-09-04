package com.nevex.investing.dataloader.loader;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.edgar.EdgarCikLookupClient;
import com.nevex.investing.database.TickerToCikRepository;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.database.entity.TickerToCikEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Optional;

import static com.nevex.investing.dataloader.loader.DataLoaderOrder.TICKER_TO_CIK_LOADER;

/**
 * Created by Mark Cunningham on 8/31/2017.
 * <br> This is the starting point: https://www.sec.gov/cgi-bin/browse-edgar?CIK=lc&owner=exclude&action=getcompany
 * <br> Basicaly, to use Edgar, we need the CIK number, which can be gotten by parsing the html page (scraping)
 */
public class EdgarTickerToCIKLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(EdgarTickerToCIKLoader.class);
    private final TickersRepository tickersRepository;
    private final EdgarCikLookupClient edgarCikLookupClient;
    private final TickerToCikRepository tickerToCikRepository;
    private final boolean onlyLookupMissingCiks;

    public EdgarTickerToCIKLoader(DataLoaderService dataLoaderService, TickersRepository tickersRepository,
                                  EdgarCikLookupClient edgarCikLookupClient, TickerToCikRepository tickerToCikRepository, boolean onlyLookupMissingCiks) {
        super(dataLoaderService);
        if ( tickersRepository == null ) { throw new IllegalArgumentException("Provided tickersRepository is null"); }
        if ( edgarCikLookupClient == null ) { throw new IllegalArgumentException("Provided edgarCikLookupClient is null"); }
        if ( tickerToCikRepository == null ) { throw new IllegalArgumentException("Provided tickerToCikRepository is null"); }
        this.tickersRepository = tickersRepository;
        this.edgarCikLookupClient = edgarCikLookupClient;
        this.tickerToCikRepository = tickerToCikRepository;
        this.onlyLookupMissingCiks = onlyLookupMissingCiks;
    }

    @Override
    int getOrderNumber() {
        return TICKER_TO_CIK_LOADER;
    }

    @Override
    String getName() {
        return "ticker-to-cik-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        int tickersProcessed = super.processAllPagesForRepo(tickersRepository, this::processTicker, 750);
        return new DataLoaderWorkerResult(tickersProcessed);
    }

    private void processTicker(TickerEntity tickerEntity) {

        if ( onlyLookupMissingCiks ) {
            // check we don't have this cik already
            if ( tickerToCikRepository.findByTickerId(tickerEntity.getId()).isPresent()) {
                return; // skip this guy, we already have him....
            }
        }

        if ( !getCikForTickerSymbol(tickerEntity)) {
            // we didn't get a cik for the symbol, so let's try the company name
            LOGGER.info("Did not find cik for ticker [{}], but will try the company name instead now", tickerEntity.getSymbol());
            getCikForCompanyName(tickerEntity);
        }
    }

    private boolean getCikForCompanyName(TickerEntity tickerEntity) {
        String companyName = tickerEntity.getName();
        try {
            Optional<String> cikOptional = edgarCikLookupClient.getCikForCompanyName(companyName);
            if ( cikOptional.isPresent()) {
                saveCikForTicker(tickerEntity.getId(), tickerEntity.getSymbol(), cikOptional.get());
                return true;
            } else {
                saveExceptionToDatabase("CIK search returned nothing for company name ["+companyName+"]");
            }
        } catch (ApiException apiException) {
            saveExceptionToDatabase("An error occurred trying to get the CIK for company name ["+companyName+"]. Error: "+apiException.getMessage());
        }
        return false;
    }

    private boolean getCikForTickerSymbol(TickerEntity tickerEntity) {
        String ticker = tickerEntity.getSymbol();
        try {
            Optional<String> cikOptional = edgarCikLookupClient.getCikForTicker(ticker);
            if ( cikOptional.isPresent()) {
                saveCikForTicker(tickerEntity.getId(), tickerEntity.getSymbol(), cikOptional.get());
                return true;
            } else {
                saveExceptionToDatabase("CIK search returned nothing for ticker symbol ["+ticker+"]");
            }
        } catch (ApiException apiException) {
            saveExceptionToDatabase("An error occurred trying to get the CIK for ticker ["+ticker+"]. Error: "+apiException.getMessage());
        }
        return false;
    }


    private void saveCikForTicker(int tickerId, String tickerSymbol, String cik) {

        try {
            TickerToCikEntity entityToSave = new TickerToCikEntity();
            Optional<TickerToCikEntity> existingEntityOptional = tickerToCikRepository.findByTickerId(tickerId);
            if ( existingEntityOptional.isPresent()) {
                TickerToCikEntity existingEntity = existingEntityOptional.get();
                if ( StringUtils.equalsIgnoreCase(cik, existingEntity.getCik())) {
                    // no need to save
                    return;
                } else {
                    // they are different - so replace the existing one
                    LOGGER.info("Replacing existing cik [{}] for ticker [{}] with new cik [{}]", existingEntity.getCik(), tickerSymbol, cik);
                    entityToSave = existingEntity; // re-save the existing one with the id already set now
                }
            }

            entityToSave.setCik(cik);
            entityToSave.setTickerId(tickerId);
            entityToSave.setUpdatedTimestamp(OffsetDateTime.now());

            tickerToCikRepository.save(entityToSave);
            LOGGER.info("Saved cik [{}] for ticker [{}]", cik, tickerSymbol);
        } catch (Exception e) {
            saveExceptionToDatabase("Could not save cik ["+cik+"] for ticker ["+tickerSymbol+"]. Reason: "+e.getMessage());
            LOGGER.error("Could not save cik [{}] for ticker [{}]", cik, tickerSymbol, e);
        }
    }

}
