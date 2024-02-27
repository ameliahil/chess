package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryGameDAO;

public class GameService {
    private final MemoryGameDAO gameDAO;

    public GameService(MemoryGameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public void clearGame() throws DataAccessException{
        gameDAO.clear();
    }
}
