package com.nevex.roboinvesting.dataloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public abstract class DataLoaderWorker implements Comparable<DataLoaderWorker> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataLoaderWorker.class);

    @Deprecated // Not necessary anymore
    abstract boolean canHaveExceptions();

    abstract int orderNumber();

    abstract void doWork() throws DataLoadWorkerException;

    @Override
    public final int compareTo(DataLoaderWorker that) {
        return Integer.compare(orderNumber(), that.orderNumber());
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

}
