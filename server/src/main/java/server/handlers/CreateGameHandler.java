package server.handlers;
import Requests.CreateGameRequest;
import Requests.CreateGameResponse;
import com.google.gson.Gson;
import dataAccess.MemoryGameDAO;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private final MemoryGameDAO gameDAO;

    private final GameService gameService;
    public CreateGameHandler(MemoryGameDAO gameDAO) {
        this.gameDAO = gameDAO;
        gameService = new GameService(gameDAO);
    }
    public Object createGame(Request req, Response res){
        CreateGameRequest gameName = new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameResponse createGame = gameService.createGame(gameName.gameName());
        res.status(200);
        return new Gson().toJson(createGame);
    }
}
