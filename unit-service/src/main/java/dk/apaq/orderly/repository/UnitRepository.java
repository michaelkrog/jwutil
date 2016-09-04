package dk.apaq.orderly.repository;

import dk.apaq.orderly.common.model.Unit;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author michael
 */
public interface UnitRepository extends MongoRepository<Unit, String>  {

    Page<Unit> findByIdIn(Iterable<String> ids, Pageable pageable);
    
    Stream<Unit> findAllBy();
    
}
