package com.kor.tomcat.service.notebook;

import com.kor.common.Result;
import com.kor.tomcat.service.notebook.deserialization.YamlNotebookRoot;
import com.kor.tomcat.service.notebook.question.IQuestion;
import com.kor.tomcat.service.user_session_service.INotebookUserAnswerStorage;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class NotebookService {
    private final YamlNotebookDb notebook_db;
    private final INotebookUserAnswerStorage answer_db;

    public NotebookService(YamlNotebookDb notebook_storage, INotebookUserAnswerStorage answer_storage) {
        notebook_db = notebook_storage;
        answer_db = answer_storage;
    }

    public YamlNotebookDb getNotebookDb() {
        return notebook_db;
    }

    public Result<Result<IQuestion.AnswerRight, IQuestion.AnswerWrong>, String> correctAndStoreUserAnswer(Long user_id,
            String notebook_id, int question_id, String answer) {
        Result<YamlNotebookRoot, String> notebook_read = notebook_db.getNotebook(notebook_id);
        if (notebook_read.isErr()) {
            return Result.err(notebook_read.err().get());
        }
        YamlNotebookRoot notebook = notebook_read.ok().get();
        if (notebook.contents.size() <= question_id) {
            return Result.err("Unknown question index");
        }

        IQuestion question = notebook.contents.get(question_id);
        Result<IQuestion.AnswerRight, IQuestion.AnswerWrong> correction_result = question.checkAnswer(answer);

        JsonObject user_answers = answer_db.getAnswerData(notebook_id, user_id);
        JsonObjectBuilder builder;

        if (user_answers == null) {
            builder = Json.createObjectBuilder();
        } else {
            builder = Json.createObjectBuilder(user_answers);
        }

        builder.add(
                "" + question_id,
                Json.createObjectBuilder()
                        .add("is_correct", correction_result.isOk())
                        .add("raw_answer", answer)
                        .build());

        user_answers = builder.build();
        answer_db.saveAnswerData(notebook_id, user_id, user_answers);

        return Result.ok(correction_result);
    }

    public JsonObject getUserAnswerData(Long user_id, String notebook_id) {
        JsonObject ret = answer_db.getAnswerData(notebook_id, user_id);
        return ret != null ? ret : Json.createObjectBuilder().build();
    }

    public void removeUserAnswerData(Long user_id, String notebook_id) {
        answer_db.deleteEntry(notebook_id, user_id);
    }
}
