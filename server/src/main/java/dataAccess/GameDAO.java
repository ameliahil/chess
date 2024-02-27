package dataAccess;

import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    void clear();
    void createGame();
    GameData getGame();
    HashSet<GameData> listGames();
    void updateGame();
}
