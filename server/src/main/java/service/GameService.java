package service;

import Requests.CreateGameResponse;
import Requests.JoinRequest;
import Requests.ListGamesResponse;
import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.SQLGameDAO;
import java.util.Collection;

public class GameService {
    private final SQLGameDAO gameDAO;

    public GameService(SQLGameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public void clearGame() throws DataAccessException {
        gameDAO.clear();
    }

    public void join(JoinRequest joinRequest, String username)throws DataAccessException{
        if(joinRequest.playerColor() == ChessGame.TeamColor.WHITE){
            gameDAO.updateWhiteUsername(joinRequest.gameID(),username);
        }
        else if(joinRequest.playerColor() == ChessGame.TeamColor.BLACK){
            gameDAO.updateBlackUsername(joinRequest.gameID(),username);
        }
        else{
            gameDAO.watch(joinRequest.gameID());
        }
    }

    public CreateGameResponse createGame(String gameName) throws DataAccessException{
        return gameDAO.createGame(null,null,gameName);
    }

    public Collection<ListGamesResponse> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }
}
