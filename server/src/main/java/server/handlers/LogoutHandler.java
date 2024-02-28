package server.handlers;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import service.AuthService;
import spark.Request;
import spark.Response;
import model.AuthData;

public class LogoutHandler {
    private final MemoryAuthDAO authDAO;
    private final AuthService authService;
    public LogoutHandler(MemoryAuthDAO authDAO) {
        this.authDAO = authDAO;
        authService = new AuthService(authDAO);
    }
    public Object logout(Request req, Response res) throws DataAccessException {
        String auth = req.headers("Authorization");
        authService.logout(auth);
        res.status(200);
        return "";
    }
}
