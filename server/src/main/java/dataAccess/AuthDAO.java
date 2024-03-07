package dataAccess;


public interface AuthDAO {
    void clear() throws DataAccessException;
    String createAuth(String username);
    void logout(String authToken) throws DataAccessException;

}
