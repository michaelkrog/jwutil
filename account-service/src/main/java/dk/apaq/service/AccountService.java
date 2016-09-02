package dk.apaq.service;

import dk.apaq.jwutil.common.errors.InvalidArgumentException;
import dk.apaq.jwutil.common.errors.InvalidRequestException;
import dk.apaq.jwutil.common.model.Unit;
import dk.apaq.jwutil.common.service.BaseService;
import dk.apaq.model.Account;
import dk.apaq.model.Roles;
import dk.apaq.model.SecurityQuestionInformation;
import static dk.apaq.model.SecurityQuestionType.None;
import dk.apaq.repository.AccountRepository;
import dk.apaq.security.SecurityHelper;
import dk.apaq.security.TokenGenerator;
import dk.apaq.security.TokenProvider;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 *
 * @author michael
 */
@Service
public class AccountService extends BaseService<Account, AccountRepository> {
    
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);
    private static final String TOKEN_SALT = "4f#1F3t34GbhyK6";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})");
    private static final String DEFAULT_LANGUAGE_CODE = "da";
    private static final String ERROR_PASSWORD_INVALID = "Invalid password. A valid password contains at least one Upper case character and one number having a total length of 6 to 20 chars.";
    private static final String ERROR_ACCOUNT_NEVER_PERSISTED = "Account has never been persisted. Cannot change role on person.";
    private PasswordEncoder passwordEncoder;
    private MessageSource messageSource;
    private RecaptchaService recaptchaService;
    private TokenProvider tokenProvider;
    
    @Autowired
    public void setTokenProvider(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
    
    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    @Autowired 
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setRecaptchaService(RecaptchaService recaptchaService) {
        this.recaptchaService = recaptchaService;
    }

    @PreAuthorize(value = "hasPermission(#unitId, '" + Unit.ABBREVIATION + "', 'read')")
    public Page<Account> findAllByUnit(String unitId, Pageable pageable) {
        LOG.debug("Retreving all accounts by unit [unit={}]", unitId);
        
        return getRepository().findAllByRolesContaining(pageable, Roles.generateUnitAccessRole(unitId, Roles.ORGROLE_ADMIN),
                Roles.generateUnitAccessRole(unitId, Roles.ORGROLE_USER));
    }
    
    @PreAuthorize("hasPermission(#unitId, '" + Unit.ABBREVIATION + "', 'read')")
    public Iterable<Account> findAllByUnit(String unitId) {
        LOG.debug("Retreving all accounts by unit [unit={}]", unitId);
        
        return getRepository().findAllByRolesContaining(Roles.generateUnitAccessRole(unitId, Roles.ORGROLE_ADMIN),
                Roles.generateUnitAccessRole(unitId, Roles.ORGROLE_USER));
    }

    @PostFilter("hasPermission(filterObject, 'read')")
    public Iterable<Account> findAll(Iterable<String> ids) {
        return getRepository().findAll(ids);
    }
    
    @Override
    @PostAuthorize("hasPermission(returnObject, 'read')")
    public Account findOne(String id) {
        LOG.debug("Retrieving person by id [id={}]", id);
        if("current".equals(id)) {
            return SecurityHelper.getCurrentAccount();
        } else {
            return super.findOne(id); 
        }
    }
    
    public boolean isUsernameAvailable(String username) {
        return getRepository().findByLogin(username) == null;
    }
    
    public boolean isEmailAvailable(String email) {
        return getRepository().findByEmail(email) == null;
    }
    
    @Secured("ROLE_ADMIN")
    public Account findByUsername(String username) {
        LOG.debug("Retrieving person by username [id={}]", username);
        return getRepository().findByLogin(username);
    }

    public void completeEmailValidation(String id, String token) {
        
        Account person = findOne(id);
        if(person == null) {
            throw new IllegalArgumentException("The specified id [" + id + "] does not exist.");
        }
        
        assertHasEmail(person, "Account does not have an emailadress specified.");
        
        String serverToken = generateValidateMailToken(person);
        if(!serverToken.equals(token)) {
            throw new InvalidArgumentException("token", "The specified token does not match the token generated serverside.");
        }
        
        person.setEmailValidated(true);
        super.save(person);
    }

    /**
     * Regenerates password for user. The password will be generated ONLY if the answer matches the answer earlier recoreded by the user.
     * @param accountId The id of the account to regenerate password for.
     * @param answer The security answer.
     * @param newPassword The new password.
     */
    public void regeneratePassword(String accountId, String answer, String newPassword) {
        Account account = getRepository().findOne(accountId);
        if(account == null) {
            throw new IllegalArgumentException("account not found.");
        }
        
        if(!answer.equalsIgnoreCase(account.getSecurityQuestionAnswer())) {
               
            throw new IllegalArgumentException("The answer specified is not correct.");
        }
        
        account.setPassword(passwordEncoder.encode(newPassword));
        getRepository().save(account);
        
        // Send event 
        //mailService.sendPasswordChanged(account, newPassword);
    }

    /**
     * Resolves the security question type originally chosen by user.
     * @param login The login for the account.
     * @param email The email of the account.
     * @param recaptchaResponse The recaptcha response.
     * @param remoteIp The remote ip.
     * @return Information about the Security Question or NULL if no Security Question set.
     */
    public SecurityQuestionInformation getSecurityQuestionType(String login, String email, String recaptchaResponse, String remoteIp) {
        checkHumanOrAdmin(recaptchaResponse, remoteIp);
        SecurityQuestionInformation resetInformation = null;
        Account person = getRepository().findByLoginAndEmail(login, email);
        if(person != null) {
            switch(person.getSecurityQuestionType()) {
                case None:
                    // If user has no security question chosen, then send him a token for resetting password.
                    String shortAnswer = generateShortCode();
                    person.setSecurityQuestionAnswer(shortAnswer);
                    getRepository().save(person);
                    
                    // Send event
                    // mailService.sendPasswordResetToken(person, shortAnswer);
                default:
                    resetInformation = new SecurityQuestionInformation(person.getId(), person.getSecurityQuestionType());
            }
            
        }
        return resetInformation;
    }
    
    @Secured("ROLE_ADMIN")
    public void setRoles(Account person, List<String> roles) {
        if(person.isNew()) {
            throw new IllegalArgumentException(messageSource.getMessage("error.account.never_persisted", null, ERROR_ACCOUNT_NEVER_PERSISTED, 
                    LocaleContextHolder.getLocale()));
        }
        Account existing = findOne(person.getId());
        existing.setRoles(roles);
        super.save(existing);
    }
    
    protected void connectToUnit(String accountId, String unitId, String role) {
        Account account = findOne(accountId);
        if(account != null) {
            // Apply role
            account.getRoles().add(Roles.generateUnitAccessRole(unitId, role));
            super.save(account);
        }
    }
    
    protected void disconnectFromUnit(String accountId, String unitId) {
        Account account = findOne(accountId);
        if(account != null) {
            // Remove role
            account.getRoles().removeIf(role -> Roles.isUnitRole(role) && unitId.equals(Roles.getUnitId(role)));
            super.save(account);
        }
    }
    
    @PreAuthorize("hasPermission(#entity, 'write')")
    public Account create(Account entity, String recaptchaResponse, String remoteIp) {
        if(!entity.isNew()) {
            throw new InvalidRequestException("Cannot create entity that has already been persisted.");
        }
        
        checkHumanOrAdmin(recaptchaResponse, remoteIp);
        checkUsernameAndEmailAvailability(entity);
        
        return doSave(entity);
    }
    
    @PreAuthorize("hasPermission(#entity, 'write')")
    public Account save(Account entity) {
        if(entity.isNew()) {
            throw new InvalidRequestException("Cannot update entity that has never been persisted.");
        }
        return doSave(entity);
    }
    
    private Account doSave(Account entity) {
        LOG.debug("Saving person [person=[]]", entity);
        boolean newEntity = entity.isNew();
        
        if(entity.getLanguageCode() == null) {
            entity.setLanguageCode(DEFAULT_LANGUAGE_CODE);
        }
        
        if(!newEntity) {
            Account existing = findOne(entity.getId());
            if(existing != null) {
                // The username and password can only be set once - hence only if the user does not already exist with username and password specified.
                if(hasCredentials(existing)) {
                    entity.setLogin(existing.getLogin());
                    entity.setPassword(existing.getPassword());
                } else if(hasCredentials(entity)) {
                    // Username and password has been set for the first time. Validate them and enable user.
                    validatePassword(entity.getPassword());
                    entity.setPassword(passwordEncoder.encode(entity.getPassword()));
                }
                
                //Some properties cannot be changed using this method
                entity.setRoles(existing.getRoles());
                entity.setEmailValidated(existing.isEmailValidated());
                
            } else {
                throw new IllegalStateException("Entity has id specified but it does not exist. Cannot persist the entity.");
            }
        } else {
            if(hasCredentials(entity)) {
                validatePassword(entity.getPassword());
                entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            }
            entity.setEmailValidated(false);
            entity.setRole(Roles.ROLE_USER);
            
            
        }
        entity = super.save(entity); 
        
        if(newEntity) {
            
            if(entity.getEmail() != null) {
                
                initiateEmailValidation(entity);
            }
            
            // If creating user has access to an unit, this user should have access as well
            Account creatingUser = SecurityHelper.getCurrentAccount();
            if(creatingUser != null) {
                for(String role : creatingUser.getRoles()) {
                    if(Roles.isUnitRole(role)) {
                        String orgId = Roles.getUnitId(role);
                        connectToUnit(entity.getId(), orgId, Roles.ORGROLE_USER);
                    }
                }
            }
            
        }
        return entity;
    }
    
    public void initiateEmailValidation(Account entity) {
        // Send event
    }
    
    /**
     * Changes password for the current authenticated user.
     * @param currentPassword The current password.
     * @param newRawPassword The new password.
     */
    public void changePassword(String currentPassword, String newRawPassword) {
        Account person = SecurityHelper.getCurrentAccount();
        LOG.debug("Changing password for current person [person={}]", person);
        if(person == null) {
            throw new IllegalStateException("Current authentication does not refer to a person.");
        }
        
        if(currentPassword == null || !passwordEncoder.matches(currentPassword, person.getPassword())) {
            throw new InvalidArgumentException("currentPassword", 
                    messageSource.getMessage("error.password.no_match", null, LocaleContextHolder.getLocale()));
        }
        
        validatePassword(newRawPassword);
        
        person = findOne(person.getId());
        person.setPassword(passwordEncoder.encode(newRawPassword));
        super.save(person);
    }

    @PreAuthorize("hasPermission(#entity, 'delete')")
    public void delete(Account entity) {
        LOG.debug("Deleting person and all its relations. [person={}]", entity);
        super.delete(entity);
    }
    
    private void checkUsernameAndEmailAvailability(Account account) {
        LOG.debug("Checking username/email availability.");
        if(!isUsernameAvailable(account.getLogin())) {
            throw new InvalidArgumentException("login", "The login is already in use by another user.");
        }
        
        if(!isEmailAvailable(account.getEmail())) {
            throw new InvalidArgumentException("email", "The email is already in use by another user.");
        }
    }
    
    private boolean hasCredentials(Account person) {
        return person.getLogin() != null || person.getPassword() != null;
    }
    
    private void validatePassword(String password) {
        if(password== null) { 
            throw new InvalidArgumentException("password", "A password must be specified.");
        }
        
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        if(!matcher.matches()) { 
            throw new InvalidArgumentException("password", messageSource.getMessage("error.password.invalid", null, ERROR_PASSWORD_INVALID, 
                    LocaleContextHolder.getLocale()));
        }
        
    }
    
    private void assertNotNew(Account person, String message) {
        if(person.isNew()) {
            throw new IllegalArgumentException(message);
        }
    }
    
    private void assertHasEmail(Account person, String message) {
        if(person.getEmail() == null) {
            throw new IllegalStateException(message);
        }
    }
    
    private String generateValidateMailToken(Account account) {
        assertNotNew(account, "account has never been persisted.");
        assertHasEmail(account, "account has not email address specified.");
        return TokenGenerator.generateValidateMailToken(account, TOKEN_SALT);
    }
    
    private String generateShortCode() {
        Random r = new Random();
        Integer code = 100000 + r.nextInt(900000);
        return code.toString();
    }
    
    private void checkHumanOrAdmin(String recaptchaResponse, String remoteIp) {
        if(!SecurityHelper.isAdmin()) {
            if(recaptchaResponse == null) {
                throw new InvalidRequestException("No recaptcha response specified.");
            }
            
            if(!recaptchaService.verifyResponse(recaptchaResponse, remoteIp)) {
                throw new InvalidRequestException("Invalid recaptcha response specified.");
            }
        }
    }

}
