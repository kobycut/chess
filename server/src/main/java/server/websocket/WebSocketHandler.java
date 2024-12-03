package server.websocket;

import com.google.gson.Gson;
import dataaccess.GameDAO;
import dataaccess.MySqlGameDataAccess;
import exceptions.DataAccessException;
import model.GameData;
import model.GameDataPlayerColor;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getUsername(), session, command.getTeamColor(), command.getGameID());
            case MAKE_MOVE -> makeMove(command.getUsername(), command.getMove(), command.getGameID());
            case LEAVE -> leave(command.getUsername(), session, command.getGameID(), command.getTeamColor());
            case RESIGN -> resign(command.getUsername(), session);
        }
    }

    private void connect(String username, Session session, String teamColor, Integer gameId) throws IOException, DataAccessException {
        connections.add(username, session, null, gameId);
        var message = String.format("%s joined the game as %s team", username, teamColor);

        if (teamColor.equals("Observer")) {
            message = String.format("%s joined the game as an observer", username);
        }
        var db = new MySqlGameDataAccess();
        GameData gameData = db.getGame(gameId);
        var lst = new ArrayList<String>();
        lst.add(gameData.whiteUsername());
        lst.add(gameData.blackUsername());
        if (!lst.contains(username)) {
            lst.add(username);
        }

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
        lst.removeIf(Objects::isNull);
//        lst.remove(username);
        connections.broadcast(notification, username, gameId);
    }

    private void makeMove() {
    }

    private void leave(String username, Session session, Integer gameId, String playerColor) throws IOException, DataAccessException {
        connections.remove(username);
        var message = String.format("%s left the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
        //  update database
        var db = new MySqlGameDataAccess();
        GameData gameData = db.getGame(gameId);

        db.updateGame(gameData, playerColor, null);
        var lst = new ArrayList<String>();
        lst.add(gameData.whiteUsername());
        lst.add(gameData.blackUsername());
        lst.removeIf(Objects::isNull);
        connections.broadcast(notification, username, gameId);
    }

    private void resign(String username, Session session) throws IOException {
        connections.remove(username);
        var message = String.format("%s resigned", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
//        connections.broadcast(notification);
    }

    public void loadGame(GameData gameData, String playerColor) throws IOException {
        var gameDataPlayerColor = new GameDataPlayerColor(gameData, playerColor);
        var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, gameDataPlayerColor);

        var username = gameData.blackUsername();
        var lst = new ArrayList<String>();
        if (Objects.equals(playerColor, "WHITE")) {
            lst.add(gameData.whiteUsername());
            username = gameData.whiteUsername();
        } else {
            lst.add(gameData.blackUsername());
        }
        connections.broadcastLoad(loadGame, username);

    }

}


