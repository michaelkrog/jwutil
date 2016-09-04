package dk.apaq.orderly.repository;

import dk.apaq.orderly.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AccountRepository extends MongoRepository<Account, String>  {
    
    @Query("{'$or': [{'login': ?0}, {'email': ?0}]}")
    Account findByLoginOrEmail(String loginOrEmail);
    Account findByLogin(String login);
    Account findByEmail(String email);
    Account findByLoginAndEmail(String login, String email);
    
    Iterable<Account> findAllByRolesContaining(String ... roles);
    Page<Account> findAllByRolesContaining(Pageable pageable, String ... roles);
    
    Page<Account> findAll(Pageable pageable);
}
