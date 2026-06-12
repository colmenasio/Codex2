package com.kor.tomcat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet(urlPatterns = "/home/*")
public class HomeServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(HomeServlet.class);

    @Override
    public void init() throws ServletException {
        logger.info("Initializing HomeServlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.equals("/home")) {
            req.getRequestDispatcher("/content/home/index.jsp").forward(req, resp);
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
}
