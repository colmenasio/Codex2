package com.kor.tomcat.service.user_session_service;

import jakarta.json.JsonObject;

public interface INotebookUserAnswerStorage {
    JsonObject getAnswerData(String notebook_id, Long user_id);
    void saveAnswerData(String notebook_id, Long user_id, JsonObject answer_data);
    void deleteEntry(String notebook_id, Long user_id);
}