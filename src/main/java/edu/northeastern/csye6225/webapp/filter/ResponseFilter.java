package edu.northeastern.csye6225.webapp.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(servletResponse instanceof HttpServletResponse){
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setHeader("cache-control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            //call next filter if any
            filterChain.doFilter(servletRequest, httpResponse);
        }
        else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }
}

