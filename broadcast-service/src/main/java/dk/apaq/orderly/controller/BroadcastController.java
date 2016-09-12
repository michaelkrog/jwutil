package dk.apaq.orderly.controller;

import dk.apaq.orderly.common.controller.BaseController;
import dk.apaq.orderly.common.controller.TreeNodeHolder;
import dk.apaq.orderly.model.Broadcast;
import dk.apaq.orderly.service.BroadcastService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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

//@RestController
public class BroadcastController extends BaseController<Broadcast, BroadcastService> {
     private static final Logger LOG = LoggerFactory.getLogger(BroadcastController.class);
    
    @Autowired
    private BroadcastService broadcastService;
    
    @RequestMapping(value = "/broadcasts", method = RequestMethod.GET)
    public ResponseEntity<List<Broadcast>> list(WebRequest request) {
        LOG.debug("List All Broadcasts request.");
        return handlePage(broadcastService.findAll(resolvePageRequest(request)));
    }
    
    @RequestMapping(value = "/broadcasts", method = RequestMethod.POST)
    public Broadcast create(@RequestBody Broadcast broadcast) {
        return doCreate(broadcast);
    }
    
    @RequestMapping(value = "/broadcasts", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public Broadcast createViaForm(@ModelAttribute Broadcast broadcast) {
        return create(broadcast);
    }
    
    @RequestMapping(value = "/broadcasts/{id}", method = RequestMethod.GET)
    public Broadcast get(@PathVariable String id) {
        return doGet(id);
    }
    
    
    @RequestMapping(value = "/broadcasts/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    public Broadcast update(@RequestBody Broadcast broadcast, @PathVariable String id) {
        return doUpdate(id, broadcast, treeNodePropertyReferenceConverter.translate(TreeNodeHolder.get()));
    }

    @RequestMapping(value = "/broadcasts/{id}", method = {RequestMethod.PUT, RequestMethod.POST}, 
            consumes = "application/x-www-form-urlencoded")
    public Broadcast updateViaForm(@PathVariable String id, @ModelAttribute Broadcast broadcast, HttpServletRequest request) {
        return doUpdate(id, broadcast, formPropertyReferenceConverter.translate(request.getParameterMap()));
    }
    
    
    @RequestMapping(value = "/broadcasts/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable String id) {
        doDelete(id);
    }
    
    
}
