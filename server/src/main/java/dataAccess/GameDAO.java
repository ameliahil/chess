package dataAccess;

import Requests.CreateGameResponse;
import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    void clear();
    CreateGameResponse createGame(String whiteUsername, String blackUsername, String gameName);
    GameData getGame(int gameID);
    HashSet<GameData> listGames();
    void updateGame();
}
