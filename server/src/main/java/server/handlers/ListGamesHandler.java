package server.handlers;
import Requests.ListGamesResponse;
import Requests.ListGamesResponseList;
import Requests.LoginRequest;
import Requests.LoginResponse;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.ExceptionHandler;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.HashSet;

public class ListGamesHandler {
    private final GameService gameService;
    private final AuthService authService;
    public ListGamesHandler(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO){
        gameService = new GameService(gameDAO);
        authService = new AuthService(authDAO);}

    public Object listGames(Request req, Response res){
        String auth = req.headers("Authorization");
        try{authService.findAuth(auth);}
        catch(DataAccessException error){
            if(error.getMessage().equals("Error: unauthorized")){
                res.status(401);
                ExceptionHandler exception = new ExceptionHandler(error.getMessage());
                return new Gson().toJson(exception);
            }
        }
        HashSet<ListGamesResponse> games;
        try{
            games = new HashSet<>(gameService.listGames());
        }
        catch (DataAccessException error){
            res.status(500);
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            return new Gson().toJson(exception);
        }
        ListGamesResponseList gamesList = new ListGamesResponseList(games);
        res.status(200);
        return new Gson().toJson(gamesList);
    }
}
