package server.handlers;
import Requests.CreateGameRequest;
import Requests.CreateGameResponse;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.ExceptionHandler;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final MemoryGameDAO gameDAO;

    private final GameService gameService;
    private final AuthService authService;
    public CreateGameHandler(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        gameService = new GameService(gameDAO);
        authService = new AuthService(authDAO);
    }
    public Object createGame(Request req, Response res){
        CreateGameRequest gameName = new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameResponse createGame;
        String auth = req.headers("Authorization");
        try{authService.findAuth(auth);}
        catch(DataAccessException error){
            if(error.getMessage().equals("Error: unauthorized")){
                res.status(401);
                ExceptionHandler exception = new ExceptionHandler(error.getMessage());
                return new Gson().toJson(exception);
            }
        }
        try{createGame = gameService.createGame(gameName.gameName());}
        catch (DataAccessException error){
            if(error.getMessage().equals("Error: bad request")) {
                res.status(400);
                ExceptionHandler exception = new ExceptionHandler(error.getMessage());
                return new Gson().toJson(exception);
            }
            else{
                res.status(500);
                ExceptionHandler exception = new ExceptionHandler(error.getMessage());
                return new Gson().toJson(exception);
            }
        }
        res.status(200);
        return new Gson().toJson(createGame);
    }
}
