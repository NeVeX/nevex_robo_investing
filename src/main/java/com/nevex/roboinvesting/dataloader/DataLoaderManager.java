package com.nevex.roboinvesting.dataloader;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mark Cunningham on 8/9/2017.
 */
public class DataLoaderManager implements ApplicationListener<ApplicationReadyEvent> {

    private Set<DataLoaderWorker> workers = new TreeSet<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void addDataWorker(DataLoaderWorker dw) {
        this.workers.add(dw);
    }

    @Scheduled()
    public void onApplicationEvent(ApplicationReadyEvent event) {
        executorService.submit(this::start);
    }

    @PreDestroy
    void destroy() {
        executorService.shutdownNow();
    }

    private void start() {
        for ( DataLoaderWorker dw : workers ) {
            dw.doWork();
        }
    }
}
