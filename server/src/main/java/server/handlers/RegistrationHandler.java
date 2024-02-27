package server.handlers;
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
        var user = new Gson().fromJson(req.body(), UserData.class);
        return Gson.toJson(userService.addUser(user));
    }
}
