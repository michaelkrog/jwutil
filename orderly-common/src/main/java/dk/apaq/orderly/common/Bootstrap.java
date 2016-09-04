package dk.apaq.orderly.common;

import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableMongoAuditing
@ComponentScan(basePackages = {"dk.apaq"})
@EnableAutoConfiguration
public class Bootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

    


    // ***** MONGO BEAN VALIDATION ***** 
    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    // ***** ENTRY POINT *****
    public static void main(String[] args) {
        // We make sure that the server uses the same timezone no matter it is installed.
        // https://github.com/michaelkrog/Skveege-Server/issues/143
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        SpringApplication.run(Bootstrap.class, args);
    }

}
