package dataAccess;

import Requests.CreateGameResponse;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear();
    CreateGameResponse createGame(String whiteUsername, String blackUsername, String gameName);
    GameData getGame(int gameID);
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame();
}
