package com.nevex.investing.service;

import com.nevex.investing.database.TickerToCikRepository;
import com.nevex.investing.database.entity.TickerToCikEntity;
import com.nevex.investing.util.CikUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Created by Mark Cunningham on 9/4/2017.
 */
public class EdgarAdminService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EdgarAdminService.class);
    private final TickerToCikRepository tickerToCikRepository;

    public EdgarAdminService(TickerToCikRepository tickerToCikRepository) {
        if ( tickerToCikRepository == null ) { throw new IllegalArgumentException("Provided tickerToCikRepository is null"); }
        this.tickerToCikRepository = tickerToCikRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public Optional<Long> getCikForTicker(int tickerId) {
        Optional<TickerToCikEntity> tickerToCikEntityOpt = tickerToCikRepository.findByTickerId(tickerId);
        if ( tickerToCikEntityOpt.isPresent()) {
            return CikUtils.parseCik(tickerToCikEntityOpt.get().getCik());
        }
        return Optional.empty();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCikForTicker(int tickerId, String tickerSymbol, String cik) throws ServiceException {

        try {
            TickerToCikEntity entityToSave = new TickerToCikEntity();
            Optional<TickerToCikEntity> existingEntityOptional = tickerToCikRepository.findByTickerId(tickerId);
            if ( existingEntityOptional.isPresent()) {
                TickerToCikEntity existingEntity = existingEntityOptional.get();
                if ( StringUtils.equalsIgnoreCase(cik, existingEntity.getCik())) {
                    // no need to save
                    return;
                } else {
                    // they are different - so replace the existing one
                    LOGGER.info("Replacing existing cik [{}] for ticker [{}] with new cik [{}]", existingEntity.getCik(), tickerSymbol, cik);
                    entityToSave = existingEntity; // re-save the existing one with the id already set now
                }
            }

            entityToSave.setCik(cik);
            entityToSave.setTickerId(tickerId);
            entityToSave.setUpdatedTimestamp(OffsetDateTime.now());

            tickerToCikRepository.save(entityToSave);
            LOGGER.info("Saved cik [{}] for ticker [{}]", cik, tickerSymbol);
        } catch (Exception e) {
            throw new ServiceException("Could not save cik ["+cik+"] for ticker ["+tickerSymbol+"]", e);
        }
    }

}
