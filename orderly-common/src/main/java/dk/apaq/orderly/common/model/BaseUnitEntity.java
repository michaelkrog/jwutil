package dk.apaq.orderly.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.index.Indexed;


public abstract class BaseUnitEntity extends BaseEntityWithMeta {

    @NotNull
    @Indexed
    private String unitId;

    @JsonIgnore
    public String getUnitId() {
        return unitId;
    }

    @JsonIgnore
    public void setUnitId(String organizationId) {
        this.unitId = organizationId;
    }
    
}
