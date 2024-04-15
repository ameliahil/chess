package UI;

import Requests.*;
import chess.*;
import dataAccess.DataAccessException;
import dataAccess.SQLGameDAO;
import model.UserData;
import webSocket.NotificationHandler;
import webSocket.WebSocketFacade;
import model.GameData;

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

    private SQLGameDAO gameDAO = new SQLGameDAO();
    private ChessGame.TeamColor color;

    private String authToken;
    private int gameID;
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
                case "leave" -> leave(params);
                case "move" -> makeMove(params);
                case "redraw" -> redrawBoard(params);
                case "resign" -> resign(params);
                case "highlight" -> highlightLegalMoves(params);
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
                this.color = color;
                int gameID = idMap.get(gameNum);
                this.gameID = gameID;
                JoinRequest join = new JoinRequest(color,gameID);
                server.joinGame(join);
                WebSocketFacade ws = new WebSocketFacade(url,notificationHandler);
                ws.joinPlayer(authToken,gameID,color);
                state = State.INGAME;
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
            if((state == State.SIGNEDIN) && !idMap.isEmpty()) {
                color = ChessGame.TeamColor.WHITE;
                int gameNum = Integer.parseInt(params[0]);
                int gameID = idMap.get(gameNum);
                JoinRequest join = new JoinRequest(null,gameID);
                this.gameID = gameID;
                server.joinGame(join);
                WebSocketFacade ws = new WebSocketFacade(url,notificationHandler);
                ws.joinObserve(authToken,gameID);
                state = State.INGAME;
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

    public String redrawBoard(String... params) throws DataAccessException {
        if(params.length == 0){
            GameData gameData = gameDAO.getGame(gameID);
            printBoard(color, gameData);
        }
        return "";
    }

    public String makeMove(String... params) throws DataAccessException {
        if((params.length == 2) || (params.length == 3)){
            if(state == State.INGAME) {
                String start = (params[0]);
                String end = params[1];
                String promotion = null;
                ChessPiece.PieceType promotionPiece = null;
                if(params.length == 3) {
                    promotion = params[2];
                    promotionPiece = findPiece(promotion);
                }

                int startCol = findCol(start.charAt(0));
                int startRow = start.charAt(1) - '0';
                int endCol = findCol(end.charAt(0));
                int endRow = end.charAt(1) - '0';
                ChessPosition startPosition = new ChessPosition(startRow,startCol);
                ChessPosition endPosition = new ChessPosition(endRow,endCol);
                ChessMove move = new ChessMove(startPosition,endPosition,promotionPiece);

                WebSocketFacade ws = new WebSocketFacade(url,notificationHandler);
                ws.makeMove(authToken,gameID, move);
                state = State.INGAME;
                return "";
            }
            else{
                throw new DataAccessException("Not logged in");
            }
        }
        else{
            throw new DataAccessException("Wrong number of parameters");
        }
    }

    private ChessPiece.PieceType findPiece(String piece){
        switch(piece){
            case "queen" -> {return ChessPiece.PieceType.QUEEN;}
            case "rook" -> {return ChessPiece.PieceType.ROOK;}
            case "bishop" -> {return ChessPiece.PieceType.BISHOP;}
            case "knight" -> {return ChessPiece.PieceType.KNIGHT;}
            default -> {return null;}
        }
    }

    private int findCol(char colChar){
        switch (colChar) {
            case 'a' -> {return 8;}
            case 'b' -> {return 7;}
            case 'c' -> {return 6;}
            case 'd' -> {return 5;}
            case 'e' -> {return 4;}
            case 'f' -> {return 3;}
            case 'g' -> {return 2;}
            case 'h' -> {return 1;}
        }
        return -1;
    }

    public String leave(String... params) throws DataAccessException {
        if(params.length == 0){
            if(state == State.INGAME) {
                WebSocketFacade ws = new WebSocketFacade(url,notificationHandler);
                ws.leave(authToken,gameID);
                state = State.SIGNEDIN;
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
    public String resign(String... params) throws DataAccessException{
        if(params.length == 0){
            if(state == State.INGAME){
                WebSocketFacade ws = new WebSocketFacade(url,notificationHandler);
                ws.resign(authToken,gameID);
                return "";
            }
            else{
                throw new DataAccessException("Not logged in");
            }
        }
        else{
            throw new DataAccessException("Wrong number of parameters");
        }
    }
    public String highlightLegalMoves(String... params) throws DataAccessException{
        return "";
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
                move <start square> <end square> <promotion piece> - join a game! make sure to list games first
                highlight <square> - what moves can your piece play!
                leave - would you like to leave the game?
                resign - you will forfeit the game!
                help
                """;
    }

    public void printBoard(ChessGame.TeamColor color, GameData game){
        ChessGame chessGame = game.implementation();
        ChessBoard board = chessGame.getBoard();

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        System.out.print(SET_TEXT_BOLD);

        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};

        if (color == ChessGame.TeamColor.WHITE) {
            System.out.print("  ");
            for (char letter : letters) {
                System.out.print(letter + "   ");
            }
            System.out.println();

            for (int y = 0; y < 8; y++) {
                int row = 8 - y;
                System.out.print(SET_TEXT_COLOR_WHITE + row + " ");
                System.out.print(SET_TEXT_COLOR_BLACK);
                for (int x = 0; x < 8; x++) {
                    boolean isWhite = (x + y) % 2 == 0;
                    String piece = findPiece(y, x, board);
                    String backgroundColor = isWhite ? SET_BG_COLOR_YELLOW : SET_BG_COLOR_MAGENTA;
                    System.out.print(backgroundColor + piece);
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        } else if (color == ChessGame.TeamColor.BLACK) {
            System.out.print("  ");
            for (int i = 7; i > -1; i--) {
                System.out.print(letters[i] + "   ");
            }
            System.out.println();

            for (int y = 7; y > -1; y--) {
                int row = 8 - y;
                System.out.print(SET_TEXT_COLOR_WHITE + row + " ");
                System.out.print(SET_TEXT_COLOR_BLACK);
                for (int x = 7; x > -1; x--) {
                    String piece = findPiece(y, x, board);
                    boolean isWhite = (x + y) % 2 == 0;
                    String backgroundColor = isWhite ? SET_BG_COLOR_YELLOW : SET_BG_COLOR_MAGENTA;
                    System.out.print(backgroundColor + piece);
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR);
        }

        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_DARK_GREY + RESET_TEXT_BOLD_FAINT);
    }

    private String findPiece(int row, int col, ChessBoard board){
        ChessPosition position = new ChessPosition(8 - row,8 - col);
        ChessPiece piece = board.getPiece(position);
        if(piece == null){
            return EMPTY;
        }
        ChessPiece.PieceType pieceType = piece.getPieceType();
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            if(pieceType == ChessPiece.PieceType.PAWN){
                return WHITE_PAWN;
            }
            if(pieceType == ChessPiece.PieceType.ROOK){
                return WHITE_ROOK;
            }
            if(pieceType == ChessPiece.PieceType.KNIGHT){
                return WHITE_KNIGHT;
            }
            if(pieceType == ChessPiece.PieceType.BISHOP){
                return WHITE_BISHOP;
            }
            if(pieceType == ChessPiece.PieceType.KING){
                return WHITE_KING;
            }
            if(pieceType == ChessPiece.PieceType.QUEEN){
                return WHITE_QUEEN;
            }
        }
        else if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            if(pieceType == ChessPiece.PieceType.ROOK){
                return BLACK_ROOK;
            }
            if(pieceType == ChessPiece.PieceType.PAWN){
                return BLACK_PAWN;
            }
            if(pieceType == ChessPiece.PieceType.BISHOP){
                return BLACK_BISHOP;
            }
            if(pieceType == ChessPiece.PieceType.KNIGHT){
                return BLACK_KNIGHT;
            }
            if(pieceType == ChessPiece.PieceType.KING){
                return BLACK_KING;
            }
            if(pieceType == ChessPiece.PieceType.QUEEN){
                return BLACK_QUEEN;
            }
        }
        return EMPTY;
    }
}
