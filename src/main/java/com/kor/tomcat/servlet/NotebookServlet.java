package com.kor.tomcat.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kor.common.Result;
import com.kor.tomcat.service.notebook.NotebookService;
import com.kor.tomcat.service.notebook.deserialization.YamlNotebookRoot;
import com.kor.tomcat.service.user_session_service.UserSessionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = { "/notebook/*"})
public class NotebookServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(NotebookServlet.class);
    private NotebookService nb_srv;
    private UserSessionService usr_srv;

    @Override
    public void init() throws ServletException {
        logger.info("Initializing NotebookServlet");
        this.nb_srv = (NotebookService) this.getServletContext().getAttribute("notebookService");
        this.usr_srv = (UserSessionService) this.getServletContext().getAttribute("userSessionService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        String prefix = "/notebook";
        if(!uri.startsWith(prefix)){
            resp.sendError(1);
            return;
        }
        String notebook_path = uri.substring(prefix.length());
        logger.info("Getting notebook: {}", notebook_path);

        Result<YamlNotebookRoot, String> notebook = nb_srv.getNotebook(notebook_path);
        if(notebook.isErr()){
            logger.error(notebook.err().get());
            resp.sendError(3, "Unexpected Error lmau");
            return;
        }
        YamlNotebookRoot notebook_unwrapped = notebook.ok().get();
        Long user_id = (Long) req.getSession().getAttribute("userId");
        String current_user = usr_srv.getUserDataById(user_id).data.username;

        logger.info("Sending {} questions", notebook_unwrapped.contents.size());

        req.setAttribute("notebooks", notebook_unwrapped.name);
        req.setAttribute("currentUser", current_user);
        req.setAttribute("questions", notebook_unwrapped.contents);
        req.getRequestDispatcher("/content/notebook/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    @Override
    public void destroy() {
        logger.info("Destroying NotebookServlet");
        super.destroy();
    }
}