package com.kor.tomcat.service.notebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.kor.common.Result;
import com.kor.tomcat.service.notebook.deserialization.YamlNotebookConstructor;
import com.kor.tomcat.service.notebook.deserialization.YamlNotebookRoot;
import com.kor.tomcat.service.notebook.question.IQuestion;

public class NotebookService {

    private final String nb_db_path;

    public NotebookService(String notebook_db_path){
        nb_db_path = notebook_db_path;
    }

    public Result<ArrayList<IQuestion>, String> getQuestions(String notebook_path){
        Result<YamlNotebookRoot, String> notebook_r = this.getNotebook(notebook_path);
        if(notebook_r.isErr()){return Result.err(notebook_r.err().get());}
        YamlNotebookRoot data = notebook_r.ok().get();

        ArrayList<IQuestion> questions = data.contents;
        return Result.ok(questions);
    }

    /* 
    Err() If IO Erro occured
    Ok(Ok()) If the answer was correct
    Ok(Err()) If the answer was wrong
    */
    class AnswerVerificacionOK {
        public Result<IQuestion.AnswerRight, IQuestion.AnswerWrong> correction;
    }

    public Result<AnswerVerificacionOK, String> verifyAnswer(String notebook_path, int question, String answer){
        return Result.err("");
    }

    private Result<YamlNotebookRoot, String> getNotebook(String notebook_path){
        String nb_yaml_path = nb_db_path.concat(notebook_path).concat("/notebook.yaml");

        YamlNotebookRoot data;
        try(InputStream inputStream = new FileInputStream(nb_yaml_path);){
            Yaml yaml = new Yaml(new YamlNotebookConstructor(new LoaderOptions()));
            data = yaml.load(inputStream);
            System.out.println(data.name);
            System.out.println(data.contents.size());
        } catch (Exception e) {
            System.err.println(e);
            return Result.err(e.toString());
        }

        return Result.ok(data);
    }

    public ArrayList<NotebookListing> listNotebooks(){ 
        File[] directories = new File(nb_db_path).listFiles(File::isDirectory);
        ArrayList<NotebookListing> ret = new ArrayList<>();
        for (File file : directories) {
            String path = "/" + file.getName();
            Result<YamlNotebookRoot, String> res = getNotebook(path);
            if(res.isErr()){
                continue;
            }

            NotebookListing new_listing = new NotebookListing();
            new_listing.name = res.ok().get().name;
            new_listing.url = "/notebook" + path;

            ret.add(new_listing);
        }
        return ret;
    }
}
