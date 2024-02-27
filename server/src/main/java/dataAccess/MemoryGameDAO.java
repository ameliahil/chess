package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    HashMap<String, GameData> games = new HashMap<>(); //gameName or gameID
    HashSet<GameData> gamesList = new HashSet<>();
    public void clear(){
        games.clear();
    }
    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame implementation){
        GameData game = new GameData(gameID,whiteUsername,blackUsername,gameName,implementation);
        games.put(gameName, game);
        gamesList.add(game);
    }
    public GameData getGame(String gameName){
        return games.get(gameName);
    }
    public HashSet<GameData> listGames(){return gamesList;}
    public void updateGame(){ //just implementation?

    }
}
