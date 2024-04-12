package UI;

import Requests.*;
import chess.ChessGame;
import dataAccess.DataAccessException;
import model.UserData;
import webSocket.NotificationHandler;
import webSocket.WebSocketFacade;
import webSocketMessages.userCommands.JoinPlayerCommand;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static UI.EscapeSequences.*;

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private String user = "";

    private String authToken;
    private final String url;
    private HashMap<Integer, Integer> idMap = new HashMap<>();
    private NotificationHandler notificationHandler;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        url = serverUrl;
        this.notificationHandler = notificationHandler;
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout(params);
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                case null, default -> help();
            };

        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws DataAccessException{
        if(params.length == 2 && state == State.SIGNEDOUT){
            LoginRequest loginRequest = new LoginRequest(params[0],params[1]);
            LoginResponse loginResponse = server.login(loginRequest);
            authToken = loginResponse.authToken();
            state = State.SIGNEDIN;
            user = loginResponse.username();
            return String.format("You signed in as %s.", user);
        }
        else{
            throw new DataAccessException("Error");
        }
    }
    public String register(String... params) throws DataAccessException{
        if(params.length == 3 && state == State.SIGNEDOUT){
            UserData registerRequest = new UserData(params[0],params[1],params[2]);
            LoginResponse registerResponse = server.registration(registerRequest);
            authToken = registerResponse.authToken();
            state = State.SIGNEDIN;
            user = registerResponse.username();
            return String.format("You signed in as %s.", user);
        }
        else{
            throw new DataAccessException("Error");
        }
    }
    public String logout(String... params) throws DataAccessException{
        if(params.length == 0 && state == State.SIGNEDIN){
            server.logout();
            state = State.SIGNEDOUT;
            return String.format("Goodbye, %s.", user);
        }
        else{
            throw new DataAccessException("Error");
        }
    }
    public String createGame(String... params) throws DataAccessException{
        if(params.length == 1){
            if(state == State.SIGNEDIN) {
                CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
                CreateGameResponse response = server.createGame(createGameRequest);
                int gameID = response.gameID();
                return String.format("You created game %s.", params[0]);
            }
            else{
                throw new DataAccessException("Not logged in");
            }
        }
        else{
            throw new DataAccessException("Wrong number of parameters");
        }
    }
    public String listGames(String... params) throws DataAccessException{
        if(params.length == 0){
            if(state == State.SIGNEDIN) {
                ListGamesResponseList list  = server.listGames();
                Collection<ListGamesResponse> games = list.games();
                int i = 0;
                String listString = "";
                for(ListGamesResponse game: games){
                    i += 1;
                    listString += i + ". " + game.gameName() + "\n\tWhite:" + game.whiteUsername() + "\n\tBlack:" + game.blackUsername() + "\n";
                    idMap.put(i,game.gameID());
                }
                return String.format("Games:\n" + listString);
            }
            else{
                throw new DataAccessException("Not logged in");
            }
        }
        else{
            throw new DataAccessException("Wrong number of parameters");
        }
    }
    public String joinGame(String... params) throws DataAccessException{
        if(params.length == 2){
            if((state == State.SIGNEDIN || state == State.INGAME) && !idMap.isEmpty()) {
                int gameNum = Integer.parseInt(params[0]);
                ChessGame.TeamColor color = null;
                if(params[1].equals("white")){
                    color = ChessGame.TeamColor.WHITE;
                }else if(params[1].equals("black")){
                    color = ChessGame.TeamColor.BLACK;
                }
                int gameID = idMap.get(gameNum);
                JoinRequest join = new JoinRequest(color,gameID);
                server.joinGame(join);
                WebSocketFacade ws = new WebSocketFacade(url,notificationHandler);
                ws.joinPlayer(authToken,gameID,color,user);
                state = State.INGAME;
                //printBoard(color);
                return String.format("You joined game as %s.", params[1]);
            }
            else{
                throw new DataAccessException("Not logged in");
            }
        }
        else{
            throw new DataAccessException("Wrong number of parameters");
        }
    }
    public String observeGame(String... params) throws DataAccessException{
        if(params.length == 1){
            if((state == State.SIGNEDIN || state == State.INGAME) && !idMap.isEmpty()) {
                int gameNum = Integer.parseInt(params[0]);
                int gameID = idMap.get(gameNum);
                JoinRequest join = new JoinRequest(null,gameID);
                server.joinGame(join);
                state = State.INGAME;
                printBoard(ChessGame.TeamColor.WHITE);
                return String.format("You joined game %s as an observer.", gameNum);
            }
            else{
                throw new DataAccessException("Not logged in");
            }
        }
        else{
            throw new DataAccessException("Wrong number of parameters");
        }
    }

    public String leave(String... params) throws DataAccessException {
        if(params.length == 0){
            if(state == State.INGAME) {
                return "You left the game.";
            }
            else{
                throw new DataAccessException("Not logged in");
            }
        }
        else{
            throw new DataAccessException("Wrong number of parameters");
        }
    }
    public String help(){
        if(state == State.SIGNEDOUT){
            return"""
                register <USERNAME> <PASSWORD> <EMAIL> - type in your info to get started
                login <USERNAME> <PASSWORD> - type your username and password
                quit - done already?
                help - type help to see all commands
                """;
        }
        else if(state == State.SIGNEDIN){
            return """
                create <NAME> - create a new game
                list - list all possible games
                join <ID> [WHITE|BLACK] - join a game! make sure to list games first
                observe <ID> - don't want to play? type the game id to simply observe
                logout - see you next time!
                quit
                help
                """;}
        return """
                redraw - redraw the chess board
                move <start square> <end square> - join a game! make sure to list games first
                highlight <square> - what moves can your piece play!
                leave - would you like to leave the game?
                resign - you will forfeit the game!
                help
                """;
    }

    public void printBoard(ChessGame.TeamColor color){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        System.out.print(SET_TEXT_COLOR_BLACK + SET_TEXT_BOLD);

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                String piece = findPiece(y, x);
                boolean isWhite = (x + y) % 2 == 0;
                String backgroundColor = isWhite ? SET_BG_COLOR_YELLOW : SET_BG_COLOR_MAGENTA;
                System.out.print(backgroundColor + piece);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }
        System.out.println(EscapeSequences.RESET_BG_COLOR);

        for (int y = 7; y > -1; y--) {
            for (int x = 7; x > -1; x--) {
                String piece = findPiece(y, x);
                boolean isWhite = (x + y) % 2 == 0;
                String backgroundColor = isWhite ? SET_BG_COLOR_YELLOW : SET_BG_COLOR_MAGENTA;
                System.out.print(backgroundColor + piece);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }

        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_DARK_GREY + RESET_TEXT_BOLD_FAINT);
    }

    private String findPiece(int row, int col){
        if(row > 1 && row < 6){
            return EMPTY;
        }
        if(row == 1){
            return BLACK_PAWN;
        }
        if(row == 6){
            return WHITE_PAWN;
        }
        if(row == 0){
            if(col == 0 || col == 7){
                return BLACK_ROOK;
            }
            if(col == 1 || col == 6){
                return BLACK_KNIGHT;
            }
            if(col == 2 || col == 5){
                return BLACK_BISHOP;
            }
            if(col == 3){
                return BLACK_KING;
            }
            if(col == 4){
                return BLACK_QUEEN;
            }
        }
        if(row == 7){
            if(col == 0 || col == 7){
                return WHITE_ROOK;
            }
            if(col == 1 || col == 6){
                return WHITE_KNIGHT;
            }
            if(col == 2 || col == 5){
                return WHITE_BISHOP;
            }
            if(col == 3){
                return WHITE_KING;
            }
            if(col == 4){
                return WHITE_QUEEN;
            }
        }
        return null;
    }
}
