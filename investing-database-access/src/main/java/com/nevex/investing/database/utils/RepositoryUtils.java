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
            CrudRepository<T, ID> repo, T inputEntity, Supplier<Optional<T>> existenceCheck) throws DataSaveException {

        T entityToSave = inputEntity;

        // check if the entity exists
        Optional<T> existingEntityOpt = existenceCheck.get();
        if ( existingEntityOpt.isPresent()) {
            // we have an existing entity, so merge in entity we want to save (keeping the id etc)
            T existingEntity = existingEntityOpt.get();
            existingEntity.merge(entityToSave);
            entityToSave = existingEntity; // and now (re) save this
        }

        try {
            return repo.save(entityToSave);
        } catch (Exception ex) {
            throw new DataSaveException(entityToSave, ex);
        }
    }

}
