package com.nevex.investing.service;

import com.nevex.investing.api.yahoo.model.YahooStockInfo;
import com.nevex.investing.database.YahooStockInfoRepository;
import com.nevex.investing.database.entity.YahooStockInfoEntity;
import com.nevex.investing.service.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/6/2017.
 */
public class YahooStockInfoService {

    private final Logger LOGGER = LoggerFactory.getLogger(YahooStockInfoService.class);
    private final YahooStockInfoRepository yahooStockInfoRepository;

    public YahooStockInfoService(YahooStockInfoRepository yahooStockInfoRepository) {
        if ( yahooStockInfoRepository == null) { throw new IllegalArgumentException("Provided yahooStockInfoRepository is null"); }
        this.yahooStockInfoRepository = yahooStockInfoRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveYahooStockInfo(int tickerId, YahooStockInfo yahooStockInfo) throws ServiceException {
        YahooStockInfoEntity newEntity = createEntity(tickerId, yahooStockInfo);

        Optional<YahooStockInfoEntity> existingEntityOpt = yahooStockInfoRepository.findByTickerIdAndDate(tickerId, newEntity.getDate());
        if ( existingEntityOpt.isPresent()) {
            LOGGER.info("Found an existing entity for yahoo stock info with ticker [{}] for date [{}] - will merge the two data sets and save", tickerId, newEntity.getDate());
            YahooStockInfoEntity existingEntity = existingEntityOpt.get();
            existingEntity.merge(newEntity);
            newEntity = existingEntity; // re-save this entity instead
        }
        try {
            yahooStockInfoRepository.save(newEntity);
        } catch (Exception e ) {
            throw new ServiceException("Could not save entity yahoo stock entity for ["+tickerId+"]", e);
        }
    }

    private YahooStockInfoEntity createEntity(int tickerId, YahooStockInfo yahooStockInfo) {
        return new YahooStockInfoEntity(
                tickerId,
                LocalDate.now(),
                yahooStockInfo.getCurrency().isPresent() ? yahooStockInfo.getCurrency().get() : null,
                yahooStockInfo.getStockExchange().isPresent() ? yahooStockInfo.getStockExchange().get() : null,
                yahooStockInfo.getAsk().isPresent() ? yahooStockInfo.getAsk().get() : null,
                yahooStockInfo.getAskSize().isPresent() ? yahooStockInfo.getAskSize().get() : null,
                yahooStockInfo.getBid().isPresent() ? yahooStockInfo.getBid().get() : null,
                yahooStockInfo.getBidSize().isPresent() ? yahooStockInfo.getBidSize().get() : null,
                yahooStockInfo.getMarketCap().isPresent() ? yahooStockInfo.getMarketCap().get() : null,
                yahooStockInfo.getSharesFloat().isPresent() ? yahooStockInfo.getSharesFloat().get() : null,
                yahooStockInfo.getSharesOutstanding().isPresent() ? yahooStockInfo.getSharesOutstanding().get() : null,
                yahooStockInfo.getSharesOwned().isPresent() ? yahooStockInfo.getSharesOwned().get() : null,
                yahooStockInfo.getEarningsPerShare().isPresent() ? yahooStockInfo.getEarningsPerShare().get() : null,
                yahooStockInfo.getPriceToEarningsRatio().isPresent() ? yahooStockInfo.getPriceToEarningsRatio().get() : null,
                yahooStockInfo.getPriceEarningsToGrowthRatio().isPresent() ? yahooStockInfo.getPriceEarningsToGrowthRatio().get() : null,
                yahooStockInfo.getEpsEstimateCurrentYear().isPresent() ? yahooStockInfo.getEpsEstimateCurrentYear().get() : null,
                yahooStockInfo.getEpsEstimateNextQuarter().isPresent() ? yahooStockInfo.getEpsEstimateNextQuarter().get() : null,
                yahooStockInfo.getEpsEstimateNextYear().isPresent() ? yahooStockInfo.getEpsEstimateNextYear().get() : null,
                yahooStockInfo.getPriceToBookRatio().isPresent() ? yahooStockInfo.getPriceToBookRatio().get() : null,
                yahooStockInfo.getPriceToSalesRatio().isPresent() ? yahooStockInfo.getPriceToSalesRatio().get() : null,
                yahooStockInfo.getBookValuePerShareRatio().isPresent() ? yahooStockInfo.getBookValuePerShareRatio().get() : null,
                yahooStockInfo.getRevenue().isPresent() ? yahooStockInfo.getRevenue().get() : null,
                yahooStockInfo.getEbitda().isPresent() ? yahooStockInfo.getEbitda().get() : null,
                yahooStockInfo.getOneYearTargetPrice().isPresent() ? yahooStockInfo.getOneYearTargetPrice().get() : null,
                yahooStockInfo.getReturnOnEquity().isPresent() ? yahooStockInfo.getReturnOnEquity().get() : null,
                yahooStockInfo.getShortRatio().isPresent() ? yahooStockInfo.getShortRatio().get() : null,
                yahooStockInfo.getAnnualDividendYield().isPresent() ? yahooStockInfo.getAnnualDividendYield().get() : null,
                yahooStockInfo.getAnnualDividendYieldPercent().isPresent() ? yahooStockInfo.getAnnualDividendYieldPercent().get() : null
        );
    }

}
