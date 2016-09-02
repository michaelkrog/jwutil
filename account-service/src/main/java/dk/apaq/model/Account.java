package dk.apaq.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.malkusch.validation.constraints.Country;
import dk.apaq.jwutil.common.model.BaseEntityWithMeta;
import dk.apaq.jwutil.common.model.HasAddress;
import dk.apaq.jwutil.common.validation.Language;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model for System Accounts.
 */
@Document(collection = "accounts")
@TypeAlias(Account.ABBREVIATION)
public class Account extends BaseEntityWithMeta implements HasAddress, Serializable {

    public static final String ABBREVIATION = "acct";
        
    @Indexed(unique = true)
    @Size(min = 5, max = 30)
    @NotNull
    @Pattern(regexp = "[0-9a-zA-Z_\\.]+")
    private String login;
    
    private String password;

    @NotNull
    private String name;
    
    @Size(max = 50)
    private String address;
    @Size(min = 0, max = 15)
    private String appartment;
    
    @Size(max = 15)
    private String postalCode;
    @Size(max = 40)
    private String city;
    
    @Country
    private String countryCode;
    

    @Size(min = 5, max = 50)
    @Indexed(unique = true)
    @NotNull
    @Email
    private String email;
    private boolean emailValidated = false;
    @NotNull
    @Language
    private String languageCode = "da";

    private List<String> roles = Arrays.asList(new String[]{Roles.ROLE_USER});

    private SecurityQuestionType securityQuestionType = SecurityQuestionType.None;
    private String securityQuestionAnswer;

    private boolean nonLocked = true;
    private boolean enabled = true;
    
    @NotNull
    @Size(min = 2, max = 2)
    private double[] location = {0,0};
    
    public Account() {
    }

    public Account(String name) {
        this.name = name;
    }
    
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String emailAddress) {
        this.email = emailAddress == null ? null : emailAddress.toLowerCase();
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login == null ? null : login.toLowerCase();
    }

    @JsonIgnore 
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAppartment() {
        return appartment;
    }

    public void setAppartment(String appartment) {
        this.appartment = appartment;
    }
    
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    @JsonProperty
    public List<String> getRoles() {
        return roles;
    }

    @JsonIgnore 
    public void setRole(String role) {
        setRoles(Arrays.asList(new String[]{role}));
    }
    
    @JsonIgnore 
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @JsonIgnore 
    public boolean isEnabled() {
        return enabled;
    }

    @JsonIgnore 
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @JsonProperty
    public boolean isEmailValidated() {
        return emailValidated;
    }

    @JsonIgnore 
    public void setEmailValidated(boolean value) {
        this.emailValidated = value;
    }

    @JsonIgnore 
    public boolean isNonExpired() {
        return true;
    }

    public void setNonLocked(boolean nonLocked) {
        this.nonLocked = nonLocked;
    }
    
    @JsonIgnore 
    public boolean isNonLocked() {
        return nonLocked;
    }

    @JsonIgnore 
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public SecurityQuestionType getSecurityQuestionType() {
        return securityQuestionType;
    }

    public void setSecurityQuestionType(SecurityQuestionType securityQuestionType) {
        this.securityQuestionType = securityQuestionType != null ? securityQuestionType : SecurityQuestionType.FirstPet;
    }

    @JsonIgnore 
    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    @JsonProperty 
    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[]{"meta"});
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public String getTypeAbbreviation() {
        return ABBREVIATION;
    }


    
}