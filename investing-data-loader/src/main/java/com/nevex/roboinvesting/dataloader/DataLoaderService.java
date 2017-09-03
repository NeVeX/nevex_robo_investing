package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.database.DataLoaderErrorsRepository;
import com.nevex.roboinvesting.database.DataLoaderRunsRepository;
import com.nevex.roboinvesting.database.entity.DataLoaderErrorEntity;
import com.nevex.roboinvesting.database.entity.DataLoaderRunEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

/**
 * Created by Mark Cunningham on 9/2/2017.
 */
public class DataLoaderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataLoaderService.class);
    private final DataLoaderRunsRepository dataLoaderRunsRepository;
    private final DataLoaderErrorsRepository dataLoaderErrorsRepository;

    public DataLoaderService(DataLoaderRunsRepository dataLoaderRunsRepository, DataLoaderErrorsRepository dataLoaderErrorsRepository) {
        if ( dataLoaderRunsRepository == null ) { throw new IllegalArgumentException("Provided dataLoaderRunsRepository is null"); }
        if ( dataLoaderErrorsRepository == null ) { throw new IllegalArgumentException("Provided dataLoaderErrorsRepository is null"); }
        this.dataLoaderRunsRepository = dataLoaderRunsRepository;
        this.dataLoaderErrorsRepository = dataLoaderErrorsRepository;
    }

    public boolean saveError(String loaderName, String message) {
        DataLoaderErrorEntity errorEntity = new DataLoaderErrorEntity();
        errorEntity.setErrorMessage(message);
        errorEntity.setName(loaderName);
        errorEntity.setTimestamp(OffsetDateTime.now());
        try {
            return dataLoaderErrorsRepository.save(errorEntity) != null;
        } catch (Exception e ) {
            LOGGER.error("Could not save error entity [{}] into the database. Reason [{}]", errorEntity, e.getMessage());
            return false;
        }
    }

    public boolean saveRun(String name, OffsetDateTime startTime, int recordsProcessed) {
        DataLoaderRunEntity entity = new DataLoaderRunEntity();
        entity.setStartTimestamp(startTime);
        entity.setEndTimestamp(OffsetDateTime.now());
        entity.setName(name);
        entity.setRecordsProcessed(recordsProcessed);
        try {
            return dataLoaderRunsRepository.save(entity) != null;
        } catch (Exception e) {
            LOGGER.error("Could not save data loader run entity [{}]", entity, e);
            return false;
        }
    }



}
