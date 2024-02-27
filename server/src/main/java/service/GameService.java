package service;

import Requests.CreateGameResponse;
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

    public CreateGameResponse createGame(String gameName){
        return gameDAO.createGame(null,null,gameName);
    }
}
