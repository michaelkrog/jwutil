package dk.apaq.orderly.common.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dk.apaq.orderly.common.filter.UnitIdHeaderFilter;
import dk.apaq.orderly.common.serializer.RestErrorSerializer;
import javax.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WebConfig.class);
    
    @Bean
    public Filter organizationHeaderFilter() {
        return new UnitIdHeaderFilter();
    }
    
        @Bean
    public Module jacksonModule() {
        LOG.info("Initializing Jackson Module.");
        SimpleModule module = new SimpleModule("Orderly", new Version(1, 0, 0, null, "Orderly", "Orderly"));

        module.addSerializer(new RestErrorSerializer());

        return module;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseRegisteredSuffixPatternMatch(false);
    }
    
}
