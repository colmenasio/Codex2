package com.kor.tomcat.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kor.common.Result;
import com.kor.tomcat.service.user_session_service.UserEntry;
import com.kor.tomcat.service.user_session_service.UserSessionService;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;
import java.util.UUID;

@WebServlet(urlPatterns = { "/login/signup", "/login/signin", "/api/login" })
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
        if (!uri.matches("/api/login")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        resp.setContentType("application/json");

        JsonObject req_json = Json.createReader(req.getReader()).readObject();
        String action;
        String username;
        String password;
        try {
            action = req_json.getString("action");
            username = req_json.getString("username");
            password = req_json.getString("password");
        } catch (Exception e) {
            fillApiResponse(resp.getWriter(), false, "Request Format Error", "");
            return;
        }

        if (action.matches("signin")) {
            Result<UserSessionService.AuthOk, UserSessionService.AuthErr> result = srv.authenticate(username, password);

            if (result.isOk()) {
                String auth_token = UUID.randomUUID().toString();
                HttpSession session = req.getSession();
                session.setAttribute("authToken", auth_token);
                session.setAttribute("user_id", result.ok().get().user.id);
                session.setMaxInactiveInterval(30 * 60);
                fillApiResponse(resp.getWriter(), true, null, auth_token);
                return;
            } else {
                fillApiResponse(resp.getWriter(), false, result.err().get().toString(), null);
                return;
            }
        }

        if (action.matches("signup")) {
            Result<UserEntry, UserSessionService.AccountCreationErr> result = srv.createAccount(username, password);
            if (result.isOk()) {
                fillApiResponse(resp.getWriter(), true, null, null);
                return;
            } else {
                fillApiResponse(resp.getWriter(), false, result.err().get().toString(), null);
                return;
            }
        }
    }

    private void fillApiResponse(PrintWriter writer, boolean success, String error_msg, String token) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("ok", success);
        if (token != null) {
            builder.add("token", token);
        }
        if (error_msg != null) {
            builder.add("error_msg", error_msg);
        }
        JsonObject jsonResponse = builder.build();

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