package com.nevex.investing.dataloader.loader;

import com.nevex.investing.dataloader.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
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
    public abstract int getOrderNumber();

    /**
     * The simple name of this loader
     */
    public abstract String getName();

    abstract DataLoaderWorkerResult doWork() throws DataLoaderWorkerException;

    /**
     * Entry point to start the loader
     */
    public void start() {
        start(this::doWork);
    }

    /**
     * Start the loader, by invoking the given supplier
     */
    void start(DataWorkerSupplier worker) {
        LOGGER.info("Started data loader worker [{}]", getName());
        OffsetDateTime startTime = OffsetDateTime.now();
        long startTimeMs = System.currentTimeMillis();
        try {
            DataLoaderWorkerResult result = worker.doWork();
            // Save the work done
            dataLoaderService.saveRun(getName(), startTime, result.getRecordsProcessed());
            LOGGER.info("[{}] finished it's work in [{}] ms", getName(), (System.currentTimeMillis() - startTimeMs));
        } catch (DataLoaderWorkerException ex) {
            dataLoaderService.saveError(getName(), ex.getMessage());
            throw new IllegalStateException("["+getName()+"] failed", ex);
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
    <T, ID extends Serializable> int processAllPagesIndividuallyForRepo(
            PagingAndSortingRepository<T, ID> sortingRepository, Consumer<T> consumer, long waitTimeBetweenTickersMs) {
        return processAllPages(sortingRepository::findAll,
                new ProcessPageIndividually<>(consumer, waitTimeBetweenTickersMs),
                20);
    }

    <T, ID extends Serializable> int processAllPagesIndividuallyForIterable (
            PageableIterable<T> iterable, Consumer<T> consumer, long waitTimeBetweenTickersMs) {
        return processAllPages(iterable,
                new ProcessPageIndividually<>(consumer, waitTimeBetweenTickersMs),
                20);
    }

    /**
     * Helper function to page across a pageable repository - but to process elements in bulk
     * @return the total records processed
     */
    <T, ID extends Serializable> int processAllPagesInBulkForRepo(
            PagingAndSortingRepository<T, ID> sortingRepository, Consumer<List<T>> consumer, long waitTimeBetweenBulkMs, int bulkAmount) {
        return processAllPages(sortingRepository::findAll, new ProcessPageInBulk<>(consumer, waitTimeBetweenBulkMs), bulkAmount);
    }

    /**
     * Helper function to page across a pageable repository
     * @return the total records processed
     */
    <T> int processAllPages(
            PageableIterable<T> iterable,
            ProcessPage<T> processPage,
            int pageCountMax) {
        int totalRecordsProcessed = 0;
        // Fetch all the ticker symbols we have
        Pageable pageable = new PageRequest(0, pageCountMax);
        while ( pageable != null ) {
            // At some point the pageable will turn null

            Page<T> page = iterable.iterate(pageable);
            if ( page != null && page.hasContent()) {
                totalRecordsProcessed += processPage.processPage(page);
            }

            pageable = page != null && page.hasNext() ? page.nextPageable() : null;
            long totalElements = page != null ? page.getTotalElements() : 0;
            long percentDone = totalElements == 0 ? 100 : (long) ((totalRecordsProcessed / (double) totalElements) * 100);
            LOGGER.info("[{}] job is {}% done. It has processed [{}] items of a total of [{}]", getName(), percentDone, totalRecordsProcessed, totalElements);
        }
        return totalRecordsProcessed;
    }

    // Tries to pause the thread for a certain amount of time
    // Returns TRUE if an exception happened
    private static boolean tryPauseThreadForMs(long timeToPauseMs) {
        // we need to pause the thread for a moment
        try {
            Thread.sleep(timeToPauseMs);
            return false;
        } catch (Exception e) {
            LOGGER.error("An exception occurred while sleeping between page processing across data. Wanted to sleep for [{}]", timeToPauseMs, e);
            return true;
        }
    }

    void saveExceptionToDatabase(String message) {
        dataLoaderService.saveError(getName(), message);
    }

    @FunctionalInterface
    interface PageableIterable<T> {
        Page<T> iterate(Pageable pageable);
    }

    @FunctionalInterface
    interface ProcessPage<T> {
        int processPage(Page<T> page);
    }

    private static class ProcessPageIndividually<T> implements ProcessPage<T> {

        private final long waitTimeBetweenElements;
        private final Consumer<T> consumer;

        ProcessPageIndividually(Consumer<T> consumer, long waitTimeBetweenElements) {
            this.consumer = consumer;
            this.waitTimeBetweenElements = waitTimeBetweenElements;
        }

        @Override
        public int processPage(Page<T> page) {
            int totalRecordsProcessed = 0;
            for ( T data : page) {
                consumer.accept(data);
                totalRecordsProcessed++;
                if ( waitTimeBetweenElements > 0 ) {
                    if ( ! tryPauseThreadForMs(waitTimeBetweenElements) ) {
                        LOGGER.warn("Could not sleep the thread for [{}] in between bulk page processing. Will continue still...", waitTimeBetweenElements);
                    }
                }
            }
            return totalRecordsProcessed;
        }
    }

    private static class ProcessPageInBulk<T> implements ProcessPage<T> {

        private final long waitTimeBetweenBulkMs;
        private final Consumer<List<T>> bulkConsumer;

        ProcessPageInBulk(Consumer<List<T>> bulkConsumer, long waitTimeBetweenBulkMs) {
            this.bulkConsumer = bulkConsumer;
            this.waitTimeBetweenBulkMs = waitTimeBetweenBulkMs;
        }

        @Override
        public int processPage(Page<T> page) {
            int totalRecordsProcessed = page.getNumberOfElements();
            bulkConsumer.accept(page.getContent());
            if ( ! tryPauseThreadForMs(waitTimeBetweenBulkMs)) {
                LOGGER.warn("Could not sleep the thread for [{}] in between bulk page processing. Will continue still...", waitTimeBetweenBulkMs);
            }
            return totalRecordsProcessed;
        }
    }

}
