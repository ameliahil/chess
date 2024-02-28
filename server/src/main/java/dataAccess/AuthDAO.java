package dataAccess;


public interface AuthDAO {
    void clear();
    String createAuth(String username);
    void logout(String authToken) throws DataAccessException;

}
