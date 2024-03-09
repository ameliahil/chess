package dataAccess;

import java.sql.*;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        try {
            configureAuthDatabase();
            configureGameDatabase();
            configureUserDatabase();
        }
        catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
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

    private static void configureAuthDatabase() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = createAuthTableString();
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: %s");
        }
    }

    public static String createAuthTableString(){
        return """
                CREATE TABLE IF NOT EXISTS authTokens(
                authToken varchar2(256) NOT NULL PRIMARY KEY,
                username varchar2(256) NOT NULL,
                json TEXT DEFAULT NULL
                )
                """;
    }

    private static void configureUserDatabase() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = createUserTableString();
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: %s");
        }
    }

    public static String createUserTableString(){
        return """
                CREATE TABLE IF NOT EXISTS users(
                username varchar2(256) NOT NULL PRIMARY KEY,
                password varchar2(256) NOT NULL,
                email varchar2(256) NOT NULL,
                json TEXT DEFAULT NULL
                )
                """;
    }

    private static void configureGameDatabase() throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = createGameTableString();
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: %s");
        }
    }

    public static String createGameTableString(){
        return """
                CREATE TABLE IF NOT EXISTS authTokens(
                gameID int NOT NULL PRIMARY KEY
                whiteUsername varchar2(256) DEFAULT NULL,
                blackUsername varchar2(256) DEFAULT NULL,
                gameName varchar2(256) NOT NULL,
                implementation TEXT DEFAULT NULL,
                json TEXT DEFAULT NULL
                )
                """;
    }

}
