package webSocket;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws DataAccessException {
        try {
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
                        //System.out.println(object.getMessage());
                    }
                    else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
                        Notification object = new Gson().fromJson(message,Notification.class);
                        //System.out.println(object.getMessage);
                    }
                    else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){

                    }

                    System.out.println(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enterPetShop(String visitorName) throws DataAccessException {
        try {
            var action = new UserGameCommand(visitorName);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
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