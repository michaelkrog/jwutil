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
@RequestMapping("/units")
public class UnitController extends BaseController<Unit, UnitService> {
    
    private static final Logger LOG = LoggerFactory.getLogger(UnitController.class);
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Override
    public Unit get(@PathVariable String id) {
        if(id.equals("current")) {
            id = UnitIdHeaderFilter.getCurrentUnitId();
        }
        return doGet(id);
    }
    
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    @Override
    public Unit update(@RequestBody Unit unit, @PathVariable String id) {
        if(id.equals("current")) {
            id = UnitIdHeaderFilter.getCurrentUnitId();
        }
        return doUpdate(id, unit, treeNodePropertyReferenceConverter.translate(TreeNodeHolder.get()));
    }
    
    
}