package dk.apaq.jwutil.common.controller;

import dk.apaq.jwutil.common.errors.InvalidArgumentException;
import dk.apaq.jwutil.common.errors.ResourceNotFoundException;
import dk.apaq.jwutil.common.filter.UnitIdHeaderFilter;
import dk.apaq.jwutil.common.model.BaseEntity;
import dk.apaq.jwutil.common.service.BaseService;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.context.request.WebRequest;

public class BaseController<T extends BaseEntity, S extends BaseService<T, ?>> {
    
    private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final List<String> DEFAULT_IGNORED_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[]{"createdDate"}));
    private S service;
    protected FormPropertyReferenceConverter formPropertyReferenceConverter = new FormPropertyReferenceConverter();
    protected TreeNodePropertyReferenceConverter treeNodePropertyReferenceConverter = new TreeNodePropertyReferenceConverter();
    
    
    @Autowired
    public void setService(S service) {
        this.service = service;
    }
    
    protected Filter resolveFiltering(WebRequest request) {
        String search = null;
        
        if(request.getParameter("search") != null) {
            search = request.getParameter("search");
        }
        
        return new Filter(search);
    }
    
    protected PageRequest resolvePageRequest(WebRequest request, String ... validOrderFields) {
        int page = 0, size = DEFAULT_PAGE_SIZE;
        Sort sort = null;
        
        if(request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch(NumberFormatException ex) { /* IGNORE */ }
        }
        
        if(request.getParameter("size") != null) {
            try {
                size = Math.abs(Math.min(100, Integer.parseInt(request.getParameter("size"))));
            } catch(NumberFormatException ex) { /* IGNORE */ }
        }
        
        if(request.getParameter("order") != null) {
            List<String> validationList = Arrays.asList(validOrderFields);
            List<Sort.Order> orders = new ArrayList<>();
            try {
                String[] fields = request.getParameter("order").split(",");
                for(String field : fields) {
                    Sort.Direction dir = Sort.Direction.ASC;
                    if(field.startsWith("^")) {
                        field = field.substring(1);
                        dir = Sort.Direction.DESC;
                    }
                    
                    if(validationList.contains(field)) {
                        orders.add(new Sort.Order(dir, field));
                    }
                }
                
                if(!orders.isEmpty()) {
                    sort = new Sort(orders);
                }
            } catch(NumberFormatException ex) { /* IGNORE */ }
        }
        
        return new PageRequest(page, size, sort);
    }
    
    protected ResponseEntity<List<T>> handleIterable(Iterable<T> list) {
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }
    
    protected ResponseEntity<List<T>> handlePage(Page<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        return new ResponseEntity(page.getContent(), headers, HttpStatus.OK);
    }
    
    protected T mergeEntities(T existingEntity, T newEntity, Iterable<String> dirtyFields, List<String> ignoredFields) {
        Assert.notNull(existingEntity, "existingEntity must be specified.");
        Assert.notNull(newEntity, "newEntity must be specified.");
        Assert.notNull(dirtyFields, "dirtyField must be specified.");
        Assert.notNull(ignoredFields, "ignoredField must be specified.");
         
        dirtyFields.iterator().forEachRemaining(item -> {
            if(!DEFAULT_IGNORED_FIELDS.contains(item) && !ignoredFields.contains(item)) {
                try {
                    PropertyUtils.setProperty(existingEntity, item, PropertyUtils.getProperty(newEntity, item));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IndexOutOfBoundsException ex) {
                    LOG.error("Error occured while merging entities.", ex);
                    throw new InvalidArgumentException(item, "The parameter does not apply to this resource.");
                }
            }
        });
        return existingEntity;
    }

    
    protected T checkResource(T resource) {
        return checkResource(resource, null);
    }
    
    protected T checkResource(T resource, String message) {
        if (resource == null) {
            throw new ResourceNotFoundException(message == null ? "Resource not found." : message);
        }
        return resource;
    }
    
    protected String checkOrganizationReference() {
        String currentOrganizationId = UnitIdHeaderFilter.getCurrentUnitId();
        if (currentOrganizationId == null) {
            throw new ResourceNotFoundException("No unit referenced.");
        }
        return currentOrganizationId;
    }
    
    public T doCreate(T entity) {
        LOG.debug("Create request [entity={}]", entity);
        entity.setId(null); // Make sure that the account is a new account
        return service.save(entity);
    }
    
    /*public T doCreateViaForm(T entity, Map<String, String[]> map) {
        applyModel(entity, map);
        return doCreate(entity);
    }*/
    
    public T doGet(String id) {
        LOG.debug("Retrieved GET request for specific instance [id={}]", id);
        return checkResource(service.findOne(id));
    }

    public T doUpdate(String id, T entity, Iterable<String> dirtyFields) {
        return doUpdate(id, entity, dirtyFields, (List<String>)Collections.EMPTY_LIST);
    }
    
    public T doUpdate(String id, T entity, Iterable<String> dirtyFields, List<String> ignoredFields) {
        LOG.debug("Update request [id={};entity={}]", id, entity);
        
        entity = mergeEntities(service.findOne(id), entity, dirtyFields, ignoredFields);
        
        entity.setId(id); // Make sure that the entity has the correct id
        return service.save(entity);
    }
    
    public void doDelete(String id) {
        LOG.debug("Delete request [id={}]", id);
        T entity = service.findOne(id);
        service.delete(entity);
    }
}
