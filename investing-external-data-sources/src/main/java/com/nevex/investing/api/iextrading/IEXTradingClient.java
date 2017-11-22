package com.nevex.investing.api.iextrading;

import com.nevex.iextrading.reference.stock.Symbol;
import com.nevex.investing.api.ApiException;

import java.util.List;
import java.util.Set;

/**
 * Created by Mark Cunningham on 11/21/2017.
 */
public final class IEXTradingClient {

    private final com.nevex.iextrading.IEXTradingClient client = new com.nevex.iextrading.IEXTradingClient();

    public IEXTradingClient() {    }

    public Set<Symbol> getAllSymbols() throws ApiException {
        try {
            return client.referenceData().getAllSymbols();
        } catch (Exception e) {
            throw new ApiException("Could not get all symbols using IEXTrading", e);
        }
    }

}
