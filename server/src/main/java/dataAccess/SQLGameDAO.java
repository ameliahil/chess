package dataAccess;

import Requests.CreateGameResponse;
import Requests.ListGamesResponse;
import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
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
        int gameID;
        try(var conn = DatabaseManager.getConnection()){
            var statement = "INSERT INTO games (id, whiteUsername, blackUsername, gameName) VALUES (?, ?, ?, ?)";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, whiteUsername);
                ps.setString(2, blackUsername);
                ps.setString(3, gameName);
                ps.executeUpdate();

                try(var rs = ps.getGeneratedKeys()){
                    if(rs.next()){
                        gameID = rs.getInt(1);
                    }
                    else {
                        throw new DataAccessException("Error: could not retrieve generated gameID");
                    }
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        ChessGame implementation = new ChessGame();
        GameData gameData = new GameData(gameID,whiteUsername,blackUsername,gameName,implementation);
        json =
        var gameID = executeUpdate(statement, whiteUsername, blackUsername, json);
        return new CreateGameResponse(gameID);
    }
    public GameData getGame(int gameID){
        return games.get(gameID);
    }
    public Collection<ListGamesResponse> listGames() {
        HashSet<ListGamesResponse> gameList = new HashSet<>();
        for(int gameID: gameIDList){
            GameData currGame = games.get(gameID);
            gameList.add(new ListGamesResponse(currGame.gameID(),currGame.whiteUsername(),currGame.blackUsername(),currGame.gameName()));
        }
        return gameList;
    }

    public void updateBlackUsername(int gameID, String username) throws DataAccessException{
        GameData game = getGame(gameID);
        if(game.blackUsername() != null){
            throw new DataAccessException("Error: already taken");
        }
        if(games.get(gameID) == null){
            throw new DataAccessException("Error: bad request");
        }
        GameData newGame = new GameData(gameID,game.whiteUsername(),username,game.gameName(),game.implementation());
        games.remove(gameID);
        games.put(gameID,newGame);
    }

    public void updateWhiteUsername(int gameID, String username) throws DataAccessException{
        GameData game = getGame(gameID);
        if(game == null){
            throw new DataAccessException(("Error: bad request"));
        }
        if(game.whiteUsername() != null){
            throw new DataAccessException("Error: already taken");
        }
        if(games.get(gameID) == null){
            throw new DataAccessException("Error: bad request");
        }
        GameData newGame = new GameData(gameID,username,game.blackUsername(),game.gameName(),game.implementation());
        games.remove(gameID);
        games.put(gameID,newGame);
    }

    private int assignGameID(){
        updateGameIDList();
        return currID;
    }

    private void updateGameIDList(){
        currID += 1;
    }

    public void watch(int gameID) throws DataAccessException{
        if(games.get(gameID) == null){
            throw new DataAccessException("Error: bad request");
        }
    }
}
