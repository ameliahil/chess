package UI;

import Requests.LoginRequest;
import Requests.LoginResponse;
import dataAccess.DataAccessException;

import java.util.Arrays;

public class ChessClient {
    private final Repl repl;
    private State state = State.SIGNEDOUT;
    private final ServerFacade server;
    private final String serverUrl;

    private String authToken;

    public ChessClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> createGame();
                case "list" -> listGames(params);
                case "join" -> joinGame();
                case "observe" -> observeGame();
                case "quit" -> "quit";
                case null, default -> help();
            };

        } catch (DataAccessException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws DataAccessException{
        if(params.length == 2){
            LoginRequest loginRequest = new LoginRequest(params[0],params[1]);
            LoginResponse loginResponse = server.login(loginRequest);
            authToken = loginResponse.authToken();
            return String.format("You signed in as %s.", loginResponse.username());
        }
        else{
            throw new DataAccessException("Wrong number of parameters");
        }
    }
    public String register(String... params) throws DataAccessException{

    }
    public String logout(String... params) throws DataAccessException{

    }
    public String createGame(String... params) throws DataAccessException{

    }
    public String listGames(String... params) throws DataAccessException{

    }
    public String joinGame(String... params) throws DataAccessException{

    }
    public String observeGame(String... params) throws DataAccessException{

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
