package dataAccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{
    DatabaseManager manager = new DatabaseManager();

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE authTokens";
        manager.executeUpdate(statement);
    }


    /*public String createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        addAuth(token,username);
        return token;
    }*/

    public void addAuth(String authToken, String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken from authTokens WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        var updateStatement = "INSERT INTO authTokens (authToken, username, json) VALUES (?, ?, ?)";
                        try (var updatePs = conn.prepareStatement(updateStatement)) {
                            AuthData authData = new AuthData(authToken, username);
                            var json = new Gson().toJson(authData);
                            updatePs.setString(1, authToken);
                            updatePs.setString(2, username);
                            updatePs.setString(3, json);
                            updatePs.executeUpdate();
                        }
                    } else {
                        throw new DataAccessException("Error: already taken");
                    }
                }
            }
        }
        catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public void logout(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM authTokens WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Error: unauthorized");
                }
            }
        }catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public String getUser(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username from authTokens WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Error: unauthorized");
                    }
                    return rs.getString("username");
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    public void findAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken from authTokens WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Error: unauthorized");
                    }
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    }
}
