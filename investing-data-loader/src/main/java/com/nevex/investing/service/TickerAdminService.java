package com.nevex.investing.service;

import com.nevex.investing.database.TickersRepository;
import com.nevex.investing.database.entity.TickerEntity;
import com.nevex.investing.database.model.DataSaveException;
import com.nevex.investing.database.utils.RepositoryUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class TickerAdminService extends TickerService {

    public TickerAdminService(TickersRepository tickersRepository) {
        super(tickersRepository);
    }

    public int refreshAllTickers() {
        return super.refreshAllTickers();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TickerEntity saveTicker(TickerEntity tickerEntity) throws DataSaveException {
        return RepositoryUtils.createOrUpdate(tickersRepository,
                tickerEntity,
                () -> tickersRepository.findBySymbol(tickerEntity.getSymbol()));
    }


}
