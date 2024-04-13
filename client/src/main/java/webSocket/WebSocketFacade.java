package webSocket;

import UI.ChessClient;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessException;
import model.GameData;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    ChessClient client;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws DataAccessException {
        try {
            this.client = new ChessClient(url, notificationHandler);
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @OnMessage
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message,ServerMessage.class);

                    if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
                        Error object = new Gson().fromJson(message,Error.class);
                        System.out.println(object.getMessage());
                    }
                    else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
                        Notification object = new Gson().fromJson(message,Notification.class);
                        System.out.println(object.getMessage());
                    }
                    else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
                        LoadGame object = new Gson().fromJson(message,LoadGame.class);
                        GameData game = object.getGameData();
                        client.printBoard(object.getColor(),game);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendCommand(UserGameCommand command) {
        try{
            String json = new Gson().toJson(command,UserGameCommand.class);
            this.session.getBasicRemote().sendObject(json);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor color) throws DataAccessException {
        try {
            JoinPlayerCommand command = new JoinPlayerCommand(authToken,gameID,color);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void leavePetShop(String visitorName) throws DataAccessException {
        try {
            var action = new UserGameCommand(visitorName);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

}