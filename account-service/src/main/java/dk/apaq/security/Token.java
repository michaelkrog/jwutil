package dk.apaq.security;

/**
 * The security token.
 * The token used for xauth.
 */
public class Token {

    String token;
    long expires;

    public Token(String token, long expires){
        this.token = token;
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public long getExpires() {
        return expires;
    }
}
