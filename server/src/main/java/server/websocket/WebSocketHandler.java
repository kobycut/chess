package server.websocket;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getUsername(), session, command.getTeamColor());
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave(command.getUsername(), session);
            case RESIGN -> resign(command.getUsername(), session);
        }
    }

    private void connect(String username, Session session, String teamColor) throws IOException {
        connections.add(username, session);
        var message = String.format("%s joined the game as %s team", username, teamColor);

        if (teamColor.equals("Observer")) {
            message = String.format("%s joined the game as an observer", username);
        }

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

        connections.broadcast(notification);
    }
    private void makeMove() {}
    private void leave(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(notification);
    }
    private void resign(String username, Session session) throws IOException {
        connections.add(username, session);
        var message = String.format("%s resigned", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(notification);
    }


//    public void joined(String username, String playerColor) throws DataAccessException {
//        try {
//            var message = String.format("%s joined the game as %s team", username, playerColor);
//            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
//            connections.broadcast(notification);
//        } catch (Exception ex) {
//            throw new DataAccessException(500, "Could not join the game");
//        }
//
//    }
}
