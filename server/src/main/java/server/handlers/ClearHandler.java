package server.handlers;

import com.google.gson.Gson;
import dataAccess.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class ClearHandler {

    private final SQLUserDAO userDAO;
    private final SQLGameDAO gameDAO;
    private final SQLAuthDAO authDAO;

    private final UserService userService;
    private final GameService gameService;
    private final AuthService authService;

    public ClearHandler(SQLUserDAO userDAO, SQLGameDAO gameDAO, SQLAuthDAO authDAO){

        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;

        userService = new UserService(userDAO);
        gameService = new GameService(gameDAO);
        authService = new AuthService(authDAO);
    }

    public Object clear(Request req, Response res) throws DataAccessException {
        try{
            DatabaseManager.createDatabase();
        }
        catch(DataAccessException error){
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            res.status(exception.findException());
            return new Gson().toJson(exception);
        }
        authService.clearAuth();
        gameService.clearGame();
        userService.clearUser();
        res.status(200);
        return "";
    }

}
