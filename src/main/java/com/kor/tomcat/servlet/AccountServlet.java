package com.kor.tomcat.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kor.common.Result;
import com.kor.tomcat.service.user_session_service.UserEntry;
import com.kor.tomcat.service.user_session_service.UserSessionService;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;

@WebServlet(urlPatterns = { "/login/signup", "/login/signin", "/api/login"})
public class AccountServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(NotebookServlet.class);
    private UserSessionService srv;

    @Override
    public void init() throws ServletException {
        srv = new UserSessionService();
        getServletContext().setAttribute("userSessionService", srv);
        logger.info("Initializing NotebookServlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.equals("/login/signup")) {
            req.getRequestDispatcher("/content/login/signup/index.jsp").forward(req, resp);
        } else if (uri.equals("/login/signin")) {
            req.getRequestDispatcher("/content/login/signin/index.jsp").forward(req, resp);
        } else {
            resp.sendError(0, "Unknown direction");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        if(!uri.matches("/api/login")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        logger.info("sc 0");

        resp.setContentType("application/json");

        JsonObject req_json = Json.createReader(req.getReader()).readObject();
        String action;
        String username;
        String password;
        try{
            action = req_json.getString("action");
            username = req_json.getString("username");
            password = req_json.getString("password");
        } catch(Exception e){
            fillApiResponse(resp.getWriter(), false, "Request Format Error", "");
            return;
        }

        logger.info("action is:");
        logger.info(action);
        if(action.matches("signin")){
            Result<String, UserSessionService.LoginErr> result = srv.login(username, password);
            if(result.isOk()){
                fillApiResponse(resp.getWriter(), true, "", result.ok().get());
                return;
            } else {
                logger.info("ts should be displaying");
                fillApiResponse(resp.getWriter(), false, result.err().get().toString(), "");
                return;
            }
        }
        logger.info("sc 1:");

        if(action.matches("signup")){
            Result<UserEntry, UserSessionService.AccountCreationErr> result = srv.createAccount(username, password);
            if(result.isOk()){
                fillApiResponse(resp.getWriter(), true, "", "");
                return;
            } else {
                fillApiResponse(resp.getWriter(), false, result.err().get().toString(), "");
                return;
            }
        }
        logger.info("sc 2");
    }

    private void fillApiResponse(PrintWriter writer, boolean success, String error_msg, String token){
        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("ok", success)
                .add("error_msg", error_msg)
                .build();

        try (JsonWriter jsonWriter = Json.createWriter(writer)) {
            jsonWriter.writeObject(jsonResponse);
        }
    }

    @Override
    public void destroy() {
        logger.info("Destroying NotebookServlet");
        super.destroy();
    }
}