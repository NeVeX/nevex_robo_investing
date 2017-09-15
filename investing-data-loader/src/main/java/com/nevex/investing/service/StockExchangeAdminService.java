package com.nevex.investing.service;

import com.nevex.investing.database.StockExchangesRepository;
import com.nevex.investing.database.entity.StockExchangeEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class StockExchangeAdminService {

    private final StockExchangesRepository stockExchangesRepository;

    public StockExchangeAdminService(StockExchangesRepository stockExchangesRepository) {
        if ( stockExchangesRepository == null ) { throw new IllegalArgumentException("Provided stockExchangesRepository is null"); }
        this.stockExchangesRepository = stockExchangesRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public boolean doesExchangeExist(short exchangeId) {
        return stockExchangesRepository.findOne(exchangeId) != null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addExchange(short exchangeId, String name) throws ServiceException {
        StockExchangeEntity newExchange = new StockExchangeEntity();
        newExchange.setId(exchangeId);
        newExchange.setName(name);

        StockExchangeEntity savedExchange;
        try {
            savedExchange = stockExchangesRepository.save(newExchange);
        } catch (Exception e) {
            throw new ServiceException("Could not save new exhange ["+newExchange+"]", e);
        }

        if ( savedExchange == null ) {
            throw new ServiceException("New exchange did not get saved ["+newExchange+"]");
        }
    }

}
