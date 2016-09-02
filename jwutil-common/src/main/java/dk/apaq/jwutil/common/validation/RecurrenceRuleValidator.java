package dk.apaq.jwutil.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import net.fortuna.ical4j.model.property.RRule;

public class RecurrenceRuleValidator implements ConstraintValidator<RecurrenceRule, String> {

    @Override
    public void initialize(RecurrenceRule constraintAnnotation) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || "".equals(value)) {
            return true;
        }
        
        try {
            RRule rrule = new RRule(value);
            return true;
        } catch (Exception ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ex.getMessage()).addConstraintViolation();
            return false;
        }
    }

}

