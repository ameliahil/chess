package dataAccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    HashMap<String,UserData> users = new HashMap<>();
    public void clear(){
        users.clear();
    }
    public void createUser(String username, String password, String email){
        UserData newUser = new UserData(username,password,email);
        users.put(username,newUser);
    }
    public UserData getUser(String username){
        return users.get(username);
    }
}
