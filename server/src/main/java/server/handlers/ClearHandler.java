package server.handlers;

import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class ClearHandler {

    private final MemoryUserDAO userDAO;
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;

    private final UserService userService;
    private final GameService gameService;
    private final AuthService authService;

    public ClearHandler(MemoryUserDAO userDAO, MemoryGameDAO gameDAO, MemoryAuthDAO authDAO){

        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;

        userService = new UserService(userDAO);
        gameService = new GameService(gameDAO);
        authService = new AuthService(authDAO);
    }

    public Object clear(Request req, Response res) throws DataAccessException {
        authService.clearAuth();
        gameService.clearGame();
        userService.clearUser();
        res.status(200);
        return "";
    }

}
