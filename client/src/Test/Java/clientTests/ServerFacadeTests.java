package clientTests;

import Requests.*;
import UI.ServerFacade;
import chess.ChessGame;
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
    public static void init() throws DataAccessException {
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

    @BeforeEach
    void clear() throws DataAccessException {serverFacade.clear();}
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
        UserData user = new UserData("user","pass","email");
        serverFacade.registration(user);
        serverFacade.logout();
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
    public void logoutPosTest() throws DataAccessException {
        UserData user = new UserData("user","pass","email");
        serverFacade.registration(user);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        serverFacade.login(loginRequest);
        assertDoesNotThrow(() -> {serverFacade.logout();});
    }
    @Test
    public void logoutNegTest() {
        assertThrows(DataAccessException.class,()->{serverFacade.logout();});
    }
    @Test
    public void listGamesNegTest() throws DataAccessException {
        UserData user = new UserData("user","pass","email");
        serverFacade.registration(user);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        serverFacade.login(loginRequest);
        assertTrue(serverFacade.listGames().games().isEmpty());
    }
    @Test
    public void listGamesPosTest() throws DataAccessException {
        UserData user = new UserData("user","pass","email");
        serverFacade.registration(user);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        serverFacade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("newGame");
        serverFacade.createGame(createGameRequest);
        assertNotNull(serverFacade.listGames());
    }
    @Test
    public void createGamePosTest() throws DataAccessException {
        UserData user = new UserData("user","pass","email");
        serverFacade.registration(user);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        serverFacade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        serverFacade.createGame(createGameRequest);
        assertNotNull(serverFacade.listGames());
    }
    @Test
    public void createGameNegTest() throws DataAccessException {
        UserData user = new UserData("user","pass","email");
        serverFacade.registration(user);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        serverFacade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        serverFacade.createGame(createGameRequest);
        assertThrows(DataAccessException.class,()->{serverFacade.createGame(createGameRequest);});
    }
    @Test
    public void joinGamePosTest() throws DataAccessException {
        UserData user = new UserData("user","pass","email");
        serverFacade.registration(user);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        serverFacade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        CreateGameResponse createGameResponse =  serverFacade.createGame(createGameRequest);
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE,createGameResponse.gameID());
        assertDoesNotThrow(() -> {serverFacade.joinGame(joinRequest);});
    }
    @Test
    public void joinGameNegTest() throws DataAccessException {
        UserData user = new UserData("user","pass","email");
        serverFacade.registration(user);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        serverFacade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myGame");
        CreateGameResponse createGameResponse =  serverFacade.createGame(createGameRequest);
        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE,createGameResponse.gameID());
        serverFacade.joinGame(joinRequest);
        assertThrows(DataAccessException.class,()->{serverFacade.joinGame(joinRequest);});
    }
}