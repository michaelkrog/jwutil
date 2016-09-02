package dk.apaq.jwutil.common.util;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdGenerator /*implements IdentifierGenerator, Configurable*/ {

    private static final Logger LOG = LoggerFactory.getLogger(IdGenerator.class);
    private String prefix;
    private static final Hashids hashids = new Hashids();
    private static final Random random = new Random();
    
    public static String generate(String prefix) {
        int random1 = random.nextInt(Integer.MAX_VALUE), random2 = random.nextInt(Integer.MAX_VALUE);
        LOG.debug("Generating new ID [random1={};random2={}]");
        String id = hashids.encode(random1, random2);
        if (prefix != null) {
            return prefix + "-" + id;
        } else {
            return id;
        }
    }
    
    public static String generate(String prefix, String hex) {
        LOG.debug("Generating new ID [hex={}]", hex);
        
        String id = hashids.encodeHex(hex);
        if (prefix != null) {
            return prefix + "-" + id;
        } else {
            return id;
        }
    }
/*
    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        
    }

    @Override
    public void configure(Type type, Properties params, Dialect d) throws MappingException {
        prefix = params.getProperty("prefix");
    }
*/
}
