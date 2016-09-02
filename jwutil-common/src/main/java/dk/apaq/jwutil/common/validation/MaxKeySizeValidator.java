package dk.apaq.jwutil.common.validation;

import java.util.Map;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class MaxKeySizeValidator implements ConstraintValidator<MaxKeySize, Map<String, String>> { 

    private int maxKeySize;
    
    @Override
    public void initialize(MaxKeySize constraintAnnotation) {
        maxKeySize = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        
        for(String key : value.keySet()) {
            if(key.length() > maxKeySize) {
                return false;
            }
        }
        
        return true;
    }

    

}
