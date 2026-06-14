package com.kor.tomcat.service.user_session_service;

import java.io.InputStream;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class DerbyStorage implements IUserSessionStorage, INotebookUserAnswerStorage {
    private static final String DB_URL = "jdbc:derby:userdb;create=true";

    public DerbyStorage() {
        System.setProperty("derby.stream.error.file", "/dev/null");
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE users (" +
                    "id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "pwd_hash VARCHAR(255) NOT NULL)");

        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) { // Table already exists
                e.printStackTrace();
            }
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement()) {
            //stmt.execute("DROP TABLE notebook_user_answers ");
            stmt.execute("CREATE TABLE notebook_user_answers (" +
                    "id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "user_id BIGINT NOT NULL," +
                    "notebook_id VARCHAR(255) NOT NULL," +
                    "answer_data_json LONG VARCHAR NOT NULL)");

        } catch (SQLException e) {
            System.err.println(e.getSQLState().equals("X0Y32"));
            if (!e.getSQLState().equals("X0Y32")) { // Table already exists
                e.printStackTrace();
            }
        }
    }

    @Override
    public UserEntry getUserByUsername(String username) {
        String sql = "SELECT id, username, pwd_hash FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserEntry(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserEntry getUserById(Long user_id) {
        String sql = "SELECT id, username, pwd_hash FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, user_id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserEntry(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserEntry createUser(UserData data) {
        String sql = "INSERT INTO users (username, pwd_hash) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, data.username);
            pstmt.setString(2, data.pwd_hash);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong(1);
                UserEntry entry = new UserEntry();
                entry.id = id;
                entry.data = data;
                return entry;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UserEntry extractUserEntry(ResultSet rs) throws SQLException {
        UserEntry entry = new UserEntry();
        entry.id = rs.getLong("id");
        entry.data = new UserData();
        entry.data.username = rs.getString("username");
        entry.data.pwd_hash = rs.getString("pwd_hash");
        return entry;
    }

    // INotebookResultStorage
    private JsonObject parseJson(String raw_json) {
        try {
            return Json.createReader(new StringReader(raw_json)).readObject();
        } catch (Exception e) {
            return Json.createReader(new StringReader("{}")).readObject();
        }
    };

    @Override
    // null == entry doesnt exist
    public JsonObject getAnswerData(String notebook_id, Long user_id) {
        // stmt.execute("CREATE TABLE notebook_user_answers (" +
        // "id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
        // "user_id BIGINT NOT NULL" +
        // "notebook_id VARCHAR(255) NOT NULL" +
        // "answer_data_json BLOB");
        String sql = "SELECT answer_data_json FROM notebook_user_answers WHERE user_id = ? AND notebook_id = ?";
        String raw_json = null;

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, user_id);
            pstmt.setString(2, notebook_id);

            ResultSet result = pstmt.executeQuery();
            boolean has_row = result.next();
            if (!has_row){
                return null;
            }
            raw_json = result.getString(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return raw_json == null ? null : parseJson(raw_json);
    }

    @Override
    public void saveAnswerData(String notebook_id, Long user_id, JsonObject answer_data) {
        saveAnswerData(notebook_id, user_id, answer_data.toString());
    }

    private void saveAnswerData(String notebook_id, Long user_id, String answer_data) {
        String checkSql = "SELECT COUNT(*) FROM notebook_user_answers WHERE user_id = ? AND notebook_id = ?";
        String updateSql = "UPDATE notebook_user_answers SET answer_data_json = ? WHERE user_id = ? AND notebook_id = ?";
        String insertSql = "INSERT INTO notebook_user_answers (user_id, notebook_id, answer_data_json) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setLong(1, user_id);
                checkStmt.setString(2, notebook_id);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                boolean exists = rs.getInt(1) > 0;

                if (exists) {
                    // Update existing entry
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, answer_data);
                        updateStmt.setLong(2, user_id);
                        updateStmt.setString(3, notebook_id);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Create new entry
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setLong(1, user_id);
                        insertStmt.setString(2, notebook_id);
                        insertStmt.setString(3, answer_data);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void deleteEntry(String notebook_id, Long user_id) {
        String deleteSql = "DELETE FROM notebook_user_answers WHERE user_id = ? AND notebook_id = ?";

        if (notebook_id == null || user_id == null) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setLong(1, user_id);
            pstmt.setString(2, notebook_id);

            pstmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
