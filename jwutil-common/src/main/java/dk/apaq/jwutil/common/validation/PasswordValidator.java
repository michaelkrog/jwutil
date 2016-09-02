package dk.apaq.jwutil.common.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This is class is used to validate the password. The rule for the password validation is
 * given below.
 * <ul>
 * <li>1. The total length of password should be 6 to 20 characters.</li>
 * <li>2. One upper case and one lower case</li>
 * <li>3. One numeric digit.</li>
 * </ul>
 * 
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {
    /**
     * Regular expression for password validation. The rule is given below.
     * <ul>
     * <li>1. The total length of password should be 6 to 20 characters.</li>
     * <li>2. One upper case and one lower case</li>
     * <li>3. One numeric digit.</li>
     * </ul>
     */
    private String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.validation.ConstraintValidator#initialize(java.lang.annotation.
     * Annotation)
     */
    public void initialize(Password pwd) {
	// do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     * javax.validation.ConstraintValidatorContext)
     */
    public boolean isValid(String str, ConstraintValidatorContext ctx) {
	return validate(str);
    }

    /**
     * This method is used to validate a password based on the regular
     * expression given above.
     * 
     * @param password
     *            of type String
     * @return boolean value either true or false
     */
    public boolean validate(final String password) {
	Matcher matcher;
	Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
	matcher = pattern.matcher(password);
	return matcher.matches();
    }

}
