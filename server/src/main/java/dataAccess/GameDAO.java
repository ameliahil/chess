package dataAccess;

import Requests.CreateGameResponse;
import Requests.ListGamesResponse;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear();
    CreateGameResponse createGame(String whiteUsername, String blackUsername, String gameName) throws DataAccessException;
    GameData getGame(int gameID);
    Collection<ListGamesResponse> listGames() throws DataAccessException;
}
