package dataAccess;

import Requests.CreateGameResponse;
import Requests.ListGamesResponse;
import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class SQLGameDAO implements GameDAO {
    public HashMap<Integer, GameData> games = new HashMap<>(); //gameName or gameID
    HashMap<String, GameData> gameNames = new HashMap<>();
    HashSet<Integer> gameIDList = new HashSet<>();
    int currID = -1;
    public void clear(){
        games.clear();
        gameIDList.clear();
        gameNames.clear();
    }
    public CreateGameResponse createGame(String whiteUsername, String blackUsername, String gameName) throws DataAccessException {
        if(gameNames.get(gameName) != null){
            throw new DataAccessException("Error:");
        }
        int gameID = assignGameID();
        ChessGame implementation = new ChessGame();
        GameData game = new GameData(gameID,whiteUsername,blackUsername,gameName,implementation);
        games.put(gameID, game);
        gameNames.put(gameName,game);
        gameIDList.add(gameID);
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
