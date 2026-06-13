package com.kor.tomcat.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kor.tomcat.service.notebook.NotebookService;
import com.kor.tomcat.service.user_session_service.UserSessionService;

@WebListener
public class ServiceRegistryListener implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger(ServiceRegistryListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Application starting up...");
        logger.info("Context path: {}", sce.getServletContext().getContextPath());
        logger.info("Server info: {}", sce.getServletContext().getServerInfo());

        ServletContext ctx = sce.getServletContext();
        
        NotebookService notebookService = new NotebookService(ctx.getRealPath("notebooks"));
        UserSessionService userSessionService = new UserSessionService();
        
        ctx.setAttribute("notebookService", notebookService);
        ctx.setAttribute("userSessionService", userSessionService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down...");
    }
}
