package serviceTests;
import org.junit.jupiter.api.*;
import model.*;
import chess.ChessGame;

public class ServerTests {
    public void clearTest(){
        ChessGame chessGame = new ChessGame();
        AuthData authData = new AuthData("authToken", "username");
        UserData userData = new UserData("username", "password", "email");
        GameData gameData = new GameData(0,"username", "username2","game",chessGame);

    }
}
