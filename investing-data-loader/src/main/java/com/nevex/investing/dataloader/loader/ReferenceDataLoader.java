package com.nevex.investing.dataloader.loader;

import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.service.ServiceException;
import com.nevex.investing.service.StockExchangeAdminService;
import com.nevex.investing.service.model.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mark Cunningham on 8/10/2017.
 */
public class ReferenceDataLoader extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReferenceDataLoader.class);
    private final StockExchangeAdminService stockExchangeAdminService;

    public ReferenceDataLoader(StockExchangeAdminService stockExchangeAdminService, DataLoaderService dataLoaderService) {
        super(dataLoaderService);
        if ( stockExchangeAdminService == null) { throw new IllegalArgumentException("Provided stockExchangeAdminService is null"); }
        this.stockExchangeAdminService = stockExchangeAdminService;
    }

    @Override
    public String getName() {
        return "reference-data-loader";
    }

    @Override
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        int totalExchangesAdded = loadStockExchanges();
        return new DataLoaderWorkerResult(totalExchangesAdded);
    }

    private int loadStockExchanges() throws DataLoaderWorkerException {
        int stockExchangesAdded = 0;
        for (StockExchange se : StockExchange.values()) {

            boolean doesExchangeExist = stockExchangeAdminService.doesExchangeExist(se.getId());

            if ( doesExchangeExist ) {
                try {
                    stockExchangeAdminService.addExchange(se.getId(), se.name());
                } catch (ServiceException serviceEx ) {
                    // Don't continue...
                    throw new DataLoaderWorkerException("Could not addEvent exchange ["+se+"] to database", serviceEx);
                }
                stockExchangesAdded++;
            }
            // TODO: Add this check back in....
//            else {
//                if (entity.getId() != se.getId()) {
//                    throw new DataLoaderWorkerException("DB entity ["+entity+"] does not match code entity id ["+se+"]");
//                }
//            }
        }
        return stockExchangesAdded;
    }

    @Override
    public int getOrderNumber() {
        return DataLoaderOrder.REFERENCE_DATA_LOADER;
    }
}
