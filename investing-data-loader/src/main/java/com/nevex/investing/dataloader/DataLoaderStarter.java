package com.nevex.investing.dataloader;

import com.nevex.investing.dataloader.loader.DataLoaderWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import javax.annotation.PreDestroy;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class DataLoaderStarter implements ApplicationListener<ApplicationReadyEvent> {

    private static final int EXIT_CODE_ON_EXCEPTION = 455;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoaderStarter.class);
    private Set<DataLoaderWorker> workers = new TreeSet<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
            dw.start();
        }
    }

}
