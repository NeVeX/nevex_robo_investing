package com.nevex.investing.service;

import com.nevex.investing.database.TickersRepository;

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

}
