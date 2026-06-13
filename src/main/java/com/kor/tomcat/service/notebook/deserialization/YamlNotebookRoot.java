package com.kor.tomcat.service.notebook.deserialization;

import java.util.ArrayList;

import com.kor.tomcat.service.notebook.question.IQuestion;

public class YamlNotebookRoot {
    public String name;
    public ArrayList<IQuestion> contents;
}
