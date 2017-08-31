package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.database.DataLoaderErrorsRepository;
import com.nevex.roboinvesting.database.entity.DataLoaderErrorEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public abstract class DataLoaderWorker implements Comparable<DataLoaderWorker> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataLoaderWorker.class);
    final DataLoaderErrorsRepository dataLoaderErrorsRepository;

    DataLoaderWorker(DataLoaderErrorsRepository dataLoaderErrorsRepository) {
        if ( dataLoaderErrorsRepository == null ) { throw new IllegalArgumentException("Provided dataLoaderErrorsRepository is null"); }
        this.dataLoaderErrorsRepository = dataLoaderErrorsRepository;
    }

    @Deprecated // Not necessary anymore
    abstract boolean canHaveExceptions();

    /**
     * The order in which this worker should execute (relative to other workers).
     * Lower number is a higher precedence. E.g. 1 will go first, then 2, then 3....
     */
    abstract int getOrderNumber();

    /**
     * The simple name of this worker
     */
    abstract String getName();

    abstract void doWork() throws DataLoadWorkerException;

    @Override
    public final int compareTo(DataLoaderWorker that) {
        return Integer.compare(getOrderNumber(), that.getOrderNumber());
    }

    /**
     * Helper function to page across a pageable repository
     */
    <T, ID extends Serializable> void processAllPagesForRepo(
            PagingAndSortingRepository<T, ID> sortingRepository, Consumer<T> consumer, long waitTimeBetweenTickersMs) {

        // Fetch all the ticker symbols we have
        Pageable pageable = new PageRequest(0, 20);
        while ( pageable != null ) {
            // At some point the pageable will turn null

            Page<T> page = sortingRepository.findAll(pageable);
            if ( page != null && page.hasContent()) {
                for ( T data : page) {
                    consumer.accept(data);
                    if ( waitTimeBetweenTickersMs > 0 ) {
                        boolean exceptionOccurred = tryPauseThreadForMs(waitTimeBetweenTickersMs, sortingRepository.getClass().getName(), pageable);
                        if ( exceptionOccurred ) {
                            return;
                        }
                    }
                }
            }

            pageable = page != null && page.hasNext() ? page.nextPageable() : null;
        }
    }

    // Tries to pause the thread for a certain amount of time
    // Returns TRUE if an exception happened
    private boolean tryPauseThreadForMs(long timeToPauseMs, String repoName, Pageable pageable) {
        // we need to pause the thread for a moment
        try {
//            LOGGER.info("Sleeping thread for [{}] for repository [{}] at page [{}]", timeToPauseMs, repoName, pageable);
            Thread.sleep(timeToPauseMs);
            return false;
        } catch (Exception e) {
            LOGGER.error("Will stop paging since a thread exception was received while sleeping for [{}] for repository [{}] at page [{}]",
                    timeToPauseMs, repoName, pageable, e);
            return true; // stop processing
        }
    }

    void saveExceptionToDatabase(String message) {
        DataLoaderErrorEntity errorEntity = new DataLoaderErrorEntity();
        errorEntity.setErrorMessage(message);
        errorEntity.setName(getName());
        errorEntity.setTimestamp(OffsetDateTime.now());
        try {
            dataLoaderErrorsRepository.save(errorEntity);
        } catch (Exception e ) {
            LOGGER.error("Could not save error entity [{}] into the database. Reason [{}]", errorEntity, e.getMessage());
        }
    }

}
