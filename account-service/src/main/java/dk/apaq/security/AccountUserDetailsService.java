package dk.apaq.security;

import dk.apaq.model.Account;
import dk.apaq.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * UerDetailsService for Skveege Accounts.
 */
public class AccountUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountUserDetailsService.class);
    
    @Autowired
    private AccountRepository personRepository;
    
    public void setAccountRepository(AccountRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        username = username.toLowerCase();
        LOG.debug("Loading user by username [username={}]", username);
        Account person = personRepository.findByLogin(username);
        if(person == null) {
            throw new UsernameNotFoundException("No user found by name '" + username + "'");
        } else {
            return new AccountUserDetails(person);
        }
    }



}
