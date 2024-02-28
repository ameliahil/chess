package dataAccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    HashMap<String,AuthData> authTokens = new HashMap<>();
    public void clear(){
        authTokens.clear();
    }
    public String createAuth(String username){
        String token = UUID.randomUUID().toString();
        AuthData authToken = new AuthData(token, username);
        authTokens.put(token,authToken);
        return token;
    }
    public AuthData getAuth(String username){
        return authTokens.get(username);
    }
    public void deleteAuth(){ //multiple auth tokens?

    }
    public void logout(String authToken){
        authTokens.remove(authToken);
    }
}
