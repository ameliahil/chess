package service;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO){

        this.userDAO = userDAO;
    }

    public void clearUser() throws DataAccessException{
        userDAO.clear();
    }
}
