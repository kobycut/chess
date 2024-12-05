package server.websocket;

import chess.ChessGame;
import chess.ChessMove;

import chess.InvalidMoveException;
import com.google.gson.Gson;

import dataaccess.MySqlAuthDataAccess;
import dataaccess.MySqlGameDataAccess;

import exceptions.DataAccessException;
import model.AuthData;
import model.GameData;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException,
            InvalidMoveException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getUsername(), session, command.getGameID(), command.getAuthToken());
            case MAKE_MOVE ->
                    makeMove(command.getUsername(), command.getMove(), command.getGameID(), command.getTeamColor(),
                            command.getMoveString(), command.getAuthToken(), session);
            case LEAVE -> leave(command.getUsername(), session, command.getGameID(), command.getTeamColor(),
                    command.getAuthToken());
            case RESIGN -> resign(command.getUsername(), session, command.getGameID(), command.getAuthToken());
        }
    }

    private void connect(String username, Session session, Integer gameId, String authToken) throws IOException,
            DataAccessException {
        try {
            var gamedb = new MySqlGameDataAccess();
            var gameData = gamedb.getGame(gameId);
            String teamColor = "OBSERVER";
            if (username == null) {
                var authdb = new MySqlAuthDataAccess();
                var authData = authdb.getAuthData(authToken);
                username = authData.username();


            }
            if (Objects.equals(gameData.blackUsername(), username)) {
                teamColor = "BLACK";
            } else if (Objects.equals(gameData.whiteUsername(), username)) {
                teamColor = "WHITE";
            }

            connections.add(username, session, null, gameId);

            var message = String.format("%s joined the game as %s team", username, teamColor);
            if (teamColor.equals("OBSERVER")) {
                message = String.format("%s joined the game as an observer", username);
            }

            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message,
                    null, null);
            loadGame(gameData, username);
            connections.broadcast(notification, username, gameId);

        } catch (Exception ex) {
            var errorMessage = "could not connect, provide correct information";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,
                    null, errorMessage);

            session.getRemote().sendString(new Gson().toJson(errorNotification));
        }
    }

    private void makeMove(String username, ChessMove move, Integer gameId, String playerColor, String moveString,
                          String authToken, Session session) throws DataAccessException, InvalidMoveException,
            IOException {
        try {
            var db = new MySqlGameDataAccess();
            var authdb = new MySqlAuthDataAccess();
            GameData gameData = db.getGame(gameId);
            var authData = authdb.getAuthData(authToken);
            if (authData == null) {
                throw new DataAccessException(400, "bad auth token");
            }
            if (username == null) {
                username = authData.username();
            }
            if (gameData.chessGame().getTeamTurn() == ChessGame.TeamColor.OVER) {
                throw new DataAccessException(400, "cannot move when game is over");
            }
            playerColor = "BLACK";
            if (Objects.equals(username, gameData.whiteUsername())) {
                playerColor = "WHITE";
            }
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.BLACK;
            ChessGame.TeamColor oppColor = ChessGame.TeamColor.WHITE;
            var oppUsername = gameData.whiteUsername();
            if (playerColor.equals("WHITE")) {
                teamColor = ChessGame.TeamColor.WHITE;
                oppColor = ChessGame.TeamColor.BLACK;
                oppUsername = gameData.blackUsername();
            }
            String stateMessage = null;
            String winMessage = null;
            if (gameData.chessGame().getBoard().getPiece(move.getStartPosition()) == null) {
                throw new DataAccessException(400, "invalid move, no piece at that location");
            }
            var pieceColor = gameData.chessGame().getBoard().getPiece(move.getStartPosition()).getTeamColor();
            if (teamColor != pieceColor) {
                throw new DataAccessException(400, "move was not valid");
            }
            gameData.chessGame().makeMove(move);
            db.updateGame(gameData, playerColor, username);
            var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, gameData,
                    null);
            connections.broadcast(loadGame, null, gameId);
            boolean imreallytired = true;
            if (gameData.chessGame().isInStalemate(oppColor)) {
                stateMessage = "stalemate";
                gameData.chessGame().setTeamTurn(ChessGame.TeamColor.OVER);
                db.updateGame(gameData, "WHITE", username);
                imreallytired = false;

            }
            if (imreallytired == true) {
                if (gameData.chessGame().isInCheckmate(oppColor)) {
                    stateMessage = String.format("%s is in checkmate", oppUsername);
                    gameData.chessGame().setTeamTurn(ChessGame.TeamColor.OVER);
                    db.updateGame(gameData, "WHITE", username);
                    imreallytired = false;
                }
            }
            if (imreallytired == true) {
                if (gameData.chessGame().isInCheck(oppColor)) {
                    stateMessage = String.format("%s is in check", oppUsername);
                }
            }


            char firstChar = getCol(move.getStartPosition().getColumn());
            char secondChar = getRow(move.getStartPosition().getRow());
            char thirdChar = getCol(move.getEndPosition().getColumn());
            char fourthChar = getRow(move.getEndPosition().getRow());
            moveString = "" + firstChar + secondChar + " to " + thirdChar + fourthChar;
            var message = String.format("%s moved %s", username, moveString);
            var moveNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message,
                    null, null);
            connections.broadcast(moveNotification, username, gameId);
            gameData.chessGame().setTeamTurn(oppColor);
            if (stateMessage != null) {
                var gameStateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        stateMessage, null, null);
                connections.broadcast(gameStateNotification, null, gameId);
            }
            if (winMessage != null) {
                var winStateNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, winMessage,
                        null, null);
                connections.broadcast(winStateNotification, null, gameId);
            }
        } catch (Exception ex) {
            var errorMessage = "please enter a valid move";
            if (ex.getMessage() != null) {
                errorMessage = ex.getMessage();
            }
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null,
                    null, errorMessage);
            if (Objects.equals(errorMessage, "bad auth token")) {
                connections.broadcastSession(errorNotification, session);
            }
            connections.broadcastLoad(errorNotification, username);
        }
    }

    private void leave(String username, Session session, Integer gameId, String playerColor, String authToken)
            throws IOException, DataAccessException {
        try {
            var authdb = new MySqlAuthDataAccess();
            if (username == null) {
                var authData = authdb.getAuthData(authToken);
                username = authData.username();
            }
            connections.remove(username);
            var message = String.format("%s left the game", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message,
                    null, null);
            var db = new MySqlGameDataAccess();
            GameData gameData = db.getGame(gameId);
            playerColor = "WHITE";

            if (Objects.equals(gameData.blackUsername(), username)) {
                playerColor = "BLACK";
            }
            db.updateGame(gameData, playerColor, null);
            connections.broadcast(notification, username, gameId);
        } catch (Exception ex) {
            var errorMessage = "cannot leave";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage,
                    null, errorMessage);
            connections.broadcastLoad(errorNotification, username);
        }
    }

    private void resign(String username, Session session, Integer gameId, String authToken) throws IOException {
        try {
            var db = new MySqlGameDataAccess();
            var authdb = new MySqlAuthDataAccess();
            GameData gameData = db.getGame(gameId);
            AuthData authData = authdb.getAuthData(authToken);
            username = authData.username();
            if (!Objects.equals(username, gameData.whiteUsername()) &&
                    !Objects.equals(username, gameData.blackUsername())) {
                throw new DataAccessException(400, "not allowed to resign, not a player in game");
            }
            if (gameData.chessGame().getTeamTurn() == ChessGame.TeamColor.OVER) {
                throw new DataAccessException(400, "not allowed to resign, game already over");
            }

            var message = String.format("%s resigned", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message,
                    null, null);
            gameData.chessGame().setTeamTurn(ChessGame.TeamColor.OVER);
            db.updateGame(gameData, "WHITE", username);
            connections.broadcast(notification, null, gameId);
        } catch (Exception ex) {
            var errorMessage = "could not resign";
            if (ex.getMessage() != null) {
                errorMessage = ex.getMessage();
            }
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    null, null, errorMessage);
            connections.broadcastLoad(errorNotification, username);
        }
    }

    public void loadGame(GameData gameData, String username) throws IOException {
        try {
            var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    null, gameData, null);

            connections.broadcastLoad(loadGame, username);
        } catch (Exception ex) {

            var errorMessage = "could not load game";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    errorMessage, null, errorMessage);

            connections.broadcastLoad(errorNotification, username);
        }
    }



    private char getCol(Integer num) {
        char col = 'a';
        switch (num) {
            case 2 -> {
                col = 'b';
            }
            case 3 -> {
                col = 'c';
            }
            case 4 -> {
                col = 'd';
            }
            case 5 -> {
                col = 'e';
            }
            case 6 -> {
                col = 'f';
            }
            case 7 -> {
                col = 'g';
            }
            case 8 -> {
                col = 'h';
            }
        }

        return col;
    }
    private char getRow(Integer num) {
        char col = '1';
        switch (num) {
            case 2 -> {
                col = '2';
            }
            case 3 -> {
                col = '3';
            }
            case 4 -> {
                col = '4';
            }
            case 5 -> {
                col = '5';
            }
            case 6 -> {
                col = '6';
            }
            case 7 -> {
                col = '7';
            }
            case 8 -> {
                col = '8';
            }
        }

        return col;
    }

}


