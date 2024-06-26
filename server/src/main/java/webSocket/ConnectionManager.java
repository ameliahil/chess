package webSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocket.Connection;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String userName, Session session, String authToken, int gameID) {
        var connection = new Connection(userName, gameID, session);
        connections.put(authToken, connection);
    }
    public void setMap(ConcurrentHashMap<String, Connection> connections){
        this.connections = connections;
    }
    public ConcurrentHashMap<String, Connection> getMap(){
        return connections;
    }

    public void remove(String userName) {
        connections.remove(userName);
    }
    public ConcurrentHashMap<String,Connection> findInGame(int gameID, ConcurrentHashMap<String,Connection> allConnections){
        ConcurrentHashMap<String,Connection> inGame = new ConcurrentHashMap<>();
        for (Map.Entry<String, Connection> entry : allConnections.entrySet()) {
            Connection value = entry.getValue();
            if(value.gameID == gameID){
                inGame.put(entry.getKey(), entry.getValue());
            }
        }
        return inGame;
    }

    public void broadcast(String excludeUserName, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<String>();
        String json = new Gson().toJson(notification);
        for (Map.Entry<String,Connection> entry: connections.entrySet()){
            var c = entry.getValue();
            String key = entry.getKey();
            if (c.session.isOpen()) {
                if (!c.userName.equals(excludeUserName)) {
                    c.send(json);
                }
            } else {
                removeList.add(key);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c);
        }
    }
    public void broadcastSolo(String userName, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<String>();
        String json = new Gson().toJson(notification);
        for (Map.Entry<String,Connection> entry: connections.entrySet()){
            var c = entry.getValue();
            String key = entry.getKey();
            if (c.session.isOpen()) {
                if (c.userName.equals(userName)) {
                    c.send(json);
                }
            } else {
                removeList.add(key);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c);
        }
    }

}