package dk.apaq.jwutil.common.validation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.constraints.Size;

@Size(min = 3, max = 3)
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
@Documented
public @interface Currency {

    String message() default "Not a valid currency code. Currency code must be a valid ISO 4217 code.";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
