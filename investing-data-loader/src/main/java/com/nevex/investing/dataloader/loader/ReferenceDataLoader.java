package com.nevex.investing.dataloader.loader;

import com.nevex.investing.database.StockExchangesRepository;
import com.nevex.investing.database.entity.StockExchangeEntity;
import com.nevex.investing.dataloader.DataLoaderService;
import com.nevex.investing.service.model.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Mark Cunningham on 8/10/2017.
 */
public class ReferenceDataLoader extends DataLoaderWorker {

    // TODO: make this loader accept all reference data points
    private final static Logger LOGGER = LoggerFactory.getLogger(ReferenceDataLoader.class);
    private final StockExchangesRepository stockExchangesRepository;

    public ReferenceDataLoader(StockExchangesRepository stockExchangesRepository, DataLoaderService dataLoaderService) {
        super(dataLoaderService);
        if ( stockExchangesRepository == null) { throw new IllegalArgumentException("Provided stockExchangesRepository is null"); }
        this.stockExchangesRepository = stockExchangesRepository;
    }

    @Override
    String getName() {
        return "reference-data-loader";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        int totalExchangesAdded = loadStockExchanges();
        return new DataLoaderWorkerResult(totalExchangesAdded);
    }

    private int loadStockExchanges() throws DataLoaderWorkerException {
        int stockExchangesAdded = 0;
        for (StockExchange se : StockExchange.values()) {
            StockExchangeEntity entity = stockExchangesRepository.findOne(se.getId());
            if ( entity == null ) {
                // add it
                StockExchangeEntity newEntity = new StockExchangeEntity();
                newEntity.setId(se.getId());
                newEntity.setName(se.name());
                if ( stockExchangesRepository.save(newEntity) == null) {
                    throw new DataLoaderWorkerException("Could not save new stock exchange ["+newEntity+"]");
                }
                stockExchangesAdded++;
            } else {
                if (entity.getId() != se.getId()) {
                    throw new DataLoaderWorkerException("DB entity ["+entity+"] does not match code entity id ["+se+"]");
                }
            }
        }
        return stockExchangesAdded;
    }

    @Override
    int getOrderNumber() {
        return DataLoaderOrder.REFERENCE_DATA_LOADER;
    }
}
