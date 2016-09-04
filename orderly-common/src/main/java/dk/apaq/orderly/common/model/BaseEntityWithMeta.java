package dk.apaq.orderly.common.model;

import dk.apaq.orderly.common.validation.MaxKeySize;
import dk.apaq.orderly.common.validation.MaxValueSize;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Size;


public abstract class BaseEntityWithMeta extends BaseEntity implements HasMeta {
    @Size(max = 20)
    @MaxKeySize(value = 40)
    @MaxValueSize(value = 500)
    protected Map<String, String> meta = new HashMap<>();

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }
   
}
