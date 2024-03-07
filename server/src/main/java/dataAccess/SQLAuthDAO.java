package dataAccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLAuthDAO implements AuthDAO{
    public HashMap<String, AuthData> authTokens = new HashMap<>();
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE pet";
        executeUpdate(statement);;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    public String createAuthTableString(String authToken, String username){
        return """
                CREATE TABLE authTokens(
                authToken varchar2(256) NOT NULL PRIMARY KEY,
                username varchar2(256) NOT NULL,
                """;
    }
    public String createAuth(String username){
        String token = UUID.randomUUID().toString();
        AuthData authToken = new AuthData(token, username);
        authTokens.put(token,authToken);
        return token;
    }

    public void logout(String authToken) throws DataAccessException {
        if(authTokens.get(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        authTokens.remove(authToken);
    }

    public void insertAuth(String username, String authToken){
        AuthData authData = new AuthData(authToken,username);
        authTokens.put(authToken, authData);
    }

    public String getUser(String authToken){
        return authTokens.get(authToken).username();
    }

    public void findAuth(String authToken) throws DataAccessException {
        if (authTokens.get(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: %s");
        }
    }
}
