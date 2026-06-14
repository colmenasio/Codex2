package com.kor.tomcat.service.notebook.question;

import com.kor.common.Result;

public class ClosedQuestion implements IQuestion {
    public String title = "title";
    public String question = "question";
    public String def_answer = "";
    public String correct_answer = null;

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
        if(answer != null && correct_answer != null && answer.equals(correct_answer)){
            return Result.ok(new AnswerRight());
        } else {
            return Result.err(new AnswerWrong("Wrong answer newphew"));
        }
    }
}
