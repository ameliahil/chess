package dataAccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO{
    HashSet<UserData> users = new HashSet<>();
    public void clear(){
        users.clear();
    }
}
