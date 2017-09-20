package com.nevex.investing.processor;

import com.nevex.investing.database.entity.YahooStockInfoEntity;
import com.nevex.investing.event.EventConsumer;
import com.nevex.investing.event.type.StockFinancialsUpdatedEvent;
import com.nevex.investing.processor.model.AnalyzerResult;
import com.nevex.investing.service.YahooStockInfoService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Mark Cunningham on 9/19/2017.
 */
public class StockFinancialsAnalyzer extends EventConsumer<StockFinancialsUpdatedEvent> {

    private final YahooStockInfoService yahooStockInfoService;

    public StockFinancialsAnalyzer(YahooStockInfoService yahooStockInfoService) {
        super(StockFinancialsUpdatedEvent.class);
        if ( yahooStockInfoService == null ) { throw new IllegalArgumentException("Provided yahooStockInfoService is null"); }
        this.yahooStockInfoService = yahooStockInfoService;
    }

    @Override
    public String getConsumerName() {
        return "stock-financials-analyzer";
    }

    @Override
    protected void onEvent(StockFinancialsUpdatedEvent event) {
        int tickerId = event.getTickerId();
        Optional<YahooStockInfoEntity> entityOpt = yahooStockInfoService.getLatestStockInfo(tickerId);
        if ( !entityOpt.isPresent()) {
            return; // nothing to do
        }

        Set<AnalyzerResult> analyzerResults = new HashSet<>();

        YahooStockInfoEntity entity = entityOpt.get();
        if ( entity.getPriceToEarningsRatio() != null ) {
            analyzerResults.add(new AnalyzerResult(entity.getTickerId(), "price-to-earnings", getResultForPE(entity.getPriceToEarningsRatio())));
        }

    }

    private double getResultForPE(BigDecimal priceToEarningsRatio) {

        if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(5)) <= 1) { return 0.8; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(7)) <= 1) { return 0.7; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(10)) <= 1) { return 0.5; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(15)) <= 1) { return 0.4; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(20)) <= 1) { return 0.3; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(30)) <= 1) { return 0.2; }
        else if ( priceToEarningsRatio.compareTo(BigDecimal.valueOf(50)) <= 1) { return 0.1; }
        return 0; // terrible...
    }


}
