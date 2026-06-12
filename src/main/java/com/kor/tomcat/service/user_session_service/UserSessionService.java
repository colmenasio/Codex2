package com.kor.tomcat.service.user_session_service;

import java.security.MessageDigest;
import java.util.Base64;

import com.kor.common.Result;


public class UserSessionService {

    private IUserSessionStorage user_db;

    public UserSessionService(){
        user_db = new DerbyUserSessionStorage();
    }

    // ========== ACCOUNT CREATION ==========
    public class AccountCreationErr{
        public static final int WTF = 0;
        public static final int ACC_ALREADY_EXISTS = 1;
        public static final int INVALID_DATA = 2;

        public int errn;

        public AccountCreationErr(int error_code){
            this.errn = error_code;
        }


        @Override
        public String toString(){
            switch (errn) {
                case ACC_ALREADY_EXISTS:
                    return "Account already exist";
                case INVALID_DATA:
                    return "Invalid password or username";
                default:
                    return "Wtf";
            }
        }
    }

    public class LoginErr {
        public static final int WTF = 0;
        public static final int ACC_DOESNT_EXIST = 1;
        public static final int AUTH_ERR = 2;

        public int errn;

        public LoginErr(int error_code){
            this.errn = error_code;
        }

        @Override
        public String toString(){
            switch (errn) {
                case ACC_DOESNT_EXIST:
                    return "Account doesnt exist";
                case AUTH_ERR:
                    return "Authentication error";
                default:
                    return "Wtf";
            }
        }
    }

    public Result<UserEntry, AccountCreationErr> createAccount(String username, String password_raw) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            return Result.err(new AccountCreationErr(AccountCreationErr.INVALID_DATA));
        }
        if (password_raw == null || password_raw.length() < 8) {
            return Result.err(new AccountCreationErr(AccountCreationErr.INVALID_DATA));
        }

        // Check if user already exists
        UserEntry existing_user = user_db.getUserByUsername(username);
        if (existing_user != null){
            return Result.err(new AccountCreationErr(AccountCreationErr.ACC_ALREADY_EXISTS));
        }

        String password_hashed = this.hash(password_raw);
        if(password_hashed.equals("")){
            return Result.err(new AccountCreationErr(AccountCreationErr.WTF));
        } 

        UserData data = new UserData();
        data.username = username;
        data.pwd_hash = password_hashed;

        UserEntry new_user = user_db.createUser(data);
        if(new_user == null){
            return Result.err(new AccountCreationErr(AccountCreationErr.WTF));
        } 
        return Result.ok(new_user);
    }

    // Does literally everything. Authentication, retrieval and session creation 
    public Result<String, LoginErr> login(String username, String password_raw){
        UserEntry user = user_db.getUserByUsername(username);
        if(user==null){
            return Result.err(new LoginErr(LoginErr.ACC_DOESNT_EXIST));
        }

        if (!user.data.pwd_hash.equals(this.hash(password_raw))){
            return Result.err(new LoginErr(LoginErr.AUTH_ERR));
        }

        String token = user_db.createOrGetSession(user.id);
        return Result.ok(token);
    }

    public UserData getUserData(String token) {
        return user_db.getSessionUserData(token);
    }

    public UserEntry getUserDataById(Long user_id) {
        return user_db.getUserById(user_id);
    }



    private String hash(String input){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch(Exception e) {
            return "";
        }
    }
}