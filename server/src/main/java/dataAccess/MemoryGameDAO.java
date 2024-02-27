package dataAccess;

import Requests.CreateGameResponse;
import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    HashMap<String, GameData> games = new HashMap<>(); //gameName or gameID
    HashSet<GameData> gamesList = new HashSet<>();
    int currID = 0;
    public void clear(){
        games.clear();
    }
    public CreateGameResponse createGame(String whiteUsername, String blackUsername, String gameName){
        int gameID = assignGameID();
        ChessGame implementation = new ChessGame();
        GameData game = new GameData(gameID,whiteUsername,blackUsername,gameName,implementation);
        games.put(gameName, game);
        gamesList.add(game);
        return new CreateGameResponse(gameID);
    }
    public GameData getGame(String gameName){
        return games.get(gameName);
    }
    public HashSet<GameData> listGames(){return gamesList;}
    public void updateGame(){ //just implementation?

    }

    private int assignGameID(){
        updateGameIDList();
        return currID;
    }

    private void updateGameIDList(){
        currID += 1;
    }
}
