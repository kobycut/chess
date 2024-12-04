package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import dataaccess.MySqlAuthDataAccess;
import dataaccess.MySqlGameDataAccess;
import dataaccess.MySqlUserDataAccess;
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
        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            System.out.println("recieved connect command");
        }
        switch (command.getCommandType()) {
            case CONNECT ->
                    connect(command.getUsername(), session, command.getTeamColor(), command.getGameID(), command.getAuthToken());
            case MAKE_MOVE ->
                    makeMove(command.getUsername(), command.getMove(), command.getGameID(), command.getTeamColor(), command.getMoveString());
            case LEAVE -> leave(command.getUsername(), session, command.getGameID(), command.getTeamColor());
            case RESIGN -> resign(command.getUsername(), session, command.getGameID());
        }
    }

    private void connect(String username, Session session, String teamColor, Integer gameId, String authToken) throws IOException, DataAccessException {
        try {
            var gamedb = new MySqlGameDataAccess();
            var gameData = gamedb.getGame(gameId);
            if (username == null) {
                var authdb = new MySqlAuthDataAccess();
                var authData = authdb.getAuthData(authToken);
                username = authData.username();
            }
            if (teamColor == null) {

                if (Objects.equals(username, gameData.blackUsername())) {
                    teamColor = "BLACK";
                } else if (Objects.equals(username, gameData.whiteUsername())) {
                    teamColor = "WHITE";
                } else {
                    teamColor = "Observer";
                }
            }

            connections.add(username, session, null, gameId);

            var message = String.format("%s joined the game as %s team", username, teamColor);

            if (teamColor.equals("Observer")) {
                message = String.format("%s joined the game as an observer", username);
            }


            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);

            connections.broadcast(notification, username, gameId);
//            if (!teamColor.equals("Observer")) {
            loadGame(gameData, teamColor);
//            }
        } catch (Exception ex) {
            var errorMessage = "could not connect, provide correct information";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, null, errorMessage);

            session.getRemote().sendString(new Gson().toJson(errorNotification));
        }
    }

    private void makeMove(String username, ChessMove move, Integer gameId, String playerColor, String moveString) throws DataAccessException, InvalidMoveException, IOException {
        try {
            var db = new MySqlGameDataAccess();
            GameData gameData = db.getGame(gameId);
            if (gameData.chessGame().getTeamTurn() == ChessGame.TeamColor.OVER) {
                throw new DataAccessException(400, "cannot move when game is over");
            }
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.BLACK;
            ChessGame.TeamColor oppColor = ChessGame.TeamColor.WHITE;
            var oppUsername = gameData.whiteUsername();
            if (playerColor.equals("WHITE")) {
                teamColor = ChessGame.TeamColor.WHITE;
                oppColor = ChessGame.TeamColor.BLACK;
                oppUsername = gameData.blackUsername();
            }
            var message = String.format("%s moved %s", username, moveString);
            String stateMessage = null;
            String winMessage = null;
            var pieceColor = gameData.chessGame().getBoard().getPiece(move.getStartPosition()).getTeamColor();
            if (teamColor == pieceColor) {
                throw new DataAccessException(400, "move was not valid");
            }
            gameData.chessGame().makeMove(move);
            db.updateGame(gameData, playerColor, username);
            loadGame(gameData, playerColor);

            if (gameData.chessGame().isInCheck(oppColor)) {
                stateMessage = String.format("%s is in check", oppUsername);
            }
            if (gameData.chessGame().isInCheckmate(teamColor)) {
                stateMessage = String.format("%s is in checkmate", oppUsername);
                winMessage = String.format("%s WON!", username);
                gameData.chessGame().setTeamTurn(ChessGame.TeamColor.OVER);
            }
            if (gameData.chessGame().isInStalemate(teamColor)) {
                stateMessage = "stalemate";
                winMessage = "STALEMATE";
            }

            var loadGameNotification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, message, new GameDataPlayerColor(gameData, playerColor), null);
            var moveNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);

            connections.broadcast(loadGameNotification, username, gameId);
            connections.broadcast(moveNotification, username, gameId);
            gameData.chessGame().setTeamTurn(oppColor);
            if (stateMessage != null) {
                var gameStateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, stateMessage, null, null);
                connections.broadcast(gameStateNotification, null, gameId);
            }
            if (winMessage != null) {
                var winStateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, winMessage, null, null);
                connections.broadcast(winStateNotification, null, gameId);
            }
        } catch (Exception ex) {

            var errorMessage = "please enter a valid move";
            if (ex.getMessage() != null) {
                errorMessage = ex.getMessage();
            }
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            connections.broadcastLoad(errorNotification, username);
        }
    }

    private void leave(String username, Session session, Integer gameId, String playerColor) throws IOException, DataAccessException {
        try {
            connections.remove(username);
            var message = String.format("%s left the game", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
            var db = new MySqlGameDataAccess();
            GameData gameData = db.getGame(gameId);

            db.updateGame(gameData, playerColor, null);
            var lst = new ArrayList<String>();
            lst.add(gameData.whiteUsername());
            lst.add(gameData.blackUsername());
            lst.removeIf(Objects::isNull);
            connections.broadcast(notification, username, gameId);
        } catch (Exception ex) {
            var errorMessage = "cannot leave";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, errorMessage);
            connections.broadcastLoad(errorNotification, username);
        }
    }

    private void resign(String username, Session session, Integer gameId) throws IOException {
        try {
            var db = new MySqlGameDataAccess();
            GameData gameData = db.getGame(gameId);
            var message = String.format("%s resigned", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
            gameData.chessGame().setTeamTurn(ChessGame.TeamColor.OVER);
            connections.broadcast(notification, username, gameId);
        } catch (Exception ex) {
            var errorMessage = "could not resign";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
            connections.broadcastLoad(errorNotification, username);
        }
    }

    public void loadGame(GameData gameData, String playerColor) throws IOException {
        try {
            var gameDataPlayerColor = new GameDataPlayerColor(gameData, playerColor);
            var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, gameDataPlayerColor, null);

            var username = gameData.blackUsername();

            if (Objects.equals(playerColor, "WHITE")) {

                username = gameData.whiteUsername();
            }
            connections.broadcastLoad(loadGame, username);
        } catch (Exception ex) {
            var username = gameData.blackUsername();
            if (Objects.equals(playerColor, "WHITE")) {
                username = gameData.whiteUsername();
            }

            var errorMessage = "could not load game";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, errorMessage);

            connections.broadcastLoad(errorNotification, username);
        }
    }

}


