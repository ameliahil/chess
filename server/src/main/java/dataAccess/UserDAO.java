package dataAccess;
import model.UserData;

public interface UserDAO {
    void clear();
    String createUser(String username, String password, String email);
    UserData getUser(String username);
}
