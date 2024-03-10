package dataAccess;

import Requests.CreateGameResponse;
import Requests.ListGamesResponse;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class SQLGameDAO implements GameDAO {
    DatabaseManager manager = new DatabaseManager();
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";
        manager.executeUpdate(statement);
    }
    public CreateGameResponse createGame(String whiteUsername, String blackUsername, String gameName) throws DataAccessException {
        int gameID = findCurrID();
        ChessGame implementation = new ChessGame();
        String jsonGame = new Gson().toJson(implementation);
        GameData gameData = new GameData(gameID,whiteUsername,blackUsername,gameName,implementation);
        var json = new Gson().toJson(gameData);
        try(var conn = DatabaseManager.getConnection()){
            var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, implementation, json) VALUES (?, ?, ?, ?, ?, ?)";
            try(var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                ps.setInt(1, gameID);
                ps.setString(2, whiteUsername);
                ps.setString(3, blackUsername);
                ps.setString(4, gameName);
                ps.setObject(5, jsonGame);
                ps.setObject(6, json);
                ps.executeUpdate();
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        return new CreateGameResponse(gameID);
    }
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json from games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Error: unauthorized");
                    }
                    var json = rs.getString("json");
                    return new Gson().fromJson(json, GameData.class);
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    public Collection<ListGamesResponse> listGames() throws DataAccessException {
        HashSet<ListGamesResponse> gameList = new HashSet<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json from games";
            try (var ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String value = rs.getString("json");
                        var newJson = new Gson().fromJson(value, ListGamesResponse.class);
                        gameList.add(new ListGamesResponse(newJson.gameID(), newJson.whiteUsername(),newJson.blackUsername(), newJson.gameName()));
                    }
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        return gameList;
    }

    public void updateBlackUsername(int gameID, String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT blackUsername from games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String data = rs.getString("blackUsername");
                        if(data == null){
                            var updateStatement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
                            try (var updatePs = conn.prepareStatement(updateStatement)) {
                                updatePs.setString(1, username);
                                updatePs.setInt(2,gameID);
                                updatePs.executeUpdate();
                            }
                        }
                        else{
                            throw new DataAccessException("Error: already taken");
                        }
                    }
                }
            }
            var jsonStatement = "SELECT json from games WHERE gameID=?";
            try (var ps = conn.prepareStatement(jsonStatement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String data = rs.getString("json");
                        GameData gameData = new Gson().fromJson(data,GameData.class);
                        GameData newGameData = new GameData(gameData.gameID(),gameData.whiteUsername(),username,gameData.gameName(),gameData.implementation());
                        String newJson = new Gson().toJson(newGameData);
                        if(data == null){
                            var updateStatement = "UPDATE games SET json = ? WHERE gameID = ?";
                            try (var updatePs = conn.prepareStatement(updateStatement)) {
                                updatePs.setString(1, newJson);
                                updatePs.setInt(2,gameID);
                                updatePs.executeUpdate();
                            }
                        }
                        else{
                            throw new DataAccessException("Error: already taken");
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void updateWhiteUsername(int gameID, String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername from games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String data = rs.getString("whiteUsername");
                        if(data == null){
                            var updateStatement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
                            try (var updatePs = conn.prepareStatement(updateStatement)) {
                                updatePs.setString(1, username);
                                updatePs.setInt(2, gameID);
                                updatePs.executeUpdate();
                            }
                        }
                        else{
                            throw new DataAccessException("Error: already taken");
                        }
                    }
                }
            }
            var jsonStatement = "SELECT json from games WHERE gameID=?";
            try (var ps = conn.prepareStatement(jsonStatement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String data = rs.getString("json");
                        GameData gameData = new Gson().fromJson(data,GameData.class);
                        GameData newGameData = new GameData(gameData.gameID(),username,gameData.blackUsername(),gameData.gameName(),gameData.implementation());
                        String newJson = new Gson().toJson(newGameData);
                        var updateStatement = "UPDATE games SET json = ? WHERE gameID = ?";
                        try (var updatePs = conn.prepareStatement(updateStatement)) {
                            updatePs.setInt(2,gameID);
                            updatePs.setString(1, newJson);
                            updatePs.executeUpdate();
                        }
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    public void watch(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID from games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Error: bad request");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: bad request");
        }
    }

    private int findCurrID() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT currID from autoIncrement WHERE lineNum=1";
            try (var selectPs = conn.prepareStatement(statement)) {
                try (var rs = selectPs.executeQuery()) {
                    if (!rs.next()) {
                        var insertStatement = "INSERT INTO autoIncrement (currID, lineNum) VALUES(1, 1)";
                        try (var insertPs = conn.prepareStatement(insertStatement)){
                            insertPs.executeUpdate();
                            return 1;
                        }
                    }
                    else{
                        int currID = rs.getInt("currID");
                        var updateStatement = "UPDATE autoIncrement SET currID = currID + 1";
                        try(var updatePs = conn.prepareStatement(updateStatement)){
                            updatePs.executeUpdate();
                            return currID;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: bad request");
        }
    }
}
