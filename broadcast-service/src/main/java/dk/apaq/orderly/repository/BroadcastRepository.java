package dk.apaq.orderly.repository;

import dk.apaq.orderly.model.Broadcast;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BroadcastRepository extends MongoRepository<Broadcast, String>  {
 
}
