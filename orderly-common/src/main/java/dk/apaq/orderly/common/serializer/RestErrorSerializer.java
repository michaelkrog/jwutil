package dk.apaq.orderly.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dk.apaq.orderly.common.errors.RestError;
import dk.apaq.orderly.common.errors.RestErrorWithParam;
import java.io.IOException;


public class RestErrorSerializer  extends JsonSerializer<RestError> {

    public static final String FIELD_MEESAGE = "message";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_PARAM = "param";
    
    @Override
    public void serialize(RestError value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen = jgen.useDefaultPrettyPrinter();
        jgen.writeStartObject();
        jgen.writeObjectField(FIELD_TYPE, value.getType());
        jgen.writeStringField(FIELD_MEESAGE, value.getMessage());
        
        if(value instanceof RestErrorWithParam) {
            jgen.writeObjectField(FIELD_PARAM, ((RestErrorWithParam)value).getParam());
        }
        
        jgen.writeEndObject();
    }

    @Override
    public Class<RestError> handledType() {
        return RestError.class;
    }
    
}
