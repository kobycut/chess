package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import dataaccess.MySqlGameDataAccess;
import exceptions.DataAccessException;
import model.GameData;
import model.GameDataPlayerColor;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException, InvalidMoveException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getUsername(), session, command.getTeamColor(), command.getGameID());
            case MAKE_MOVE ->
                    makeMove(command.getUsername(), command.getMove(), command.getGameID(), command.getTeamColor());
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

    private void makeMove(String username, ChessMove move, Integer gameId, String playerColor) throws DataAccessException, InvalidMoveException, IOException {
        var db = new MySqlGameDataAccess();
        GameData gameData = db.getGame(gameId);
        ChessGame.TeamColor teamColor = ChessGame.TeamColor.BLACK;
        ChessGame.TeamColor oppColor = ChessGame.TeamColor.WHITE;
        var oppUsername = gameData.whiteUsername();
        if (playerColor.equals("WHITE")) {
            teamColor = ChessGame.TeamColor.WHITE;
            oppColor = ChessGame.TeamColor.BLACK;
            oppUsername = gameData.blackUsername();

        }

        var message = String.format("%s made move %s", username, move.toString());
        String stateMessage = null;

        gameData.chessGame().setTeamTurn(teamColor);
        gameData.chessGame().makeMove(move);
        db.updateGame(gameData, playerColor, username);
        loadGame(gameData, playerColor);

        if (gameData.chessGame().isInCheck(oppColor)) {
            stateMessage = String.format("%s is in check", oppUsername);
        }
        if (gameData.chessGame().isInCheckmate(teamColor)) {

            stateMessage = String.format("%s is in checkmate", oppUsername);
        }
        if (gameData.chessGame().isInStalemate(teamColor)) {
            stateMessage = "stalemate";
        }

        var loadGameNotification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message, new GameDataPlayerColor(gameData, playerColor));


        connections.broadcast(loadGameNotification, null, gameId);

        if (stateMessage != null) {
            var gameStateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, stateMessage, null);
            connections.broadcast(gameStateNotification, null, gameId);
        }
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


