package serviceTests;
import Requests.JoinRequest;
import Requests.ListGamesResponse;
import Requests.ListGamesResponseList;
import Requests.LoginResponse;
import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.*;
import org.junit.jupiter.api.*;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTests {
    SQLUserDAO userDAO = new SQLUserDAO();
    UserService userService = new UserService(userDAO);
    SQLGameDAO gameDAO = new SQLGameDAO();
    GameService gameService = new GameService(gameDAO);
    SQLAuthDAO authDAO = new SQLAuthDAO();
    AuthService authService = new AuthService(authDAO);
    @AfterEach
    public void afterEach() throws DataAccessException {
        userService.clearUser();
        gameService.clearGame();
        authService.clearAuth();
    }
    @Test
    public void clearTest() throws DataAccessException {
        userDAO.createUser("username","password","email");
        userDAO.createUser("user","pass", "mail");

        userService.clearUser();

        assertThrows(DataAccessException.class,() -> {userDAO.getUser("username");
        });
    }
    @Test
    public void createGameTestPos() throws DataAccessException{
        SQLGameDAO gameDAO = new SQLGameDAO();
        GameService gameService = new GameService(gameDAO);

        gameService.createGame("game name");

        assertNotNull(gameService.listGames());
    }
    @Test
    public void createGameTestNeg() throws DataAccessException{
        gameService.createGame("game");
        assertThrows(DataAccessException.class, () -> { gameService.createGame("game");
        });
    }
    @Test
    public void joinGameTestPos() throws DataAccessException{
        UserData newUser = new UserData("username", "pass", "email@email.email");
        userService.addUser(newUser);
        int gameID = gameService.createGame("new game").gameID();
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE,gameID);
        gameService.join(joinRequest,"username");
        HashSet<ListGamesResponse> logins = new HashSet<>(gameService.listGames());
        boolean passesTest = false;
        for(ListGamesResponse login:logins){
            if(Objects.equals(login.whiteUsername(), "username")){
                passesTest = true;
                break;
            }
        }
        assertTrue(passesTest);
    }
    @Test
    public void joinGameTestNeg() throws DataAccessException{
        UserData newUser = new UserData("username", "pass", "email@email.email");
        userService.addUser(newUser);
        int gameID = gameService.createGame("new game").gameID();
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE,gameID);
        gameService.join(joinRequest,"username");

        assertThrows(DataAccessException.class, () -> { gameService.join(joinRequest,"username");
        });
    }
    @Test
    public void listGamesTestPos() throws DataAccessException{
        gameService.createGame("new game");

        assertEquals(gameService.listGames().size(),1);
    }
    @Test
    public void listGamesTestNeg() throws DataAccessException{
        assertNotEquals(gameService.listGames().size(),1);
    }
    @Test
    public void loginTestPos() throws DataAccessException{
        userDAO.createUser("user1", "password1", "email1@example.com");

        assertDoesNotThrow(() -> {userDAO.login("user1","password1");});
    }
    @Test
    public void loginTestNeg() throws DataAccessException{
        userDAO.createUser("user1", "password1", "email1@example.com");
        assertThrows(DataAccessException.class, () -> {userDAO.login("user1","password2");});
    }
    @Test
    public void logoutTestPos() throws DataAccessException{
        LoginResponse login = userDAO.createUser("user1", "password1", "email1@example.com");
        authService.insertAuth(login.username(),login.authToken());
        String authToken = login.authToken();
        authService.logout(authToken);
        assertThrows(DataAccessException.class, () -> {authService.findAuth(login.authToken());});
    }
    @Test
    public void logoutTestNeg() throws DataAccessException{
        userDAO.createUser("user1", "password1", "email1@example.com");
        assertThrows(DataAccessException.class, () -> {authService.logout("I-hate-CS-240");});
    }
    @Test
    public void registrationTestPos() throws DataAccessException{
        LoginResponse login = userDAO.createUser("user1", "password1", "email1@example.com");
        assertNotNull(login.authToken());
    }
    @Test
    public void registrationTestNeg() throws DataAccessException{
        MemoryUserDAO userDAO = new MemoryUserDAO();
        userDAO.createUser("user1", "password1", "email1@example.com");

        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser("user1", "password2", "email2@example.com");
        });
    }

}

