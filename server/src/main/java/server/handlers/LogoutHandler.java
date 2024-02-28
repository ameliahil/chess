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
        int size = authDAO.checkMap();
    }
    public Object logout(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        try{authService.logout(auth);}
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
        res.status(200);
        return "";
    }
}
