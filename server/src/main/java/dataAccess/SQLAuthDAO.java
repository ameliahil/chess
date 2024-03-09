package dataAccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLAuthDAO implements AuthDAO{
    DatabaseManager manager = new DatabaseManager();

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE authTokens";
        manager.executeUpdate(statement);
    }


    public String createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData authToken = new AuthData(token, username);
        addAuth(authToken);
        return token;
    }

    private void addAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO authTokens (authToken, username, json) VALUES (?, ?, ?)";
        var json = new Gson().toJson(authData);
        manager.executeUpdate(statement, authData.authToken(), authData.username(), json);
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

    public String getUser(String authToken){
        //return authTokens.get(authToken).username();
        return null;
    }

}
