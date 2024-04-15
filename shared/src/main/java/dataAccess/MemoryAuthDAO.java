package dataAccess;

import model.AuthData;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    public HashMap<String,AuthData> authTokens = new HashMap<>();
    public void clear(){
        authTokens.clear();
    }
    public String createAuth(String username){
        String token = UUID.randomUUID().toString();
        AuthData authToken = new AuthData(token, username);
        authTokens.put(token,authToken);
        return token;
    }

    public void logout(String authToken) throws DataAccessException {
        if(authTokens.get(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        authTokens.remove(authToken);
    }

    public void insertAuth(String username, String authToken){
        AuthData authData = new AuthData(authToken,username);
        authTokens.put(authToken, authData);
    }

    public String getUser(String authToken){
        return authTokens.get(authToken).username();
    }

    public void findAuth(String authToken) throws DataAccessException {
        if(authTokens.get(authToken) == null){
            throw new DataAccessException("Error: unauthorized");
        }
    }
}

