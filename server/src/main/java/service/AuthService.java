package service;

import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import org.junit.jupiter.api.function.Executable;

public class AuthService {
    private final SQLAuthDAO authDAO;

    public AuthService(SQLAuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void clearAuth() throws DataAccessException {
        authDAO.clear();
    }
    public Executable logout(String authToken) throws DataAccessException{
        authDAO.logout(authToken);
        return null;
    }
    public String getUser(String authToken) throws DataAccessException {
        return authDAO.getUser(authToken);
    }

    public void insertAuth(String username, String authToken) throws DataAccessException {
        authDAO.addAuth(authToken,username);
    }

    public void findAuth(String auth) throws DataAccessException {
        authDAO.findAuth(auth);
    }
}
