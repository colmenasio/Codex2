package com.kor.tomcat.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kor.tomcat.service.notebook.NotebookListing;
import com.kor.tomcat.service.notebook.NotebookService;
import com.kor.tomcat.service.notebook.YamlNotebookDb;
import com.kor.tomcat.service.user_session_service.UserSessionService;

@WebServlet(urlPatterns = "/home/*")
public class HomeServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(HomeServlet.class);
    private NotebookService nb_service;
    private UserSessionService usr_service;

    @Override
    public void init() throws ServletException {
        logger.info("Initializing HomeServlet");
        this.nb_service = (NotebookService) this.getServletContext().getAttribute("notebookService");
        this.usr_service = (UserSessionService) this.getServletContext().getAttribute("userSessionService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.equals("/home")) {
            handleHomeRequest(req, resp);
        } else if (uri.equals("/login/signin")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown direction");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    @Override
    public void destroy() {
        logger.info("Destroying HomeServlet");
        super.destroy();
    }

    private void handleHomeRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long user_id = (Long) req.getSession().getAttribute("userId");
        String currentUser = usr_service.getUserDataById(user_id).data.username;
        ArrayList<NotebookListing> notebooks = nb_service.getNotebookDb().listNotebooks();

        req.setAttribute("notebooks", notebooks);
        req.setAttribute("currentUser", currentUser);

        req.getRequestDispatcher("/content/home/index.jsp").forward(req, resp);
    }
}
