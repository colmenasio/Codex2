package com.kor.tomcat.service.notebook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.kor.common.Result;
import com.kor.tomcat.service.notebook.Deserialization.YamlNotebookContent;
import com.kor.tomcat.service.notebook.Deserialization.YamlNotebookRoot;

public class NotebookService {

    private final String nb_db_path;

    public NotebookService(String notebook_db_path){
        nb_db_path = notebook_db_path;
    }

    public Result<ArrayList<Question>, String> getQuestions(String notebook_path){
        Result<YamlNotebookRoot, String> notebook_r = this.getNotebook(notebook_path);
        if(notebook_r.isErr()){return Result.err(notebook_r.err().get());}
        YamlNotebookRoot data = notebook_r.ok().get();

        ArrayList<Question> questions = new ArrayList<>();
        for(int i = 0; i < data.contents.size(); i++){
            YamlNotebookContent content = data.contents.get(i);
            if(!content.t.equals("std_question")) {
                continue;
            }
            
            Question new_question = new Question();
            new_question.title = content.title;
            new_question.question = content.question;
            new_question.default_answer = content.def_answer == null ? "" : content.def_answer;
            questions.add(new_question);
        }
        return Result.ok(questions);
    }

    /* 
    Err() If IO Erro occured
    Ok(Ok()) If the answer was correct
    Ok(Err()) If the answer was wrong
    */
    public Result<Result<String, String>, String> verifyAnswer(String notebook_path, int question, String answer){
        return Result.err("");
    }

    private Result<YamlNotebookRoot, String> getNotebook(String notebook_path){
        String nb_yaml_path = nb_db_path.concat("/").concat(notebook_path).concat("/notebook.yaml");

        YamlNotebookRoot data;
        try(InputStream inputStream = new FileInputStream(nb_yaml_path);){
            Yaml yaml = new Yaml(new Constructor(YamlNotebookRoot.class, new LoaderOptions()));
            data = yaml.load(inputStream);
        } catch (Exception e) {
            return Result.err(e.toString());
        }

        return Result.ok(data);
    }

}
