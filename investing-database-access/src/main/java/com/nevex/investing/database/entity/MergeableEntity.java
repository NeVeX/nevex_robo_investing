package com.nevex.investing.database.entity;

/**
 * Created by Mark Cunningham on 9/20/2017.
 */
public interface MergeableEntity<T> {

    /**
     * Given another entity, merge the given entity into the instance, since the instance will be updated/saved.
     * @param other - the entity that has data to be merged with
     */
    void merge(T other);

}
