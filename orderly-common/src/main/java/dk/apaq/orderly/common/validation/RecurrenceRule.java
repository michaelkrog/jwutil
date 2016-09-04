package dk.apaq.orderly.common.validation;

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
@Constraint(validatedBy = RecurrenceRuleValidator.class)
@Documented
public @interface RecurrenceRule {

    String message() default "Not a valid recurrencerule. The recurrencerule must be a valid RFC 2445 recurrence rule.";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
