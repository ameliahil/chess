package server.handlers;
import Requests.JoinRequest;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.ExceptionHandler;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private final GameService gameService;
    private final AuthService authService;
    public JoinGameHandler(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO){
        gameService = new GameService(gameDAO);
        authService = new AuthService(authDAO);
    }
    public Object joinGame(Request req, Response res) throws DataAccessException {
        JoinRequest joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        String auth = req.headers("Authorization");
        try{authService.findAuth(auth);}
        catch(DataAccessException error){
            if(error.getMessage().equals("Error: unauthorized")){
                res.status(401);
                ExceptionHandler exception = new ExceptionHandler(error.getMessage());
                return new Gson().toJson(exception);
            }
        }
        String user = authService.getUser(auth);
        try{gameService.join(joinRequest, user);}
        catch (DataAccessException error){
            if(error.getMessage().equals("Error: bad request")) {
                res.status(400);
                ExceptionHandler exception = new ExceptionHandler(error.getMessage());
                return new Gson().toJson(exception);
            }
            if(error.getMessage().equals("Error: already taken")){
                res.status(403);
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
        return "";
    }
}
