package dataAccess;

import Requests.CreateGameResponse;
import Requests.ListGamesResponse;

import java.util.Collection;

public interface GameDAO {
    void clear() throws DataAccessException;
    CreateGameResponse createGame(String whiteUsername, String blackUsername, String gameName) throws DataAccessException;
    Collection<ListGamesResponse> listGames() throws DataAccessException;
}
