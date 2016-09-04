package dk.apaq.orderly.common.validation;

import java.util.Map;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class MaxValueSizeValidator implements ConstraintValidator<MaxValueSize, Map<String, String>> { 

    private int maxValue;
    
    @Override
    public void initialize(MaxValueSize constraintAnnotation) {
        maxValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Map<String, String> map, ConstraintValidatorContext context) {
        if(map == null) {
            return true;
        }
        
        for(String current : map.values()) {
            if(current != null && current.length() > maxValue) {
                return false;
            }
        }
        
        return true;
    }

    

}
