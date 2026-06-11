package com.kor.tomcat.controller;

import com.kor.tomcat.service.HelloService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;

import java.io.IOException;

@WebServlet(urlPatterns = { "/hello", "/api/hello" })
public class HelloServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(HelloServlet.class);
    private HelloService helloService;

    @Override
    public void init() throws ServletException {
        logger.info("Initializing HelloServlet");
        helloService = new HelloService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("name");
        String response = helloService.getGreeting(name);

        logger.info("Handling GET request for name: {}", name);

        String acceptHeader = req.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            resp.setContentType("application/json");

            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("message", response)
                    .build();

            try (JsonWriter jsonWriter = Json.createWriter(resp.getWriter())) {
                jsonWriter.writeObject(jsonResponse);
            }
        } else {
            req.setAttribute("message", response);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("name");
        String message = helloService.createPersonalizedMessage(name);

        resp.setContentType("application/json");

        JsonObject jsonResponse = Json.createObjectBuilder()
                .add("status", "success")
                .add("message", message)
                .build();

        try (JsonWriter jsonWriter = Json.createWriter(resp.getWriter())) {
            jsonWriter.writeObject(jsonResponse);
        }
    }

    @Override
    public void destroy() {
        logger.info("Destroying HelloServlet");
        super.destroy();
    }
}