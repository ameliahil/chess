package service;

import Requests.CreateGameResponse;
import Requests.JoinRequest;
import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MemoryGameDAO;
import model.GameData;

public class GameService {
    private final MemoryGameDAO gameDAO;

    public GameService(MemoryGameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public void clearGame() throws DataAccessException{
        gameDAO.clear();
    }

    public void join(JoinRequest joinRequest, String username){
        if(joinRequest.playerColor() == ChessGame.TeamColor.WHITE){
            gameDAO.updateWhiteUsername(joinRequest.gameID(),username);
        }
        else{
            gameDAO.updateBlackUsername(joinRequest.gameID(),username);
        }
    }

    public CreateGameResponse createGame(String gameName) throws DataAccessException{
        return gameDAO.createGame(null,null,gameName);
    }
}
