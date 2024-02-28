package dataAccess;

import Requests.LoginResponse;
import model.UserData;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MemoryUserDAO implements UserDAO{
    HashMap<String,UserData> users = new HashMap<>();
    public void clear(){
        users.clear();
    }
    public LoginResponse createUser(String username, String password, String email) throws DataAccessException{
        if(users.get(username) != null){
            throw new DataAccessException("Error: already taken");
        }
        if(username == null || password == null || email == null){
            throw new DataAccessException("Error: bad request");
        }
        UserData newUser = new UserData(username,password,email);
        users.put(username,newUser);
        return new LoginResponse(username, createAuth());
    }
    public LoginResponse login(String username, String password) throws DataAccessException {
        if(users.get(username) == null || !Objects.equals(users.get(username).password(), password)){
            throw new DataAccessException("Error: unauthorized");
        }
        return new LoginResponse(username, createAuth());
    }

    public UserData getUser(String username){
        return users.get(username);
    }

    public String createAuth() {
        return UUID.randomUUID().toString();
    }
}
