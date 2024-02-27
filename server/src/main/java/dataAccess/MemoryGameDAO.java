package dataAccess;

import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    HashSet<GameData> games = new HashSet<>();
    public void clear(){
        games.clear();
    }
    public void createGame(){}
    public GameData getGame(){return null;}
    public HashSet<GameData> listGames(){return null;}
    public void updateGame(){}
}
