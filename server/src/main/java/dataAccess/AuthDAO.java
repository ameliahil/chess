package dataAccess;


public interface AuthDAO {
    void clear() throws DataAccessException;
    void logout(String authToken) throws DataAccessException;

}
