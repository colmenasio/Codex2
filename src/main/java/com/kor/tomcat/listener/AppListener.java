package com.kor.tomcat.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger(AppListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Application starting up...");
        logger.info("Context path: {}", sce.getServletContext().getContextPath());
        logger.info("Server info: {}", sce.getServletContext().getServerInfo());

        sce.getServletContext().setAttribute("appVersion", "1.0.0");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down...");
    }
}
