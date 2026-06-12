package com.kor.tomcat.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kor.common.Result;
import com.kor.tomcat.service.notebook.NotebookService;
import com.kor.tomcat.service.notebook.Question;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = { "/notebook/*"})
public class NotebookServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(NotebookServlet.class);
    private NotebookService srv;

    @Override
    public void init() throws ServletException {
        logger.info("Initializing NotebookServlet");
        srv = new NotebookService(this.getServletContext().getRealPath("notebooks"));
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

        Result<ArrayList<Question>, String> questions_r = srv.getQuestions(notebook_path);
        if(questions_r.isErr()){
            logger.error(questions_r.err().get());
            resp.sendError(3, "Unexpected Error lmau");
            return;
        }
        List<Question> questions = (List<Question>)questions_r.ok().get();

        logger.info("Sending {} questions", questions.size());
        //resp.getWriter().println("<html><body>placeholder_meow<body></html>");
        req.setAttribute("questions", questions);
        req.getRequestDispatcher("/content/notebook/notebook.jsp").forward(req, resp);
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