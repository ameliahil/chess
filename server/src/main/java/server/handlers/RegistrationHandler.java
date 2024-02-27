package server.handlers;
import Requests.LoginResponse;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegistrationHandler {
    private final MemoryUserDAO userDAO;
    private final UserService userService;

    public RegistrationHandler(MemoryUserDAO userDAO) {
        this.userDAO = userDAO;
        userService = new UserService(userDAO);
    }

    public Object register(Request req, Response res) throws DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        LoginResponse login = userService.addUser(user);
        res.status(200);
        return new Gson().toJson(login);
    }
}
