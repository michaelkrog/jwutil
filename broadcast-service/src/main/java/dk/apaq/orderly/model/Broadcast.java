package dk.apaq.orderly.model;

import dk.apaq.orderly.common.model.BaseEntityWithMeta;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "broadcasts")
@TypeAlias(Broadcast.ABBREVIATION)
public class Broadcast extends BaseEntityWithMeta {
    
    public static final String ABBREVIATION = "bcst";
    
    @NotNull
    private String unitId;
    @NotNull
    private String title;
    @NotNull
    private String language;

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getTypeAbbreviation() {
        return ABBREVIATION;
    }
    
}
