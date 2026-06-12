package com.kor.tomcat.service.user_session_service;
   
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DerbyUserSessionStorage implements IUserSessionStorage {
    private static final String DB_URL = "jdbc:derby:userdb;create=true";
    //private final ConcurrentHashMap<String, String> sessionMap = new ConcurrentHashMap<>(); // sessionId -> userId
    
    public DerbyUserSessionStorage() {
        System.setProperty("derby.stream.error.file", "/dev/null");
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()) {
            
            // Create users table
            stmt.execute("CREATE TABLE users (" +
                        "id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                        "username VARCHAR(255) UNIQUE NOT NULL, " +
                        "pwd_hash VARCHAR(255) NOT NULL)");
            
        } catch (SQLException e) {
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
    
    // @Override
    // public String createOrGetSession(Long user_id) {
    //     // Check if session already exists for this user
    //     for (java.util.Map.Entry<String, String> entry : sessionMap.entrySet()) {
    //         if (entry.getValue().equals(String.valueOf(user_id))) {
    //             return entry.getKey();
    //         }
    //     }
        
    //     // Create new session
    //     String sessionId = UUID.randomUUID().toString();
    //     sessionMap.put(sessionId, String.valueOf(user_id));
    //     return sessionId;
    // }
    
    // @Override
    // public UserData getSessionUserData(String session_id) {
    //     String userId = sessionMap.get(session_id);
    //     if (userId == null) {
    //         return null;
    //     }
        
    //     UserEntry entry = getUserById(Long.parseLong(userId));
    //     return entry != null ? entry.data : null;
    // }
    
    private UserEntry extractUserEntry(ResultSet rs) throws SQLException {
         UserEntry entry = new UserEntry();
         entry.id = rs.getLong("id");
         entry.data = new UserData();
         entry.data.username = rs.getString("username");
         entry.data.pwd_hash = rs.getString("pwd_hash");
         return entry;
     }
} 
