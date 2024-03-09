package server.handlers;
import Requests.JoinRequest;
import com.google.gson.Gson;
import dataAccess.*;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private final GameService gameService;
    private final AuthService authService;
    public JoinGameHandler(SQLGameDAO gameDAO, SQLAuthDAO authDAO){
        gameService = new GameService(gameDAO);
        authService = new AuthService(authDAO);
    }
    public Object joinGame(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        JoinRequest joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        try{
            DatabaseManager.createDatabase();
            authService.findAuth(auth);}
        catch(DataAccessException error){
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            res.status(exception.findException());
            return new Gson().toJson(exception);
        }
        String user = authService.getUser(auth);
        try{gameService.join(joinRequest, user);}
        catch (DataAccessException error){
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            res.status(exception.findException());
            return new Gson().toJson(exception);
        }
        res.status(200);
        return "";
    }
}
