package com.kor.tomcat.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kor.common.Result;
import com.kor.tomcat.service.notebook.NotebookService;
import com.kor.tomcat.service.notebook.deserialization.YamlNotebookRoot;
import com.kor.tomcat.service.notebook.question.IQuestion;
import com.kor.tomcat.service.user_session_service.UserSessionService;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

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

        String notebook_path = notebookPathfomURI(req.getRequestURI());
        if(notebook_path == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //logger.info("Getting notebook: {}", notebook_path);

        Result<YamlNotebookRoot, String> notebook = nb_srv.getNotebookDb().getNotebook(notebook_path);
        if(notebook.isErr()){
            resp.sendError(3, "Unexpected Error lmau");
            return;
        }
        YamlNotebookRoot notebook_unwrapped = notebook.ok().get();
        Long user_id = (Long) req.getSession().getAttribute("userId");
        String current_user = usr_srv.getUserDataById(user_id).data.username;

        //logger.info("Sending {} questions", notebook_unwrapped.contents.size());

        req.setAttribute("notebookName", notebook_unwrapped.name);
        req.setAttribute("currentUser", current_user);
        req.setAttribute("questions", notebook_unwrapped.contents);
        req.getRequestDispatcher("/content/notebook/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //String uri = req.getRequestURI();
        //if (!uri.matches("/api/notebook")) {
        //    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        //}

        resp.setContentType("application/json");
        JsonObject req_json = Json.createReader(req.getReader()).readObject();
        String action = req_json.getString("action", null);
        String auth_token = req_json.getString("authToken", null);
        if (action == null || auth_token == null) {
            fillApiResponse(resp.getWriter(), false, "Request Format Error", null, null);
            return;
        }
        HttpSession sesh = req.getSession(false);
        if (sesh == null ){
            fillApiResponse(resp.getWriter(), false, "Unknown session", null, null);
            return;
        }
        String sesh_auth_token = (String)sesh.getAttribute("authToken");
        if (sesh_auth_token == null || !sesh_auth_token.equals(auth_token)){
            fillApiResponse(resp.getWriter(), false, "Authentication Error", null, null);
            return;
        }

        if (action.matches("submit_answer")) {
            hanldeSubmitAnswer(req, resp, req_json);
        } else if (action.matches("request_stored_answers")) {
            hanldeRetrieveStoredAnswers(req, resp, req_json);
        } else if (action.matches("delete_stored_answers")) {
            hanldeDeleteStoredAnswers(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void hanldeRetrieveStoredAnswers(HttpServletRequest req, HttpServletResponse resp, JsonObject req_json)
            throws ServletException, IOException {
        Long user_id = (Long)req.getSession().getAttribute("userId");
        String notebook_path = notebookPathfomURI(req.getRequestURI());
        if(notebook_path == null){
            logger.info(notebook_path);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        JsonObject data = nb_srv.getUserAnswerData(user_id, notebook_path);
        JsonObject response = Json.createObjectBuilder()
            .add("ok", true)
            .add("data", data)
            .build();

        try (JsonWriter jsonWriter = Json.createWriter(resp.getWriter())) {
            jsonWriter.writeObject(response);
        }
    }

    private void hanldeSubmitAnswer(HttpServletRequest req, HttpServletResponse resp, JsonObject req_json)
            throws ServletException, IOException {
        Long user_id = (Long)req.getSession().getAttribute("userId");
        String notebook_path = notebookPathfomURI(req.getRequestURI());
        if(notebook_path == null){
            logger.info(notebook_path);
            logger.info(req.getRequestURI());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int question_id;
        String answer;
        try {
            question_id = req_json.getInt("questionId");
            answer = req_json.getString("answer");
        } catch (Exception e) {
            fillApiResponse(resp.getWriter(), false, "Request Format Error" + req_json.toString(), null, null);
            return;
        }

        Result<Result<IQuestion.AnswerRight, IQuestion.AnswerWrong>, String> result = nb_srv.correctAndStoreUserAnswer(user_id, notebook_path, question_id, answer);
        if(result.isErr()){
            fillApiResponse(resp.getWriter(), false, result.err().get(), null, null);
            return;
        }
        Result<IQuestion.AnswerRight, IQuestion.AnswerWrong> correction = result.ok().get();
        fillApiResponse(resp.getWriter(),true, null, correction.isOk(), correction.flatten((right) -> {return null;}, (wrong) -> {return wrong.why;}));
        
        return;
    }

    private void hanldeDeleteStoredAnswers(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long user_id = (Long)req.getSession().getAttribute("userId");
        String notebook_path = notebookPathfomURI(req.getRequestURI());
        if(notebook_path == null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        nb_srv.removeUserAnswerData(user_id, notebook_path);
        fillApiResponse(resp.getWriter(), true, null, null, null);
        return;
    }

    private void fillApiResponse(PrintWriter writer, boolean success, String error_msg, Boolean is_correct, String correction_msg) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("ok", success);
        if (is_correct != null) {
            builder.add("is_correct", is_correct);
        }
        if (error_msg != null) {
            builder.add("error_msg", error_msg);
        }
        if (correction_msg != null) {
            builder.add("correction_msg", correction_msg);
        }
        JsonObject jsonResponse = builder.build();

        try (JsonWriter jsonWriter = Json.createWriter(writer)) {
            jsonWriter.writeObject(jsonResponse);
        }
    }

    private String notebookPathfomURI(String uri){
        String prefix = "/notebook";
        if(!uri.startsWith(prefix)){
            return null;
        }
        return uri.substring(prefix.length());
    }

    @Override
    public void destroy() {
        logger.info("Destroying NotebookServlet");
        super.destroy();
    }
}