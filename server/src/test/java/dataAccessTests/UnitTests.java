package dataAccessTests;
import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import dataAccess.SQLGameDAO;
import dataAccess.SQLUserDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {
    //must test everything
    SQLUserDAO userDAO = new SQLUserDAO();
    SQLGameDAO gameDAO = new SQLGameDAO();
    SQLAuthDAO authDAO = new SQLAuthDAO();
    @AfterEach
    public void afterEach() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    public void authClearTest() throws DataAccessException {
        authDAO.addAuth("I-hate-this-class","username");
        authDAO.clear();
        assertThrows(DataAccessException.class,() -> {authDAO.findAuth("I-hate-this-class");
        });
    }

    @Test
    public void gameClearTest() throws DataAccessException {
        gameDAO.createGame(null,null,"game:)");
        gameDAO.clear();
        assertEquals(gameDAO.listGames().size(),0);
    }
    @Test
    public void userClearTest() throws DataAccessException {
        userDAO.createUser("username","password","email");
        userDAO.createUser("user","pass", "mail");
        userDAO.clear();
        assertThrows(DataAccessException.class,() -> {userDAO.getUser("username");
        });
    }

    @Test
    public void addAuthPosTest() throws DataAccessException {

    }
    @Test
    public void addAuthNegTest() throws DataAccessException {

    }

    @Test
    public void logoutPosTest() throws DataAccessException {

    }
    @Test
    public void logoutNegTest() throws DataAccessException {

    }

    @Test
    public void getUserPosTest() throws DataAccessException {

    }
    @Test
    public void getUserNegTest() throws DataAccessException {

    }
    @Test
    public void findAuthPosTest() throws DataAccessException {

    }
    @Test
    public void findAuthNegTest() throws DataAccessException {

    }

    @Test
    public void createUserPosTest() throws DataAccessException {

    }
    @Test
    public void createUserNegTest() throws DataAccessException {

    }
    @Test
    public void loginPosTest() throws DataAccessException {

    }
    @Test
    public void loginNegTest() throws DataAccessException {

    }
    @Test
    public void getUserUserPosTest() throws DataAccessException {

    }
    @Test
    public void getUserUserNegTest() throws DataAccessException {

    }
    @Test
    public void createGamePosTest() throws DataAccessException {

    }
    @Test
    public void createGameNegTest() throws DataAccessException {

    }
    @Test
    public void listGamesPosTest() throws DataAccessException {

    }
    @Test
    public void listGamesNegTest() throws DataAccessException {

    }
    @Test
    public void whiteUsernamePosTest() throws DataAccessException {

    }
    @Test
    public void whiteUsernameNegTest() throws DataAccessException {

    }
    @Test
    public void blackUsernamePosTest() throws DataAccessException {

    }
    @Test
    public void blackUsernameNegTest() throws DataAccessException {

    }

    @Test
    public void watchPostTest() throws DataAccessException {

    }
    @Test
    public void watchNegTest() throws DataAccessException {

    }
}
