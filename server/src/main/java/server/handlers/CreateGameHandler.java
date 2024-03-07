package server.handlers;
import Requests.CreateGameRequest;
import Requests.CreateGameResponse;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.ExceptionHandler;
import dataAccess.SQLAuthDAO;
import dataAccess.SQLGameDAO;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {

    private final GameService gameService;
    private final AuthService authService;
    public CreateGameHandler(SQLGameDAO gameDAO, SQLAuthDAO authDAO) {
        gameService = new GameService(gameDAO);
        authService = new AuthService(authDAO);
    }
    public Object createGame(Request req, Response res){
        CreateGameRequest gameName = new Gson().fromJson(req.body(), CreateGameRequest.class);
        String auth = req.headers("Authorization");
        CreateGameResponse createGame;
        try{authService.findAuth(auth);}
        catch(DataAccessException error){
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            res.status(exception.findException());
            return new Gson().toJson(exception);
        }
        try{createGame = gameService.createGame(gameName.gameName());}
        catch (DataAccessException error){
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            res.status(exception.findException());
            return new Gson().toJson(exception);
        }
        res.status(200);
        return new Gson().toJson(createGame);
    }
}
