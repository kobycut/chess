package server.websocket;

import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import exceptions.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getUsername(), session, command.getTeamColor());
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void connect(String username, Session session, String teamColor) throws IOException {
        connections.add(username, session);
        var message = String.format("%s joined the game as %s team", username, teamColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notification);
    }
    private void makeMove() {}
    private void leave() {}
    private void resign() {}
    public void joined(String username, String playerColor) throws DataAccessException {
        try {
            var message = String.format("%s joined the game as %s team", username, playerColor);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new DataAccessException(500, "Could not join the game");
        }

    }
}
