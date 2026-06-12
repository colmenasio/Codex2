package com.kor.tomcat.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        return;

        //HttpServletRequest httpReq = (HttpServletRequest) request;
        //HttpServletResponse httpResp = (HttpServletResponse) response;
//
        //logger.debug("Processing request: {} {}", httpReq.getMethod(), httpReq.getRequestURI());
//
        //httpResp.setHeader("X-Content-Type-Options", "nosniff");
        //httpResp.setHeader("X-Frame-Options", "DENY");
//
        //String path = httpReq.getRequestURI();
        //if (path.startsWith("/api/")) {
            //String apiKey = httpReq.getHeader("X-API-Key");
            //if (apiKey == null || !apiKey.equals("test-key-123")) {
                //httpResp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                //return;
            //}
        //}
//
        //chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed");
    }
}
