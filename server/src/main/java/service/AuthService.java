package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;

import javax.xml.crypto.Data;

public class AuthService {
    private final MemoryAuthDAO authDAO;

    public AuthService(MemoryAuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void clearAuth() throws DataAccessException{
        authDAO.clear();
    }
    public void logout(String authToken) throws DataAccessException{
        authDAO.logout(authToken);
    }
    public String getUser(String authToken) throws DataAccessException{
        return authDAO.getUser(authToken);
    }

    public void insertAuth(String username, String authToken){
        authDAO.insertAuth(username,authToken);
    }

    public void findAuth(String auth) throws DataAccessException {
        authDAO.findAuth(auth);
    }
}
