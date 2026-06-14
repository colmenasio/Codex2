package com.kor.tomcat.service.notebook.deserialization;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;

import com.kor.tomcat.service.notebook.question.ClosedQuestion;
import com.kor.tomcat.service.notebook.question.OpenQuestion;
import com.kor.tomcat.service.notebook.question.PythonQuestion;

public class YamlNotebookConstructor extends Constructor {
    
    public YamlNotebookConstructor(LoaderOptions options) {
        super(YamlNotebookRoot.class, options);

        this.addTypeDescription(
            new TypeDescription(OpenQuestion.class, "!open_question")
        );
        this.addTypeDescription(
            new TypeDescription(ClosedQuestion.class, "!closed_question")
        );
        this.addTypeDescription(
            new TypeDescription(PythonQuestion.class, "!python_question")
        );
    }
}
