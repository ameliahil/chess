package server.handlers;
import Requests.LoginRequest;
import Requests.LoginResponse;
import com.google.gson.Gson;
import dataAccess.*;
import service.AuthService;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private final UserService userService;
    private final AuthService authService;

    public LoginHandler(SQLUserDAO userDAO, SQLAuthDAO authDAO) {
        userService = new UserService(userDAO);
        authService = new AuthService(authDAO);
    }

    public Object login(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResponse loginResponse;
        try{
            DatabaseManager.createDatabase();
            loginResponse = userService.login(loginRequest);
        }
        catch (DataAccessException error){
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            res.status(exception.findException());
            return new Gson().toJson(exception);
        }
        authService.insertAuth(loginResponse.username(),loginResponse.authToken());
        res.status(200);
        return new Gson().toJson(loginResponse);
    }
}
