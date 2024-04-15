package dataAccess;

import Requests.LoginResponse;
import com.google.gson.Gson;
import model.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLUserDAO implements UserDAO{
    DatabaseManager manager = new DatabaseManager();
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        manager.executeUpdate(statement);
    }
    public LoginResponse createUser(String username, String password, String email) throws DataAccessException{
        if(username == null || password == null || email == null){
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username from users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        var updateStatement = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?)";
                        try (var updatePs = conn.prepareStatement(updateStatement)) {
                            updatePs.setString(1, username);
                            updatePs.setString(2,password);
                            updatePs.setString(3,email);
                            UserData userData = new UserData(username,password,email);
                            var json = new Gson().toJson(userData);
                            updatePs.setString(4,json);
                            updatePs.executeUpdate();
                        }
                    } else {
                        throw new DataAccessException("Error: already taken");
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return new LoginResponse(username, createAuth());
    }
    public LoginResponse login(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password from users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if(rs.next()){
                        String realPassword = rs.getString("password");
                        if(password.equals(realPassword)){
                            return new LoginResponse(username, createAuth());
                        }
                        else{
                            throw new DataAccessException("Error: unauthorized");
                        }
                    }
                    else{
                        throw new DataAccessException("Error: unauthorized");
                    }
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json from users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String value = rs.getString("json");
                        return new Gson().fromJson(value, UserData.class);
                    }
                    else{
                        throw new DataAccessException("No user found");
                    }
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    private String createAuth() {
        return UUID.randomUUID().toString();
    }
}
