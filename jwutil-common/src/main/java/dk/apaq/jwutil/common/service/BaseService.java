package dk.apaq.jwutil.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;

public abstract class BaseService<T extends Persistable<String>, R extends PagingAndSortingRepository<T, String>> {

    private final static Logger LOG = LoggerFactory.getLogger(BaseService.class);
    private R repository;
    
    @Autowired
    public void setRepository(R repository) {
        this.repository = repository;
    }
    
    public void afterPropertiesSet() {
        Assert.notNull(repository, "repository required");
    }

    public R getRepository() {
        return repository;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SYSTEM')")
    public Iterable<T> findAll() {
        LOG.debug("Listing all entities of repository {}.", repository.getClass());
        return repository.findAll();
    }
    
    @PostFilter("hasPermission(filterObject, 'read')")
    public Iterable<T> findAll(Iterable<String> ids) {
        LOG.debug("Listing all entities by ids {}.", repository.getClass());
        return repository.findAll(ids);
    }
    
    @PostFilter("hasPermission(filterObject, 'read')")
    public List<T> findAllInIdOrder(Iterable<String> ids) {
        LOG.debug("Listing all entities by ids and in same order as the given ids {}.", repository.getClass());
        
        // First load entities
        Iterable<T> entities = repository.findAll(ids);
        
        // Then map them
        Map<String, T> entityMap = new HashMap<>();
        entities.forEach(a -> entityMap.put(a.getId(), a));
        
        
        // Put them into a list in the correct order
        List<T> entityList = new ArrayList<>();
        ids.forEach(id -> entityList.add(entityMap.get(id)));
        
        // Return them
        return entityList;
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SYSTEM')")
    public Page<T> findAll(Pageable pageable) {
        LOG.debug("Listing all entities of repository {} [pageable={}].", repository.getClass(), pageable);
        return getRepository().findAll(pageable);
    }

    public T findOne(String id) {
        LOG.debug("Reading on entity of repository {}.", repository.getClass());
        return repository.findOne(id);
    }

    @Secured("ROLE_ADMIN")
    public void delete(String id) {
        LOG.debug("Deleting entity from repository {} where id is {}.", repository.getClass(), id);
        repository.delete(id);
    }

    @PreAuthorize("hasPermission(#entity, 'delete')")
    public void delete(T entity) {
        LOG.debug("Deleting entity from repository {} where entity is {}.", repository.getClass(), entity);
        repository.delete(entity);
    }

    @PreAuthorize("hasPermission(#entity, 'write')")
    public T save(T entity) {
        LOG.debug("Saving entity to repository {} where entity is {}.", repository.getClass(), entity);
        return repository.save(entity);
    }

    public Iterable<T> save(Iterable<T> entities) {
        LOG.debug("Saving entities to repository {}.", repository.getClass());
        return repository.save(entities);
    }

    boolean exists(String id) {
        LOG.debug("Checking if entity exists. [id={}]", id);
        return repository.exists(id);
    }

}
