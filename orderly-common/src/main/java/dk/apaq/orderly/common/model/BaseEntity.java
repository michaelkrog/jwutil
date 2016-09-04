package dk.apaq.orderly.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.format.annotation.DateTimeFormat;


public abstract class BaseEntity implements Persistable<String> {
    
    @Id
    protected String id;

    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String lastModifiedBy;
    
    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdDate;
    
    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastModifiedDate;

    @JsonIgnore
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonIgnore
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    @JsonIgnore
    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public String getId() {
        return id;
    }

    @JsonIgnore
    public boolean isNew() {
        return id == null;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @JsonIgnore
    public abstract String getTypeAbbreviation();
    
}
