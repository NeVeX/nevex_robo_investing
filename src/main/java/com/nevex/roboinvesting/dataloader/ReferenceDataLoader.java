package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.database.StockExchangesRepository;
import com.nevex.roboinvesting.database.entity.StockExchangesEntity;
import com.nevex.roboinvesting.model.StockExchange;
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

    public ReferenceDataLoader(StockExchangesRepository stockExchangesRepository) {
        if ( stockExchangesRepository == null) { throw new IllegalArgumentException("Provided stockExchangesRepository is null"); }
        this.stockExchangesRepository = stockExchangesRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void doWork() throws DataLoadWorkerException {
        LOGGER.info("Loader started");
        loadStockExchanges();
        LOGGER.info("Loader finished");
    }

    private void loadStockExchanges() throws DataLoadWorkerException {
        for (StockExchange se : StockExchange.values()) {
            StockExchangesEntity entity = stockExchangesRepository.findOne(se.getId());
            if ( entity == null ) {
                // add it
                StockExchangesEntity newEntity = new StockExchangesEntity();
                newEntity.setId(se.getId());
                newEntity.setName(se.name());
                if ( stockExchangesRepository.save(newEntity) == null) {
                    throw new DataLoadWorkerException("Could not save new stock exchange ["+newEntity+"]");
                }
            } else {
                if (entity.getId() != se.getId()) {
                    throw new DataLoadWorkerException("DB entity ["+entity+"] does not match code entity id ["+se+"]");
                }
            }
        }
    }

    @Override
    boolean canHaveExceptions() {
        return false;
    }

    @Override
    int orderNumber() {
        return DataLoaderOrder.REFERENCE_DATA_LOADER;
    }
}
