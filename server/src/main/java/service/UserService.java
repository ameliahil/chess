package service;

import Requests.LoginRequest;
import Requests.LoginResponse;
import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO){

        this.userDAO = userDAO;
    }

    public void clearUser() throws DataAccessException{
        userDAO.clear();
    }

    public LoginResponse addUser(UserData user) throws DataAccessException{
        return userDAO.createUser(user.username(),user.password(),user.email());
    }

    public LoginResponse login(LoginRequest loginRequest){
        return userDAO.login(loginRequest.username(),loginRequest.password());
    }
}
