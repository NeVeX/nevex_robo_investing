package com.nevex.roboinvesting.dataloader;

import com.nevex.roboinvesting.database.entity.TickersEntity;
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

    abstract int orderNumber();

    abstract void doWork() throws DataLoadWorkerException;

    @Override
    public final int compareTo(DataLoaderWorker that) {
        return Integer.compare(orderNumber(), that.orderNumber());
    }

    /**
     * Helper function to page across a pageable repository
     */
    protected  <T, ID extends Serializable> void processAllPagesForRepo(PagingAndSortingRepository<T, ID> sortingRepository, Consumer<T> consumer) {

        // Fetch all the ticker symbols we have
        Pageable pageable = new PageRequest(0, 20);
        while ( pageable != null ) {
            // At some point the pageable will turn null

            Page<T> page = sortingRepository.findAll(pageable);
            if ( page != null && page.hasContent()) {
                page.forEach(consumer);
            }

            pageable = page != null && page.hasNext() ? page.nextPageable() : null;
        }
    }

}
