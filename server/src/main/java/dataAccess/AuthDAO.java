package dataAccess;


public interface AuthDAO {
    void clear() throws DataAccessException;
    String createAuth(String username) throws DataAccessException;
    void logout(String authToken) throws DataAccessException;

}
