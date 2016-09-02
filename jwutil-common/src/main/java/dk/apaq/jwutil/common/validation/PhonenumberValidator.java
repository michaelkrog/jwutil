package dk.apaq.jwutil.common.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhonenumberValidator implements ConstraintValidator<Phonenumber, String> {

    @Override
    public void initialize(Phonenumber constraintAnnotation) { }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || "".equals(value)) {
            return true;
        }
        
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            phoneUtil.parse(value, null);
            return true;
        } catch (NumberParseException e) {
            return false;
        }
    }

}
