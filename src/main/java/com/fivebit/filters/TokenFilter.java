package com.fivebit.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * Created by fivebit on 2017/5/15.
 * OncePerRequestFilter
 */
public class TokenFilter extends HttpServlet implements Filter {
    private static Logger log = LoggerFactory.getLogger(TokenFilter.class);

    /*
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String auth = httpServletRequest.getHeader("Authorization");
        log.info("begin token filter:auth"+auth);
        if(auth == null){
        }
        String item[] = auth.split(" ");
        if(item.length != 2 || item[0].equals("Bearer") == false){
            throw new ServletException("cant get Authorization");
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);

    }
    */

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding("utf-8");
        log.debug("begin token filter:auth");
        filterChain.doFilter(servletRequest,servletResponse);


    }
}
