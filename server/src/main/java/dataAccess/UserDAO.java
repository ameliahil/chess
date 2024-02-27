package dataAccess;
import Requests.LoginResponse;
import model.UserData;

public interface UserDAO {
    void clear();
    LoginResponse createUser(String username, String password, String email);
    UserData getUser(String username);
    LoginResponse login(String username, String password);
}
