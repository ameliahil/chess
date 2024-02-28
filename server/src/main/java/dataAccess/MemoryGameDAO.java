package dataAccess;

import Requests.CreateGameResponse;
import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    HashMap<Integer, GameData> games = new HashMap<>(); //gameName or gameID
    HashSet<Integer> gameIDList = new HashSet<>();
    int currID = -1;
    public void clear(){
        games.clear();
    }
    public CreateGameResponse createGame(String whiteUsername, String blackUsername, String gameName){
        int gameID = assignGameID();
        ChessGame implementation = new ChessGame();
        GameData game = new GameData(gameID,whiteUsername,blackUsername,gameName,implementation);
        games.put(gameID, game);
        gameIDList.add(gameID);
        return new CreateGameResponse(gameID);
    }
    public GameData getGame(int gameID){
        return games.get(gameID);
    }
    public Collection<GameData> listGames() throws DataAccessException{
        HashSet<GameData> gameList = new HashSet<>();
        for(int gameID: gameIDList){
            gameList.add(games.get(gameID));
        }
        return gameList;
    }
    public void updateGame(){ //just implementation?

    }
    public void updateBlackUsername(int gameID, String username){
        GameData game = getGame(gameID);
        GameData newGame = new GameData(gameID,game.whiteUsername(),username,game.gameName(),game.implementation());
        games.remove(gameID);
        games.put(gameID,newGame);
    }

    public void updateWhiteUsername(int gameID, String username){
        GameData game = getGame(gameID);
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
}
