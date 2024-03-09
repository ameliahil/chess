package server.handlers;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.ExceptionHandler;
import dataAccess.SQLAuthDAO;
import service.AuthService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final AuthService authService;
    public LogoutHandler(SQLAuthDAO authDAO) {
        authService = new AuthService(authDAO);
    }
    public Object logout(Request req, Response res) {
        String auth = req.headers("Authorization");
        try{
            authService.logout(auth);
            DatabaseManager.createDatabase();
        }
        catch (DataAccessException error){
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            res.status(exception.findException());
            return new Gson().toJson(exception);
        }
        res.status(200);
        return "";
    }
}
