package clientTests;

import UI.ServerFacade;
import dataAccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

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
    public void registerPosTest() {

    }
    public void registerNegTest() {

    }
    public void loginPosTest() {

    }
    public void loginNegTest() {

    }
    public void logoutPosTest() {

    }
    public void logoutNegTest() {

    }
    public void listGamesPosTest() {

    }
    public void listGamesNegTest() {

    }
    public void createGamePosTest() {

    }
    public void createGameNegTest() {

    }
    public void joinGamePosTest() {

    }
    public void joinGameNegTest() {

    }
}