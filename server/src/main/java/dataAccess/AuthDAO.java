package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    void clear();
    void createAuth(String username);
    AuthData getAuth(String username);
    void deleteAuth();

}
