package com.kor.tomcat.service.notebook.question;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.kor.common.Result;

public class PythonQuestion implements IQuestion {
    public String title = "title";
    public String question = "question";
    public String def_answer = "";
    public String checker_script_path = null;

    public String verification_script_path;

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

    // This wont work in windows lmau pero sopla
    @Override
    public Result<AnswerRight, AnswerWrong> checkAnswer(String answer) {
        System.err.println("lmaooooooooooooo");
        CommadExecutor.execute("pwd");
        return Result.err(new AnswerWrong("not implemented lmao"));
    }
}

class CommadExecutor {
    public static void execute(String... command) {
        try {//"ls", "-la", "/nonexistent"
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            
            // Capture stdout
            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // Capture stderr
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            System.out.println("STDOUT:");
            String line;
            while ((line = stdOut.readLine()) != null) {
                System.out.println(line);
            }
            
            System.out.println("\nSTDERR:");
            while ((line = stdErr.readLine()) != null) {
                System.out.println(line);
            }
            
            int exitCode = process.waitFor();
            System.out.println("\nExit code: " + exitCode);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}