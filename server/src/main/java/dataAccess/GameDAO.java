package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    void clear();
    void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame implementation);
    GameData getGame(String gameName);
    HashSet<GameData> listGames();
    void updateGame();
}
