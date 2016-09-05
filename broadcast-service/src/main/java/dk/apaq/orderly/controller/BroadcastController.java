package dk.apaq.orderly.controller;

import dk.apaq.orderly.model.Broadcast;
import java.util.Collection;
import java.util.Collections;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BroadcastController {
    
    @RequestMapping(value = "/broadcasts", method = RequestMethod.GET)
    public Collection<Broadcast> list() {
        return Collections.EMPTY_LIST;
    }
    
    @RequestMapping(value = "/broadcasts/{id}", method = RequestMethod.GET)
    public Broadcast get(String id) {
        return null;
    }
    
    
}
