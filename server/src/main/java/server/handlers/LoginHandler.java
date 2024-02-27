package server.handlers;
import Requests.LoginRequest;
import Requests.LoginResponse;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final MemoryUserDAO userDAO;
    private final UserService userService;

    public LoginHandler(MemoryUserDAO userDAO) {
        this.userDAO = userDAO;
        userService = new UserService(userDAO);
    }

    public Object login(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResponse loginResponse = userService.login(loginRequest);
        res.status(200);
        return new Gson().toJson(loginResponse);
    }
}
