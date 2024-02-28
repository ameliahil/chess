package dataAccess;

import model.AuthData;

public interface AuthDAO {
    void clear();
    String createAuth(String username);
    AuthData getAuth(String username);
    void deleteAuth();
    void logout(String authToken) throws DataAccessException;

}
