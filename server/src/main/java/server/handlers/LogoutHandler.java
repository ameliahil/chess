package server.handlers;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.ExceptionHandler;
import dataAccess.MemoryAuthDAO;
import service.AuthService;
import spark.Request;
import spark.Response;
import model.AuthData;

public class LogoutHandler {
    private final AuthService authService;
    public LogoutHandler(MemoryAuthDAO authDAO) {
        authService = new AuthService(authDAO);
    }
    public Object logout(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        try{authService.logout(auth);}
        catch (DataAccessException error){
            ExceptionHandler exception = new ExceptionHandler(error.getMessage());
            res.status(exception.findException());
            return new Gson().toJson(exception);
        }
        res.status(200);
        return "";
    }
}
