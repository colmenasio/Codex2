package com.kor.tomcat.service.notebook.question;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.kor.common.Result;

public class PythonQuestion implements IQuestion {
    public String title = "title";
    public String question = "question";
    public String def_answer = "";
    public String evaluator = "";
    public String answer_entry_point = "";

    static private final String check_script = ""+
        "import sys\n"+
        "stdout = sys.stdout\n"+
        "try:\n"+
        "\tsys.stdout = open('/dev/null', 'w')\n"+
        "\tevaluator_ns = {}\n"+
        "\tanswer_ns = {}\n"+
        "\texec(sys.argv[1], evaluator_ns)\n"+
        "\texec(sys.argv[2], answer_ns)\n"+
        "\tanswer_ep = answer_ns.get(sys.argv[3])\n"+
        "\tevaluator_ep = evaluator_ns.get('main')\n"+
        "\tif answer_ep is None:\n"+
        "\t\tprint('answer entry symbol not found')\n"+
        "\tif evaluator_ep is None:\n"+
        "\t\tprint('evaluator entry symbol not found')\n"+
        "\t\texit(1)\n"+
        "\trc, comment = evaluator_ep(answer_ep)\n"+
        "\tstdout.write(comment)\n"+
        "\texit(rc)\n"+
        "except Exception as e:\n"+
        "\tstdout.write(str(e))\n"+
        "\texit(1)\n";


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
    // Also sin sindboxxear esto es una brecha de seguridad maravillosisisma
    @Override
    public Result<AnswerRight, AnswerWrong> checkAnswer(String answer) {
        Result<CommandExecutor.ExecutionOk, CommandExecutor.ExecutionErr> result = CommandExecutor.execute("python3", "-c", check_script, 
            evaluator,//"def main(target):\n\treturn (0, 'fyne') if target() == 1 else (2, 'ni sumar sabes, votante de vox tenias que ser')\n", 
            answer,//"def sum():\n\tprint('meow')\n\treturn 3\n",
            answer_entry_point//"sum"
        );
        if(result.isErr()){
            Result.err(new AnswerWrong("Internal error. Probably cause the python schecking is linux exclusive."));
        }
        CommandExecutor.ExecutionOk output = result.ok().get();
        return output.exit_c == 0 ? Result.ok(new AnswerRight()) : Result.err(new AnswerWrong(output.stdout_log.equals("") ? "Idk something is wrong" : output.stdout_log));
    }
}

class CommandExecutor {
    static public class ExecutionOk{
        public int exit_c;
        public String stdout_log; 
    }
    static public class ExecutionErr{
        public String what;
    }

    public static Result<ExecutionOk, ExecutionErr> execute(String... command) {
        try {//"ls", "-la", "/nonexistent"
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            ExecutionOk ok = new ExecutionOk();
            ok.exit_c = process.waitFor();
            ok.stdout_log =  stdOut.lines().collect(Collectors.joining("\n"));
            return Result.ok(ok);

        } catch (Exception e) {
            ExecutionErr err = new ExecutionErr();
            err.what = e.toString();
            return Result.err(err);
        }
    }
}