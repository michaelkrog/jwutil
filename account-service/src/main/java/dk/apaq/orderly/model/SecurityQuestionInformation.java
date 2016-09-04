package dk.apaq.orderly.model;


public class SecurityQuestionInformation {

    private String accountId;
    private SecurityQuestionType securityQuestionType;

    public SecurityQuestionInformation(String accountId, SecurityQuestionType securityQuestionType) {
        this.accountId = accountId;
        this.securityQuestionType = securityQuestionType;
    }

    public String getAccountId() {
        return accountId;
    }

    public SecurityQuestionType getSecurityQuestionType() {
        return securityQuestionType;
    }
    
    
}
