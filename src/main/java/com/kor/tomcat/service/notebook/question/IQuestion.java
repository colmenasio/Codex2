package com.kor.tomcat.service.notebook.question;

import com.kor.common.Result;

public interface IQuestion {
    public String getTitle();
    public String getQuestion();
    public String getDefaultAnswer();
    public Result<AnswerRight, AnswerWrong> checkAnswer(String answer);

    class AnswerRight {}
    class AnswerWrong {
        public String why;

        public AnswerWrong(String why_){
            this.why = why_;
        }
    }
}
