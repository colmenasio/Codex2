package com.kor.tomcat.service.notebook.question;

import com.kor.common.Result;

public class OpenQuestion implements IQuestion {
    public String title = "title";
    public String question = "question";
    public String def_answer = "";

    @Override
    public String toString() {
        return "Title: '" + title + "', Question: '" + question +"', Default Response: '" + (def_answer.equals("") ? "null" : def_answer) + "'";
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public String getDefaultAnswer() {
        return def_answer;
    }

    @Override
    public Result<AnswerRight, AnswerWrong> checkAnswer(String answer) {
        return Result.ok(new AnswerRight());
    }
}
