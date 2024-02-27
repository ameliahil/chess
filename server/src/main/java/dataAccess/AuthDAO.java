package dataAccess;

import model.AuthData;

public interface AuthDAO {
    void clear();
    void createAuth();
    AuthData getAuth();
    void deleteAuth();

}
