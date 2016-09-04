package dk.apaq.orderly.security;

import dk.apaq.orderly.model.Account;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * UserDetails implementation for Skveege Account.
 */
public class AccountUserDetails implements UserDetails {

    private Account person;
    
    public AccountUserDetails(Account person) {
        this.person = person;
    }

    public Account getAccount() {
        return person;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<>();
        
        person.getRoles().iterator().forEachRemaining(role -> auths.add(new SimpleGrantedAuthority(role)));
        return auths;
    }

    @Override
    public String getPassword() {
        return person.getPassword();
    }

    @Override
    public String getUsername() {
        return person.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return person.isNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return person.isNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return person.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return person.isEnabled();
    }

}
