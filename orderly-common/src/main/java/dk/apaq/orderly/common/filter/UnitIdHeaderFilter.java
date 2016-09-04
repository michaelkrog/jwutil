package dk.apaq.orderly.common.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class UnitIdHeaderFilter implements Filter {

    private static final ThreadLocal<String> CURRENT_UNIT_ID = new ThreadLocal<>();
    private static final String HEADER = "X-Unit-Id";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { /* NOOP */}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hreq = (HttpServletRequest) request;
        
        CURRENT_UNIT_ID.set(hreq.getHeader(HEADER));
        
        chain.doFilter(request, response);
    }
    

    @Override
    public void destroy() { /* NOOP */ }

    public static String getCurrentUnitId() {
        return CURRENT_UNIT_ID.get();
    }
    
}
