package com.nevex.investing.database.utils;

import com.nevex.investing.database.entity.MergeableEntity;
import com.nevex.investing.database.model.DataSaveException;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
public class RepositoryUtils {

    /**
     * Helper function to either create or update an existing entity.
     * <br>Note, you must be in the correct transactions before calling this method - no transactions are opened here
     */
    public static <T extends MergeableEntity<T>, ID extends Serializable> T createOrUpdate(
            CrudRepository<T, ID> repo, T entityToSave, Supplier<Optional<T>> existenceCheck) throws DataSaveException {

        // check if the entity exists
        Optional<T> existingEntityOpt = existenceCheck.get();
        if ( existingEntityOpt.isPresent()) {
            // we have an existing entity, so merge in the existing entity into the new entity we wish to save
            T existingEntity = existingEntityOpt.get();
            entityToSave.merge(existingEntity);
        }

        try {
            return repo.save(entityToSave);
        } catch (Exception ex) {
            throw new DataSaveException(entityToSave, ex);
        }
    }

}
