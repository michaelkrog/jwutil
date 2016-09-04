package dk.apaq.orderly.common.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class LanguageValidator implements ConstraintValidator<Language, String>{

    private static final List<String> LANGUAGES = new ArrayList<>();
    
    static {
        LANGUAGES.addAll(Arrays.asList(Locale.getISOLanguages()));
    }
    
    @Override
    public void initialize(Language constraintAnnotation) {
        
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return LANGUAGES.contains(value);
    }

}
