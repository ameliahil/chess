package dataAccessTests;
import Requests.ListGamesResponse;
import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import dataAccess.SQLGameDAO;
import dataAccess.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import java.util.HashSet;
import java.util.Objects;
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
        authDAO.addAuth("I-want-to-be-asleep","hatelife");
        assertEquals(authDAO.getUser("I-want-to-be-asleep"),"hatelife");
    }
    @Test
    public void addAuthNegTest() throws DataAccessException {
        authDAO.addAuth("I-want-to-be-asleep","hatelife");
        assertThrows(DataAccessException.class,()->{authDAO.addAuth("I-want-to-be-asleep","hatelife");});
    }

    @Test
    public void logoutPosTest() throws DataAccessException {
        authDAO.addAuth("I-want-to-be-asleep","hatelife");
        authDAO.logout("I-want-to-be-asleep");
        assertThrows(DataAccessException.class,()->{authDAO.getUser("I-want-to-be-asleep");});
    }
    @Test
    public void logoutNegTest() throws DataAccessException {
        authDAO.addAuth("I-want-to-be-asleep","hatelife");
        assertThrows(DataAccessException.class,()->{authDAO.logout("I-want-to-be-awake");});
    }

    @Test
    public void getUserPosTest() throws DataAccessException {
        authDAO.addAuth("I-want-to-be-asleep","hatelife");
        assertEquals(authDAO.getUser("I-want-to-be-asleep"),"hatelife");
    }
    @Test
    public void getUserNegTest() throws DataAccessException {
        assertThrows(DataAccessException.class,()->{authDAO.getUser("I-want-to-be-asleep");});
    }
    @Test
    public void findAuthPosTest() throws DataAccessException {
        authDAO.addAuth("I-want-to-be-asleep","hatelife");
        boolean foundAuth = false;
        try{
            authDAO.findAuth("I-want-to-be-asleep");
            foundAuth = true;
        }
        catch(DataAccessException e){}
        assertTrue(foundAuth);
    }
    @Test
    public void findAuthNegTest() throws DataAccessException {
        authDAO.addAuth("I-want-to-be-asleep","hatelife");
        boolean foundAuth = false;
        try{
            authDAO.findAuth("I-want-to-be-awake");
            foundAuth = true;
        }
        catch(DataAccessException e){}
        assertFalse(foundAuth);
    }

    @Test
    public void createUserPosTest() throws DataAccessException {
        userDAO.createUser("username","pass","email");
        UserData userData = new UserData("username","pass","email");
        UserData insertedUser =  userDAO.getUser("username");
        assertEquals(userData,insertedUser);
    }
    @Test
    public void createUserNegTest() throws DataAccessException {
        userDAO.createUser("username","pass","email");
        assertThrows(DataAccessException.class, ()->{userDAO.createUser("username","pass","email");});
    }
    @Test
    public void loginPosTest() throws DataAccessException {
        userDAO.createUser("username","pass","email");
        boolean passes = false;
        try {
            userDAO.login("username", "pass");
            passes = true;
        }
        catch(DataAccessException e){}
        assertTrue(passes);
    }
    @Test
    public void loginNegTest() throws DataAccessException {
        userDAO.createUser("username","pass","email");
        boolean passes = false;
        try {
            userDAO.login("username", "I<3CS240");
            passes = true;
        }
        catch(DataAccessException e){}
        assertFalse(passes);
    }
    @Test
    public void getUserUserPosTest() throws DataAccessException {
        userDAO.createUser("username","pass","email");
        UserData userData = new UserData("username","pass","email");
        UserData insertedUser =  userDAO.getUser("username");
        assertEquals(userData,insertedUser);
    }
    @Test
    public void getUserUserNegTest() throws DataAccessException {
        userDAO.createUser("username","pass","email");
        assertThrows(DataAccessException.class,()->{userDAO.getUser("user");});
    }
    @Test
    public void createGamePosTest() throws DataAccessException {
        gameDAO.createGame(null,null,"game");
        assertEquals(gameDAO.listGames().size(),1);
    }
    @Test
    public void createGameNegTest() throws DataAccessException {
        assertThrows(DataAccessException.class,()->{gameDAO.createGame(null,null,null);});
    }
    @Test
    public void listGamesPosTest() throws DataAccessException {
        gameDAO.createGame(null,null,"game");
        assertEquals(gameDAO.listGames().size(),1);
    }
    @Test
    public void listGamesNegTest() throws DataAccessException {
        assertEquals(gameDAO.listGames().size(),0);
    }
    @Test
    public void whiteUsernamePosTest() throws DataAccessException {
        int gameID = gameDAO.createGame(null,null,"game-time").gameID();
        userDAO.createUser("username", "pass", "email@email.email");

        gameDAO.updateWhiteUsername(gameID,"username");
        HashSet<ListGamesResponse> logins = new HashSet<>(gameDAO.listGames());
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
    public void whiteUsernameNegTest() throws DataAccessException {
        int gameID = gameDAO.createGame(null,null,"game-time").gameID();
        userDAO.createUser("username", "pass", "email@email.email");

        gameDAO.updateWhiteUsername(gameID,"username");
        assertThrows(DataAccessException.class, ()-> {gameDAO.updateWhiteUsername(gameID,"user");});
    }
    @Test
    public void blackUsernamePosTest() throws DataAccessException {
        int gameID = gameDAO.createGame(null,null,"game-time").gameID();
        userDAO.createUser("username", "pass", "email@email.email");

        gameDAO.updateBlackUsername(gameID,"username");
        HashSet<ListGamesResponse> logins = new HashSet<>(gameDAO.listGames());
        boolean passesTest = false;
        for(ListGamesResponse login:logins){
            if(Objects.equals(login.blackUsername(), "username")){
                passesTest = true;
                break;
            }
        }
        assertTrue(passesTest);
    }
    @Test
    public void blackUsernameNegTest() throws DataAccessException {
        int gameID = gameDAO.createGame(null,null,"game-time").gameID();
        userDAO.createUser("username", "pass", "email@email.email");

        gameDAO.updateBlackUsername(gameID,"username");
        assertThrows(DataAccessException.class, ()-> {gameDAO.updateBlackUsername(gameID,"user");});
    }

    @Test
    public void watchPosTest() throws DataAccessException {
        int gameID = gameDAO.createGame(null,null,"game-time").gameID();
        boolean passes = false;
        try{
            gameDAO.watch(gameID);
            passes = true;
        }
        catch(DataAccessException e){}
        assertTrue(passes);
    }
    @Test
    public void watchNegTest() throws DataAccessException {
        gameDAO.createGame(null,null,"game-time");
        boolean passes = false;
        try{
            gameDAO.watch(1590);
            passes = true;
        }
        catch(DataAccessException e){}
        assertFalse(passes);
    }
}
