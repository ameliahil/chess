package service;

import Requests.LoginRequest;
import Requests.LoginResponse;
import dataAccess.DataAccessException;
import dataAccess.SQLUserDAO;
import model.UserData;

public class UserService {
    private final SQLUserDAO userDAO;

    public UserService(SQLUserDAO userDAO){

        this.userDAO = userDAO;
    }

    public void clearUser(){
        userDAO.clear();
    }

    public LoginResponse addUser(UserData user) throws DataAccessException{
        return userDAO.createUser(user.username(),user.password(),user.email());
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException{
        return userDAO.login(loginRequest.username(),loginRequest.password());
    }


}
