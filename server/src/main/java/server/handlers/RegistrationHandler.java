package server.handlers;
import Requests.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import dataAccess.ExceptionHandler;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegistrationHandler {
    //private final MemoryUserDAO userDAO;
    private final UserService userService;
    private final AuthService authService;

    public RegistrationHandler(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        //this.userDAO = userDAO;
        userService = new UserService(userDAO);
        authService = new AuthService(authDAO);
    }

    public Object register(Request req, Response res) throws DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        LoginResponse login;
        try {
            login = userService.addUser(user);
        }
        catch (DataAccessException error){
            if(error.getMessage().equals("Error: bad request")){
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
                return new Gson().toJson(error);
            }
        }
        authService.insertAuth(login.username(),login.authToken());
        res.status(200);
        return new Gson().toJson(login);
    }
}
