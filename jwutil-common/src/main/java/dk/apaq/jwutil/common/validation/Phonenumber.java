package dk.apaq.jwutil.common.validation;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PhonenumberValidator.class)
@Documented
public @interface Phonenumber {

    String message() default "Not a valid phone number. Number must be specified in international format and valid for the given area code.";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
