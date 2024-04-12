package webSocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private SQLGameDAO gameDAO = new SQLGameDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        if(command.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER){
            JoinPlayerCommand joinPlayerCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
            joinPlayer(session, joinPlayerCommand);
        }
    }

    private void joinPlayer(Session session, JoinPlayerCommand command) throws IOException, DataAccessException {
        var connection = new Connection(command.userName,session);

        var message = String.format("%s has joined the game as %s", command.userName, command.teamColor);
        GameData game = gameDAO.getGame(command.getGameID());

        if(command.teamColor == ChessGame.TeamColor.WHITE){
            if(!(Objects.equals(command.userName, game.whiteUsername()))){
                String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Spot already taken"));
                connection.send(error);
            }
        }
        if(command.teamColor == ChessGame.TeamColor.BLACK){
            if(!(Objects.equals(command.userName, game.blackUsername()))){
                String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Spot already taken"));
                connection.send(error);
            }
        }

        connections.add(command.userName, session, command.authToken);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        connections.broadcast(null, notification);
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
