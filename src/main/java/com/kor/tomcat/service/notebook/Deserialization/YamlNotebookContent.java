package com.kor.tomcat.service.notebook.Deserialization;

public class YamlNotebookContent {
    public String t;
    public String title;
    public String question;
    public String def_answer;
    
    @Override
    public String toString() {
        return String.format("ContentItem{t='%s', title='%s', question='%s', def_answer='%s'}", 
                            t, title, question, def_answer);
    }
}