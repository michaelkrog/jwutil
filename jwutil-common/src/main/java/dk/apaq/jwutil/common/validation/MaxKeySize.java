package dk.apaq.jwutil.common.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = MaxKeySizeValidator.class)
@Documented
public @interface MaxKeySize {

    String message() default "Max key size is {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    int value();
}
