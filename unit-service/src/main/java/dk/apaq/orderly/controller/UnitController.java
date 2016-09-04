package dk.apaq.orderly.controller;

import dk.apaq.orderly.common.controller.BaseController;
import dk.apaq.orderly.common.controller.TreeNodeHolder;
import dk.apaq.orderly.common.filter.UnitIdHeaderFilter;
import dk.apaq.orderly.common.model.Unit;
import dk.apaq.orderly.service.UnitService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
public class UnitController extends BaseController<Unit, UnitService> {
    
    private static final Logger LOG = LoggerFactory.getLogger(UnitController.class);
    
    @Autowired
    private UnitService unitService;
    
    @RequestMapping(value = "/units", method = RequestMethod.GET)
    public ResponseEntity<List<Unit>> list(WebRequest request) {
        LOG.debug("List All Units request.");
        return handlePage(unitService.findAll(resolvePageRequest(request)));
    }
    
    @RequestMapping(value = "/units", method = RequestMethod.POST)
    public Unit create(@RequestBody Unit unit) {
        return doCreate(unit);
    }
    
    @RequestMapping(value = "/units", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public Unit createViaForm(@ModelAttribute Unit unit) {
        return create(unit);
    }
    
    @RequestMapping(value = "/units/{id}", method = RequestMethod.GET)
    public Unit get(@PathVariable String id) {
        if(id.equals("current")) {
            id = UnitIdHeaderFilter.getCurrentUnitId();
        }
        return doGet(id);
    }
    
    
    @RequestMapping(value = "/units/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    public Unit update(@RequestBody Unit unit, @PathVariable String id) {
        if(id.equals("current")) {
            id = UnitIdHeaderFilter.getCurrentUnitId();
        }
        return doUpdate(id, unit, treeNodePropertyReferenceConverter.translate(TreeNodeHolder.get()));
    }

    @RequestMapping(value = "/units/{id}", method = {RequestMethod.PUT, RequestMethod.POST}, 
            consumes = "application/x-www-form-urlencoded")
    public Unit updateViaForm(@PathVariable String id, @ModelAttribute Unit unit, HttpServletRequest request) {
        return doUpdate(id, unit, formPropertyReferenceConverter.translate(request.getParameterMap()));
    }
    
    
    @RequestMapping(value = "/units/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable String id) {
        doDelete(id);
    }
    
    
    
}