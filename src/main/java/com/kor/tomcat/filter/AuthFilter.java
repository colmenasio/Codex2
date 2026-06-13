package com.kor.tomcat.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    //private static final String LOGIN_PAGE = "content/login/signin/index.jsp";
    private static final String LOGIN_PAGE = "/login/signin";
    private static final ArrayList<String> ALLOWED_UNLOGGED = new ArrayList<>(Arrays.asList(
        "/login",
        "/api",
        "/content/login"
    ));

    private static final Logger logger = LogManager.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        boolean isAllowedFreely = false;
        for(int i = 0; i < ALLOWED_UNLOGGED.size(); i++){
            if(path.startsWith(ALLOWED_UNLOGGED.get(i))){
                isAllowedFreely = true;
                break;
            };
        }

        boolean isAuthenticated = (session != null && session.getAttribute("authToken") != null);
        
        //logger.info("Filtering {} request. Is Allowed Sessionless {}. Has Session {}", path, isAllowedFreely, isAuthenticated);

        if (isAllowedFreely || isAuthenticated) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(contextPath + LOGIN_PAGE);
        }
    }
    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed");
    }
}
