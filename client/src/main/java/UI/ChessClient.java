package UI;

import Requests.*;
import chess.ChessGame;
import dataAccess.DataAccessException;
import model.UserData;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private String user = "";

    private String authToken;
    private HashMap<Integer, Integer> idMap = new HashMap<>();

    public ChessClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
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
            throw new DataAccessException("Wrong number of parameters");
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
            throw new DataAccessException("Wrong number of parameters");
        }
    }
    public String logout(String... params) throws DataAccessException{
        if(params.length == 0 && state == State.SIGNEDIN){
            server.logout();
            state = State.SIGNEDOUT;
            return String.format("Goodbye, %s.", user);
        }
        else{
            throw new DataAccessException("Wrong number of parameters");
        }
    }
    public String createGame(String... params) throws DataAccessException{
        if(params.length == 1){
            if(state == State.SIGNEDIN) {
                CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
                server.createGame(createGameRequest);
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
                    listString += i + ". " + game.gameName() + "\n";
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
            if(state == State.SIGNEDIN && !idMap.isEmpty()) {
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
            if(state == State.SIGNEDIN && !idMap.isEmpty()) {
                int gameNum = Integer.parseInt(params[0]);
                int gameID = idMap.get(gameNum);
                JoinRequest join = new JoinRequest(null,gameID);
                server.joinGame(join);
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
    public String help(){
        if(state == State.SIGNEDOUT){
            return"""
                register <USERNAME> <PASSWORD> <EMAIL>
                login <USERNAME> <PASSWORD>
                quit
                help
                """;
        }
        return """
                create <NAME>
                list
                join <ID> [WHITE|BLACK|<empty>]
                observe <ID>
                logout
                quit
                help
                """;
    }
}
