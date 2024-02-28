package serviceTests;
import Requests.LoginResponse;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import org.junit.jupiter.api.*;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTests {
    MemoryUserDAO userDAO = new MemoryUserDAO();
    UserService userService = new UserService(userDAO);
    MemoryGameDAO gameDAO = new MemoryGameDAO();
    GameService gameService = new GameService(gameDAO);
    MemoryAuthDAO authDAO = new MemoryAuthDAO();
    AuthService authService = new AuthService(authDAO);
    @AfterEach
    public void afterEach() throws DataAccessException {
        userService.clearUser();
        gameService.clearGame();
        authService.clearAuth();
    }
    @Test
    public void clearTest() throws DataAccessException {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        UserService userService = new UserService(userDAO);

        userDAO.createUser("username","password","email");
        userDAO.createUser("user","pass", "mail");

        userService.clearUser();

        assertEquals(0,userDAO.users.size());
    }
    @Test
    public void createGameTestPos() throws DataAccessException{
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO);

        gameService.createGame("game name");

        assertEquals(1,gameDAO.games.size());
    }
    @Test
    public void createGameTestNeg() throws DataAccessException{
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO);

        gameService.createGame("game");
        assertThrows(DataAccessException.class, () -> { gameService.createGame("game");
        });
    }
    @Test
    public void joinGameTestPos() throws DataAccessException{
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO);

        gameService.createGame("new game");

        gameDAO.updateWhiteUsername(0,"username");

        GameData gameData = gameDAO.getGame(0);

        assertNotEquals(gameData.whiteUsername(),null);
    }
    @Test
    public void joinGameTestNeg() throws DataAccessException{
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO);

        gameService.createGame("new game");

        gameDAO.updateWhiteUsername(0,"username");

        assertThrows(DataAccessException.class, () -> { gameDAO.updateWhiteUsername(0,"username");
        });
    }
    @Test
    public void listGamesTestPos() throws DataAccessException{
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO);

        gameService.createGame("new game");

        assertEquals(gameService.listGames().size(),1);
    }
    @Test
    public void listGamesTestNeg() throws DataAccessException{
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO);

        assertNotEquals(gameService.listGames().size(),1);
    }
    @Test
    public void loginTestPos() throws DataAccessException{
        MemoryUserDAO userDAO = new MemoryUserDAO();
        userDAO.createUser("user1", "password1", "email1@example.com");

        assertDoesNotThrow(() -> {userDAO.login("user1","password1");});
    }
    @Test
    public void loginTestNeg() throws DataAccessException{
        MemoryUserDAO userDAO = new MemoryUserDAO();
        userDAO.createUser("user1", "password1", "email1@example.com");

        assertThrows(DataAccessException.class, () -> {userDAO.login("user1","password2");});
    }
    @Test
    public void logoutTestPos() throws DataAccessException{
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        AuthService authService = new AuthService(authDAO);

        assertEquals(0, authDAO.authTokens.size());
    }
    @Test
    public void logoutTestNeg() throws DataAccessException{
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        AuthService authService = new AuthService(authDAO);
        LoginResponse login = userDAO.createUser("user1", "password1", "email1@example.com");

        String authToken = login.authToken();
        assertThrows(DataAccessException.class, () -> {authService.logout(authToken);});
    }
    @Test
    public void registrationTestPos() throws DataAccessException{
        MemoryUserDAO userDAO = new MemoryUserDAO();
        userDAO.createUser("user1", "password1", "email1@example.com");

        assertEquals(1,userDAO.users.size());
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
