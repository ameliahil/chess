package webSocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthService;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private SQLGameDAO gameDAO = new SQLGameDAO();
    private SQLAuthDAO authDAO = new SQLAuthDAO();
    private AuthService authService = new AuthService(authDAO);

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        if(command.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER){
            JoinPlayerCommand joinPlayerCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
            joinPlayer(session, joinPlayerCommand);
        }
        else if(command.getCommandType() == UserGameCommand.CommandType.JOIN_OBSERVER){
            JoinObserverCommand joinCommand = new Gson().fromJson(message, JoinObserverCommand.class);
            joinObserver(session, joinCommand);
        }
    }

    private void joinPlayer(Session session, JoinPlayerCommand command) throws IOException, DataAccessException {
        int gameID = command.getGameID();
        String userName = null;
        try{
            userName = authDAO.getUser(command.authToken);
        }
        catch (DataAccessException e){
            var connection = new Connection(null,gameID,session);
            String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Wrong game ID"));
            connection.send(error);
            return;
        }

        var connection = new Connection(userName,gameID,session);
        ConnectionManager inGame = new ConnectionManager();

        try{
            gameDAO.getGame(gameID);
        }catch(DataAccessException e){
            String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Wrong game ID"));
            connection.send(error);
        }
        var message = String.format("%s has joined the game as %s", userName, command.playerColor);
        GameData game = gameDAO.getGame(command.getGameID());

        if(command.playerColor == null){
            String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: No color"));
            connection.send(error);
            return;
        }
        if(command.playerColor == ChessGame.TeamColor.WHITE){
            if(!(Objects.equals(userName, game.whiteUsername()))){
                String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Spot already taken"));
                connection.send(error);
                return;
            }
        }
        if(command.playerColor == ChessGame.TeamColor.BLACK){
            if(!(Objects.equals(userName, game.blackUsername()))){
                String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Spot already taken"));
                connection.send(error);
                return;
            }
        }

        connections.add(userName, session, command.authToken, gameID);

        inGame.setMap(inGame.findInGame(gameID, connections.getMap()));

        var loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);
        loadGame.setColor(command.playerColor);
        connection.send(new Gson().toJson(loadGame));
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        inGame.broadcast(userName, notification);
    }

    private void joinObserver(Session session, JoinObserverCommand command) throws IOException, DataAccessException {
        int gameID = command.getGameID();
        String userName = null;
        try{
            userName = authDAO.getUser(command.authToken);
        }
        catch (DataAccessException e){
            String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Wrong game ID"));
            var connection = new Connection(null,gameID,session);
            connection.send(error);
            return;
        }
        ConnectionManager inGame = new ConnectionManager();
        var connection = new Connection(userName,gameID,session);

        try{
            gameDAO.getGame(gameID);
        }catch(DataAccessException e){
            String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Wrong game ID"));
            connection.send(error);
        }

        connections.add(userName, session, command.authToken, gameID);

        var message = String.format("%s has joined the game as %s", userName, command.playerColor);
        GameData game = gameDAO.getGame(command.getGameID());

        inGame.setMap(inGame.findInGame(gameID, connections.getMap()));

        var loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);
        loadGame.setColor(ChessGame.TeamColor.WHITE);
        connection.send(new Gson().toJson(loadGame));
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        inGame.broadcast(userName, notification);
    }

    /*private void exit(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }*/
}
