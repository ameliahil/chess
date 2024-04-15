package webSocket;

import UI.ChessClient;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.GameData;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

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
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message,ServerMessage.class);

                    if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
                        Error object = new Gson().fromJson(message,Error.class);
                        if(object.getMessage() == null){
                            System.out.println("Error");
                        }else {
                            System.out.println(object.getMessage());
                        }
                    }
                    else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
                        ServerMessage object = new Gson().fromJson(message,ServerMessage.class);
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

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor color) throws DataAccessException {
        try {
            JoinPlayerCommand command = new JoinPlayerCommand(authToken,gameID,color);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    public void joinObserve(String authToken, int gameID) throws DataAccessException {
        try {
            JoinObserverCommand command = new JoinObserverCommand(authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    public void makeMove(String authToken, int gameID, ChessMove move) throws DataAccessException {
        try{
            MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText((new Gson().toJson(command)));
        } catch (IOException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws DataAccessException {
        try {
            var command = new LeaveCommand(authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.session.close();
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
    public void resign(String authToken, int gameID) throws DataAccessException {
        try {
            var command = new ResignCommand(authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

}