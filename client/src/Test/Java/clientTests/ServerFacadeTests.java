package clientTests;

import Requests.LoginRequest;
import Requests.LoginResponse;
import UI.ServerFacade;
import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;



public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    SQLUserDAO userDAO = new SQLUserDAO();
    SQLGameDAO gameDAO = new SQLGameDAO();
    SQLAuthDAO authDAO = new SQLAuthDAO();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        String url = "http://localhost:" + port;
        serverFacade = new ServerFacade(url);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void clearTest() {
        assertDoesNotThrow(() -> {serverFacade.clear();});
    }
    @Test
    public void registerPosTest() throws DataAccessException {
        UserData user = new UserData("user","pass","email");
        LoginResponse loginResponse = serverFacade.registration(user);
        assertEquals(authDAO.getUser(loginResponse.authToken()),"user");
    }
    @Test
    public void registerNegTest() throws DataAccessException {
        UserData user = new UserData("newUser","pass","email");
        LoginResponse loginResponse = serverFacade.registration(user);
        assertNotEquals(authDAO.getUser(loginResponse.authToken()),"user");
    }
    @Test
    public void loginPosTest() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        LoginResponse loginResponse = serverFacade.login(loginRequest);
        assertNotNull(loginResponse.authToken());
    }
    @Test
    public void loginNegTest() {
        LoginRequest loginRequest = new LoginRequest("user", "past");
        assertThrows(DataAccessException.class,()->{serverFacade.login(loginRequest);});
    }
    @Test
    public void logoutPosTest() {

    }
    @Test
    public void logoutNegTest() {

    }
    @Test
    public void listGamesPosTest() {

    }
    @Test
    public void listGamesNegTest() {

    }
    @Test
    public void createGamePosTest() {

    }
    @Test
    public void createGameNegTest() {

    }
    @Test
    public void joinGamePosTest() {

    }
    @Test
    public void joinGameNegTest() {

    }
}