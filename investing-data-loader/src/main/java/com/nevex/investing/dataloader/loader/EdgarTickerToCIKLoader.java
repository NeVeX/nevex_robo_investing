package com.nevex.investing.dataloader.loader;

import com.nevex.investing.api.ApiException;
import com.nevex.investing.api.edgar.EdgarCikLookupClient;
import com.nevex.investing.config.property.DataLoaderProperties;
import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.service.EdgarAdminService;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final EdgarAdminService edgarAdminService;
    private final boolean onlyLookupMissingCiks;
    private final long waitTimeBetweenTickersMs;

    public EdgarTickerToCIKLoader(DataLoaderService dataLoaderService, TickersRepository tickersRepository,
                                  EdgarCikLookupClient edgarCikLookupClient, EdgarAdminService edgarAdminService,
                                  DataLoaderProperties.EdgarTickerToCikLoaderProperties properties) {
        super(dataLoaderService);
        if ( tickersRepository == null ) { throw new IllegalArgumentException("Provided tickersRepository is null"); }
        if ( edgarCikLookupClient == null ) { throw new IllegalArgumentException("Provided edgarCikLookupClient is null"); }
        if ( edgarAdminService == null ) { throw new IllegalArgumentException("Provided edgarAdminService is null"); }
        if ( properties == null ) { throw new IllegalArgumentException("Provided properties is null"); }
        this.tickersRepository = tickersRepository;
        this.edgarCikLookupClient = edgarCikLookupClient;
        this.edgarAdminService = edgarAdminService;
        this.onlyLookupMissingCiks = properties.getOnlyLookupMissingTickerCiks();
        this.waitTimeBetweenTickersMs = properties.getWaitTimeBetweenTickersMs();
    }

    @Override
    public int getOrderNumber() {
        return TICKER_TO_CIK_LOADER;
    }

    @Override
    public String getName() {
        return "ticker-to-cik-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        int tickersProcessed = super.processAllPagesIndividuallyForRepo(tickersRepository, this::processTicker, waitTimeBetweenTickersMs);
        return new DataLoaderWorkerResult(tickersProcessed);
    }

    private void processTicker(TickerEntity tickerEntity) {

        if ( onlyLookupMissingCiks ) {
            // check we don't have this cik already
            if ( edgarAdminService.getCikForTicker(tickerEntity.getId()).isPresent()) {
                return; // skip this guy, we already have him....
            }
        }

        Optional<String> cikFound = getCikForTickerSymbol(tickerEntity);
        if ( !cikFound.isPresent()) {
            // we didn't get a cik for the symbol, so let's try the company name
            LOGGER.info("Did not find CIK for ticker [{}], but will try the company name instead now", tickerEntity.getSymbol());
            cikFound = getCikForCompanyName(tickerEntity);
        }

        if ( !cikFound.isPresent()) {
            // if it's still not found, then let's save it as en exception
            saveExceptionToDatabase("Could not find CIK for ticker ["+tickerEntity.getSymbol()+"], even after trying both ticker and company name lookups");
        } else {
            saveNewCik(tickerEntity, cikFound.get());
        }
    }

    private void saveNewCik(TickerEntity tickerEntity, String cik) {
        try {
            edgarAdminService.saveCikForTicker(tickerEntity.getId(), tickerEntity.getSymbol(), cik);
        } catch (ServiceException serviceEx ) {
            saveExceptionToDatabase("An error occurred while trying to save cik into the database for ticker ["+tickerEntity.getSymbol()+"]. Reason: "+serviceEx.getMessage());
        }
    }

    private Optional<String> getCikForTickerSymbol(TickerEntity tickerEntity) {
        String ticker = tickerEntity.getSymbol();
        try {
            return edgarCikLookupClient.getCikForTicker(ticker);
        } catch (ApiException apiException) {
            saveExceptionToDatabase("An error occurred trying to get the CIK for ticker ["+ticker+"]. Error: "+apiException.getMessage());
        }
        return Optional.empty();
    }

    private Optional<String> getCikForCompanyName(TickerEntity tickerEntity) {
        String companyName = tickerEntity.getName();
        try {
            return edgarCikLookupClient.getCikForCompanyName(companyName);
        } catch (ApiException apiException) {
            saveExceptionToDatabase("An error occurred trying to get the CIK for company name ["+companyName+"]. Error: "+apiException.getMessage());
        }
        return Optional.empty();
    }

}
