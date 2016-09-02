package dk.apaq.jwutil.common.config;

import com.mongodb.DBObject;
import dk.apaq.jwutil.common.model.BaseEntity;
import dk.apaq.jwutil.common.util.IdGenerator;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

@Component
public class BeforeSaveListener extends AbstractMongoEventListener<BaseEntity> {

    @Override
    public void onBeforeSave(BaseEntity p, DBObject dbo) {
        if (p.isNew()) {
            ObjectId oid = new ObjectId();
            String id = IdGenerator.generate(p.getTypeAbbreviation(), oid.toHexString());
            dbo.put("_id", id);
        }
    }
}
