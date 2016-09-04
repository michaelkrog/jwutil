package dk.apaq.orderly.service;

import dk.apaq.orderly.model.RecaptchaResponse;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * Service for verifying reCaptcha response
 * https://www.google.com/recaptcha/intro/index.html
 */
@Service
public class RecaptchaService {

    private static final Logger LOG = LoggerFactory.getLogger(RecaptchaService.class);
    private static final String PROPERTY_NAME_RECAPTCHA_URL = "recaptcha.url";
    private static final String PROPERTY_NAME_RECAPTCHA_SECRET = "recaptcha.secret";
    
    @Resource
    private Environment env;
    
    private String url;
    private String secret;
    private RestTemplate restOp = new RestTemplate();

    @PostConstruct
    protected void init() {
        setUrl(env.getProperty(PROPERTY_NAME_RECAPTCHA_URL));
        setSecret(env.getProperty(PROPERTY_NAME_RECAPTCHA_SECRET));
        Assert.notNull(url, "Url must be specified.");
        Assert.notNull(secret, "Secret must be specified.");
    }
    
    public void setUrl(String url) {
        LOG.debug("Setting url [url={}]", url);
        this.url = url;
    }

    public void setSecret(String secret) {
        LOG.debug("Setting secret [secret={}]", secret == null ? "Null" : "Not Null");
        this.secret = secret;
    }
    
    public boolean verifyResponse(String recaptchaResponse, String remoteIp) {
        if(recaptchaResponse == null) {
            throw new NullPointerException("recaptchaResponse must be specified.");
        }
        
        String requestUrl = url + "?secret={secret}&response={response}";
        ResponseEntity<RecaptchaResponse> response = restOp.postForEntity(requestUrl, null, RecaptchaResponse.class, secret, recaptchaResponse);
        
        return response.getBody().isSuccess();
    }
}
