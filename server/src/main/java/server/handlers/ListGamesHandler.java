package server.handlers;
import Requests.ListGamesResponse;
import Requests.ListGamesResponseList;
import com.google.gson.Gson;
import dataAccess.*;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.HashSet;

public class ListGamesHandler {
    private final GameService gameService;
    private final AuthService authService;
    public ListGamesHandler(SQLGameDAO gameDAO, SQLAuthDAO authDAO){
        gameService = new GameService(gameDAO);
        authService = new AuthService(authDAO);}

    public Object listGames(Request req, Response res){
        String auth = req.headers("Authorization");
        try{
            DatabaseManager.createDatabase();
            authService.findAuth(auth);
        }
        catch(DataAccessException error){
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            res.status(exception.findException());
            return new Gson().toJson(exception);
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
