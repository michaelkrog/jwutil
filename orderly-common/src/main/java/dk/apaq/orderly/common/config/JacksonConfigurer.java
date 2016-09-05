package dk.apaq.orderly.common.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.apaq.orderly.common.controller.TreeNodeHolder;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Component
public class JacksonConfigurer {
    private RequestMappingHandlerAdapter annotationMethodHandlerAdapter;
 
    private class CustomObjectMapper extends ObjectMapper {

        @Override
        protected Object _readMapAndClose(JsonParser jp, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
            TreeNode node = jp.readValueAsTree();
            TreeNodeHolder.set(node);
            return super._readMapAndClose(node.traverse(), valueType);
        }
    
        
    }
    
    @Autowired
    private Module jacksonModule;
 
    @PostConstruct
    public void init() {
        List<HttpMessageConverter<?>> messageConverters = annotationMethodHandlerAdapter.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
                
                CustomObjectMapper objectMapper = new CustomObjectMapper();
                objectMapper.registerModule(jacksonModule);
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                m.setObjectMapper(objectMapper);
            }
        }
    }
 
    @Autowired
    public void setAnnotationMethodHandlerAdapter(RequestMappingHandlerAdapter annotationMethodHandlerAdapter) {
        this.annotationMethodHandlerAdapter  = annotationMethodHandlerAdapter;
    }

}
