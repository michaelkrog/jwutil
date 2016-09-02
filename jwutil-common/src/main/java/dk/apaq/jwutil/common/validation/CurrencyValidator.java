package dk.apaq.jwutil.common.validation;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class CurrencyValidator implements ConstraintValidator<Currency, String>{

    private static final List<String> CURRENCIES = new ArrayList<>();
    
    static {
        for(java.util.Currency c : java.util.Currency.getAvailableCurrencies()) {
            CURRENCIES.add(c.getCurrencyCode());
        }
    }
    
    @Override
    public void initialize(Currency constraintAnnotation) {
        
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return CURRENCIES.contains(value);
    }

}
