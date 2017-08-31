package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.database.DataLoaderRunsRepository;
import com.nevex.roboinvesting.database.entity.DataLoaderRunEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import javax.annotation.PreDestroy;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class DataLoaderManager implements ApplicationListener<ApplicationReadyEvent> {

    private static final int EXIT_CODE_ON_EXCEPTION = 455;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoaderManager.class);
    private Set<DataLoaderWorker> workers = new TreeSet<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DataLoaderRunsRepository dataLoaderRunsRepository;

    public DataLoaderManager(DataLoaderRunsRepository dataLoaderRunsRepository) {
        if ( dataLoaderRunsRepository == null ) { throw new IllegalArgumentException("Provided dataLoaderRunsRepository is null"); }
        this.dataLoaderRunsRepository = dataLoaderRunsRepository;
    }

    public void addDataWorker(DataLoaderWorker dw) {
        this.workers.add(dw);
    }

    public void onApplicationEvent(ApplicationReadyEvent event) {
        Future future = executorService.submit(this::start);
        try {
            future.get();
        } catch (Exception e ) {
            LOGGER.error("Data loader failed - shutting application down", e);
            SpringApplication.exit(event.getApplicationContext(), (ExitCodeGenerator) () -> EXIT_CODE_ON_EXCEPTION);
        }
        LOGGER.info("Data loader manager has finished invoking all [{}] data workers", workers.size());
    }

    @PreDestroy
    void destroy() {
        executorService.shutdownNow();
    }

    private void start() {
        for ( DataLoaderWorker dw : workers ) {
            OffsetDateTime startTime = OffsetDateTime.now();
            long startTimeMs = System.currentTimeMillis();
            try {
                DataLoaderWorkerResult result = dw.doWork();
                // Save the work done
                saveRunToDatabase(dw.getName(), startTime, result.getRecordsProcessed());
            } catch (DataLoadWorkerException ex) {
                dw.saveExceptionToDatabase("Data loader has encountered a fatal exception. Reason: ["+ex.getMessage()+"]");
                if (dw.canHaveExceptions()) {
                    LOGGER.warn("DataLoaderWorker [{}] failed - will still allow other jobs to continue", dw.getName(), ex);
                } else {
                    // nope
                    throw new IllegalStateException("Data loader workers will be stopped since the data worked ["+dw.getName()+"] failed and has indicated it cannot continue", ex);
                }
            } finally {
                LOGGER.info("Data loader worker [{}] finished it's work in [{}] ms", dw.getName(), (System.currentTimeMillis() - startTimeMs));
            }
        }
    }

    private void saveRunToDatabase(String name, OffsetDateTime startTime, int recordsProcessed) {
        DataLoaderRunEntity entity = new DataLoaderRunEntity();
        entity.setStartTimestamp(startTime);
        entity.setEndTimestamp(OffsetDateTime.now());
        entity.setName(name);
        entity.setRecordsProcessed(recordsProcessed);
        try {
            dataLoaderRunsRepository.save(entity);
        } catch (Exception e) {
            LOGGER.error("Could not save data loader run entity [{}]", entity, e);
        }
    }
}
