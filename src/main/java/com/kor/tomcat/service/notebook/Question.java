package com.kor.tomcat.service.notebook;

public class Question {
    public String title = "title";
    public String question = "question";
    public String default_answer = "";

    @Override
    public String toString() {
        return "Title: '" + title + "', Question: '" + question +"', Default Response: '" + (default_answer.equals("") ? "null" : default_answer) + "'";
    }
}
