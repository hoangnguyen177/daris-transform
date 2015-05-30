package transform;

import arc.mf.plugin.ServiceExecutor;

public interface Repository<T extends Entity> {

    /**
     * Retrieves the given version of the entity from the repository.
     * 
     * @param uid
     *            the unique id of the entity.
     * @param version
     *            the version number of the entity.
     * @return
     * @throws Throwable
     */
    T get(long uid, int version) throws Throwable;

    /**
     * Retrieves the latest version of the entity from repository.
     * 
     * @param uid
     *            the unique id of the entity.
     * @return
     * @throws Throwable
     */
    T get(long uid) throws Throwable;

    /**
     * Save the local entity to the repository.
     * 
     * @param entity
     * @return the newly created/updated entity.
     * @throws Throwable
     */
    T save(T entity) throws Throwable;

    /**
     * Deletes the entity from the repository.
     * 
     * @param entity
     * @throws Throwable
     */
    void delete(T entity) throws Throwable;

    /**
     * Generates a unique identifier for the new entity.
     * 
     * @return
     * @throws Throwable
     */
    long uidNext() throws Throwable;

    /**
     * The Mediaflux service executor.
     * 
     * @return
     */
    ServiceExecutor executor();
}
