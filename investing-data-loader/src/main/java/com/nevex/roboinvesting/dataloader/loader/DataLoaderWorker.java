package com.nevex.roboinvesting.dataloader.loader;

import com.nevex.roboinvesting.dataloader.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public abstract class DataLoaderWorker implements Comparable<DataLoaderWorker> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataLoaderWorker.class);
    private final DataLoaderService dataLoaderService;

    DataLoaderWorker(DataLoaderService dataLoaderService) {
        if ( dataLoaderService == null ) { throw new IllegalArgumentException("Provided dataLoaderService is null"); }
        this.dataLoaderService = dataLoaderService;
    }

    /**
     * The order in which this loader should execute (relative to other workers).
     * Lower number is a higher precedence. E.g. 1 will go first, then 2, then 3....
     */
    abstract int getOrderNumber();

    /**
     * The simple name of this loader
     */
    abstract String getName();

    abstract DataLoaderWorkerResult doWork() throws DataLoaderWorkerException;

    /**
     * Entry point to start the loader
     */
    @Transactional
    public void start() {
        doStart(this::doWork);
    }

    /**
     * Start the loader, by invoking the given supplier
     */
    @Transactional
    void doStart(DataWorkerSupplier worker) {
        OffsetDateTime startTime = OffsetDateTime.now();
        long startTimeMs = System.currentTimeMillis();
        try {
            DataLoaderWorkerResult result = worker.doWork();
            // Save the work done
            dataLoaderService.saveRun(getName(), startTime, result.getRecordsProcessed());
            LOGGER.info("Data loader loader [{}] finished it's work in [{}] ms", getName(), (System.currentTimeMillis() - startTimeMs));
        } catch (DataLoaderWorkerException ex) {
            dataLoaderService.saveError(getName(), "Data loader has encountered a fatal exception. Reason: ["+ex.getMessage()+"]");
            throw new IllegalStateException("Data loader ["+getName()+"] failed", ex);
        }
    }

    @Override
    public final int compareTo(DataLoaderWorker that) {
        return Integer.compare(getOrderNumber(), that.getOrderNumber());
    }

    /**
     * Helper function to page across a pageable repository
     * @return the total records processed
     */
    <T, ID extends Serializable> int processAllPagesForRepo (
            PagingAndSortingRepository<T, ID> sortingRepository, Consumer<T> consumer, long waitTimeBetweenTickersMs) {
        int totalRecordsProcessed = 0;
        // Fetch all the ticker symbols we have
        Pageable pageable = new PageRequest(0, 20);
        while ( pageable != null ) {
            // At some point the pageable will turn null

            Page<T> page = sortingRepository.findAll(pageable);
            if ( page != null && page.hasContent()) {
                for ( T data : page) {
                    consumer.accept(data);
                    totalRecordsProcessed++;
                    if ( waitTimeBetweenTickersMs > 0 ) {
                        boolean exceptionOccurred = tryPauseThreadForMs(waitTimeBetweenTickersMs, sortingRepository.getClass().getName(), pageable);
                        if ( exceptionOccurred ) {
                            return totalRecordsProcessed;
                        }
                    }
                }
            }

            pageable = page != null && page.hasNext() ? page.nextPageable() : null;
        }
        return totalRecordsProcessed;
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
        dataLoaderService.saveError(getName(), message);
    }

}
