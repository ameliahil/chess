package dataAccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO{
    HashSet<AuthData> authTokens = new HashSet<>();
    public void clear(){
        authTokens.clear();
    }
    public void createAuth(){}
    public AuthData getAuth(){return null;}
    public void deleteAuth(){}
}
