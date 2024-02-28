package server.handlers;
import Requests.LoginRequest;
import Requests.LoginResponse;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.ExceptionHandler;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final MemoryUserDAO userDAO;
    private final UserService userService;
    private final AuthService authService;

    public LoginHandler(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        userService = new UserService(userDAO);
        authService = new AuthService(authDAO);
    }

    public Object login(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResponse loginResponse;
        try{loginResponse = userService.login(loginRequest);}
        catch (DataAccessException error){
            if(error.getMessage().equals("Error: unauthorized")){
                res.status(401);
                ExceptionHandler exception = new ExceptionHandler(error.getMessage());
                return new Gson().toJson(exception);
            }
            else{
                res.status(500);
                ExceptionHandler exception = new ExceptionHandler(error.getMessage());
                return new Gson().toJson(exception);
            }
        }
        authService.insertAuth(loginResponse.username(),loginResponse.authToken());
        res.status(200);
        return new Gson().toJson(loginResponse);
    }
}
