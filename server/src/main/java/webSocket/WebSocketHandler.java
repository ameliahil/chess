package webSocket;

import chess.*;
import com.google.gson.Gson;
import dataAccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final SQLGameDAO gameDAO = new SQLGameDAO();
    private final SQLAuthDAO authDAO = new SQLAuthDAO();

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
        else if(command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
            MakeMoveCommand makeMoveCommand = new Gson().fromJson(message,MakeMoveCommand.class);
            makeMove(session, makeMoveCommand);
        }
        else if(command.getCommandType() == UserGameCommand.CommandType.LEAVE){
            LeaveCommand leaveCommand = new Gson().fromJson(message,LeaveCommand.class);
            leave(session,leaveCommand);
        }
        else if(command.getCommandType() == UserGameCommand.CommandType.RESIGN){
            ResignCommand resignCommand = new Gson().fromJson(message,ResignCommand.class);
            resign(session,resignCommand);
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
            String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Invalid Auth Token"));
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

        var message = String.format("%s has joined the game as observer", userName);
        GameData game = gameDAO.getGame(command.getGameID());

        inGame.setMap(inGame.findInGame(gameID, connections.getMap()));

        var loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);
        loadGame.setColor(ChessGame.TeamColor.WHITE);
        connection.send(new Gson().toJson(loadGame));
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        inGame.broadcast(userName, notification);
    }
    private void makeMove(Session session,MakeMoveCommand makeMoveCommand) throws DataAccessException, IOException {
        int gameID = makeMoveCommand.getGameID();
        ChessMove move = makeMoveCommand.getMove();
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        String userName = null;
        ChessGame.TeamColor color = null;
        try{
            userName = authDAO.getUser(makeMoveCommand.authToken);
        }catch (DataAccessException e){
            var connection = new Connection(null,gameID,session);
            String error = new Gson().toJson(new Error(ServerMessage.ServerMessageType.ERROR, "Error: Invalid Auth Token"));
            connection.send(error);
            return;
        }

        GameData game = gameDAO.getGame(gameID);
        ChessGame implementation = game.implementation();

        ConnectionManager inGame = new ConnectionManager();
        inGame.setMap(inGame.findInGame(gameID, connections.getMap()));

        String otherUserName = null;
        ChessGame.TeamColor otherColor = null;
        if(Objects.equals(game.whiteUsername(), userName)){
            color = ChessGame.TeamColor.WHITE;
            otherColor = ChessGame.TeamColor.BLACK;
            otherUserName = game.blackUsername();
        } else if (game.blackUsername().equals(userName)) {
            color = ChessGame.TeamColor.BLACK;
            otherColor = ChessGame.TeamColor.WHITE;
            otherUserName = game.whiteUsername();
        }
        if(!Objects.equals(color, game.implementation().getTeamTurn())){
            Error error = new Error(ServerMessage.ServerMessageType.ERROR, "Error: Invalid Auth Token");
            inGame.broadcastSolo(userName,error);
            return;
        }
        if(game.implementation().isGameOver()){
            Error error = new Error(ServerMessage.ServerMessageType.ERROR, "Error: Game is Over");
            inGame.broadcastSolo(userName,error);
            return;
        }

        try{implementation.makeMove(move);}
        catch (InvalidMoveException e) {
            Error error = new Error(ServerMessage.ServerMessageType.ERROR, "Error: Invalid Move. Why dont you learn the rules of chess before you hop on here buddy.");
            inGame.broadcastSolo(userName,error);
            return;
        }

        gameDAO.updateGame(gameID,implementation);

        char startCol = findCol(start.getColumn());
        char endCol = findCol(end.getColumn());

        ChessBoard board = game.implementation().getBoard();
        ChessPiece piece = board.getPiece(end);
        String blackUsername = game.blackUsername();


        var message = String.format("%s has moved %s from %s%s to %s%s", userName, piece.getPieceType(),startCol,start.getRow(),endCol,end.getRow());

        var loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);

        loadGame.setColor(ChessGame.TeamColor.WHITE);
        inGame.broadcast(blackUsername,loadGame);

        var loadGameBlack = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);

        loadGameBlack.setColor((ChessGame.TeamColor.BLACK));
        inGame.broadcastSolo(blackUsername,loadGameBlack);

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        inGame.broadcast(userName, notification);

        if(implementation.isInCheckmate(otherColor)){
            var checkNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            checkNotification.addMessage(String.format("%s is in checkmate",otherUserName));
            inGame.broadcast(null, checkNotification);
            implementation.setGameOver();
            gameDAO.updateGame(gameID,implementation);
        }
        else if(implementation.isInCheck(otherColor)){
            var checkNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            checkNotification.addMessage(String.format("%s is in check",otherUserName));
            inGame.broadcast(null, checkNotification);
        }
        else if(implementation.isInStalemate(otherColor)){
            var checkNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            checkNotification.addMessage(String.format("%s is in stalemate",otherUserName));
            inGame.broadcast(null, checkNotification);
            implementation.setGameOver();
            gameDAO.updateGame(gameID,implementation);
        }
    }


    private void leave(Session session, LeaveCommand leaveCommand) throws IOException, DataAccessException {
        connections.remove(leaveCommand.authToken);
        String username = authDAO.getUser(leaveCommand.authToken);

        var message = String.format("%s left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage(message);
        ConnectionManager inGame = new ConnectionManager();
        inGame.setMap(inGame.findInGame(leaveCommand.getGameID(), connections.getMap()));
        inGame.broadcast(username, notification);
    }
    private void resign(Session session, ResignCommand resignCommand) throws IOException, DataAccessException {
        String username = authDAO.getUser(resignCommand.authToken);
        GameData game = gameDAO.getGame(resignCommand.getGameID());
        ConnectionManager inGame = new ConnectionManager();
        inGame.setMap(inGame.findInGame(resignCommand.getGameID(), connections.getMap()));

        if(game.implementation().isGameOver()){
            var error = new Error(ServerMessage.ServerMessageType.ERROR,"Error: Game is Already Over");
            inGame.broadcastSolo(username,error);
            return;
        }

        if(username.equals(game.blackUsername()) || username.equals(game.whiteUsername())) {
            var message = String.format("%s resigned the game", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage(message);
            inGame.broadcast(null, notification);

            game.implementation().setGameOver();
            gameDAO.updateGame(resignCommand.getGameID(), game.implementation());
        }
        else{
            var error = new Error(ServerMessage.ServerMessageType.ERROR,"Error: Observer Can't Resign");
            inGame.broadcastSolo(username,error);
        }
    }

    private char findCol(int colInt){
            switch (colInt) {
                case 8 -> {return 'a';}
                case 7 -> {return 'b';}
                case 6 -> {return 'c';}
                case 5 -> {return 'd';}
                case 4 -> {return 'e';}
                case 3 -> {return 'f';}
                case 2 -> {return 'g';}
                case 1 -> {return 'h';}
            }
            return ' ';
        }

}
