package dk.apaq.orderly.security;

import dk.apaq.orderly.model.Account;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;


public class TokenGenerator {

    public static String generateValidateMailToken(Account account, String salt) {
        Assert.notNull(account, "account must be specified.");
        Assert.isTrue(account.isNew() == false, "person has never been persisted.");
        Assert.notNull(account.getEmail(), "person has not email address specified.");
        String signature = DigestUtils.md5DigestAsHex((account.getEmail() + ":" + salt).getBytes());
        return DigestUtils.md5DigestAsHex((account.getEmail() + ":" + signature).getBytes());
    }
}
