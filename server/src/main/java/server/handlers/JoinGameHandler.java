package server.handlers;
import Requests.JoinRequest;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
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
        String user = authService.getUser(auth);
        gameService.join(joinRequest, user);
        res.status(200);
        return "";
    }
}
