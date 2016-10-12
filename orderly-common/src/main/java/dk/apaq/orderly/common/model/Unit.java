package dk.apaq.orderly.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.malkusch.validation.constraints.Country;
import dk.apaq.orderly.common.validation.Language;
import dk.apaq.orderly.common.validation.Phonenumber;
import java.time.LocalDate;
import java.util.Locale;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author michael
 */
@Document(collection = "units")
public class Unit extends BaseEntity implements HasAddress {

    public static final String ABBREVIATION = "unit";
    
    @NotNull
    @Size(min = 2, max = 30)
    private String name;
    @Size(min = 0, max = 50)
    private String address;
    @Size(min = 0, max = 15)
    private String appartment;
    @Size(min = 0, max = 15)
    private String postalCode;
    @Size(min = 0, max = 40)
    private String city;
    @Country
    private String countryCode;
    @Phonenumber
    private String phone;
    @Size(min = 5, max = 50)
    private String email;
    private boolean terminated;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate terminationDate;
    @NotNull
    @Language
    private String languageCode;
    private boolean locked;
    @NotNull
    @Size(min = 2, max = 2)
    private double[] location;
    
    @NotNull
    private UnitType unitType = UnitType.Congregation;
    
    private String timeZone;
    
    
    public Unit() {
    }

    public Unit(String name) {
        this.name = name;
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
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty
    public boolean isTerminated() {
        return terminated;
    }

    @JsonIgnore
    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    @JsonProperty
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    @JsonIgnore
    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String locale) {
        this.languageCode = locale;
    }

    @JsonProperty
    public boolean isLocked() {
        return locked;
    }

    @JsonIgnore
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }
    
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
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
    
    @JsonIgnore
    public Locale getLocale() {
        if(languageCode != null && countryCode != null) {
            return new Locale(languageCode, countryCode);
        } else if(languageCode != null) {
            return Locale.forLanguageTag(languageCode);
        } else {
            return Locale.getDefault();
        }
    }
    
}
