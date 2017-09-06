package com.nevex.investing.dataloader.loader;

import com.nevex.investing.dataloader.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Mark Cunningham on 9/6/2017.
 * <br>This class extends the base {@link DataLoaderWorker} and adds support for scheduling work
 */
abstract class DataLoaderSchedulingSingleWorker extends DataLoaderWorker {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataLoaderSchedulingSingleWorker.class);
    private final boolean forceStartScheduleOnStartup;
    private final AtomicBoolean isWorkerRunning = new AtomicBoolean(false);

    /**
     * Implementations will decorate this with the {@link org.springframework.scheduling.annotation.Scheduled} annotation.
     * Implementations should simple call the {{@link #start()}} method in the super class
     */
    abstract void onScheduleStartInvoked() throws DataLoaderWorkerException;

    abstract DataLoaderWorkerResult onWorkerStartedAtAppStartup() throws DataLoaderWorkerException;
    abstract DataLoaderWorkerResult doScheduledWork() throws DataLoaderWorkerException;

    DataLoaderSchedulingSingleWorker(DataLoaderService dataLoaderService, boolean forceStartScheduleOnStartup) {
        super(dataLoaderService);
        this.forceStartScheduleOnStartup = forceStartScheduleOnStartup;
    }

    @Override
    final DataLoaderWorkerResult doWork() throws DataLoaderWorkerException {
        DataLoaderWorkerResult result = onWorkerStartedAtAppStartup();
        if ( forceStartScheduleOnStartup ) {
            boolean isWorkerAlreadyRunning = isWorkerRunning.getAndSet(true); // get the current value and set to true for now
            if ( isWorkerAlreadyRunning ) {
                LOGGER.info("[{}] There is already worker running - will not invoke another", getName());
            }
            LOGGER.info("[{}] will start immediately since it is told to force start on startup", getName());
            try {
                return doScheduledWork();
            } finally {
                isWorkerRunning.set(false);
            }
        } else {
            return result;
        }
    }

    void scheduleStart() {
        LOGGER.info("[{}] has been started by it's schedule", getName());
        boolean isWorkerAlreadyRunning = isWorkerRunning.getAndSet(true); // get the current value and set to true for now
        if ( isWorkerAlreadyRunning ) {
            LOGGER.info("[{}] There is already worker running - will not invoke another", getName());
        } else {
            try {
                start(this::doScheduledWork);
            } finally {
                isWorkerRunning.set(false); // reset the worked
            }
        }
    }

}
