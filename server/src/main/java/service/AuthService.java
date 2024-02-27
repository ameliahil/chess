package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;

public class AuthService {
    private final MemoryAuthDAO authDAO;

    public AuthService(MemoryAuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public void clearAuth() throws DataAccessException{
        authDAO.clear();
    }
}
