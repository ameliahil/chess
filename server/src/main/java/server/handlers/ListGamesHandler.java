package server.handlers;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    public ListGamesHandler(MemoryGameDAO gameDAO){}

    public Object listGames(Request req, Response res){
        return null;
    }
}
