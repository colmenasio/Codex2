package com.kor.tomcat.service.user_session_service;

public interface IUserSessionStorage {
    UserEntry getUserByUsername(String username);
    UserEntry getUserById(Long user_id);
    UserEntry createUser(UserData data);
    //String createOrGetSession(Long user_id);
    //UserData getSessionUserData(String user_id);

}