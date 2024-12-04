package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import dataaccess.MySqlAuthDataAccess;
import dataaccess.MySqlGameDataAccess;
import dataaccess.MySqlUserDataAccess;
import exceptions.DataAccessException;
import model.AuthData;
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

        }
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getUsername(), session, command.getGameID(), command.getAuthToken());
            case MAKE_MOVE ->
                    makeMove(command.getUsername(), command.getMove(), command.getGameID(), command.getTeamColor(), command.getMoveString(), command.getAuthToken());
            case LEAVE ->
                    leave(command.getUsername(), session, command.getGameID(), command.getTeamColor(), command.getAuthToken());
            case RESIGN -> resign(command.getUsername(), session, command.getGameID(), command.getAuthToken());
        }
    }

    private void connect(String username, Session session, Integer gameId, String authToken) throws IOException, DataAccessException {
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

            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
            loadGame(gameData, username);
            connections.broadcast(notification, username, gameId);

        } catch (Exception ex) {
            var errorMessage = "could not connect, provide correct information";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, null, errorMessage);

            session.getRemote().sendString(new Gson().toJson(errorNotification));
        }
    }

    private void makeMove(String username, ChessMove move, Integer gameId, String playerColor, String moveString, String authToken) throws DataAccessException, InvalidMoveException, IOException {
        try {
            var db = new MySqlGameDataAccess();
            var authdb = new MySqlAuthDataAccess();
            GameData gameData = db.getGame(gameId);
            if (gameData.chessGame().getTeamTurn() == ChessGame.TeamColor.OVER) {
                throw new DataAccessException(400, "cannot move when game is over");
            }
            if (username == null) {
                var authData = authdb.getAuthData(authToken);
                username = authData.username();
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

            var startRow = getRow(move.getStartPosition().getRow());
            var endRow = getRow(move.getEndPosition().getRow());
            move = new ChessMove(new ChessPosition(startRow, move.getStartPosition().getColumn()), new ChessPosition(endRow, move.getEndPosition().getColumn()), null);
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


            char firstChar = getCol(move.getStartPosition().getRow());
            String secondChar = Integer.toString(move.getStartPosition().getColumn());
            char thirdChar = getCol(move.getEndPosition().getRow());
            String fourthChar = Integer.toString(move.getEndPosition().getColumn());

            moveString = firstChar + secondChar + " to " + thirdChar + fourthChar;

            var message = String.format("%s moved %s", username, moveString);

            var moveNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);

//            connections.broadcast(loadGameNotification, username, gameId);
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

    private void leave(String username, Session session, Integer gameId, String playerColor, String authToken) throws IOException, DataAccessException {
        try {
            var authdb = new MySqlAuthDataAccess();
            if (username == null) {
                var authData = authdb.getAuthData(authToken);
                username = authData.username();
            }
            connections.remove(username);
            var message = String.format("%s left the game", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
            var db = new MySqlGameDataAccess();
            GameData gameData = db.getGame(gameId);
            playerColor = "WHITE";

            if (Objects.equals(gameData.blackUsername(), username)) {
                playerColor = "BLACK";
            }


            db.updateGame(gameData, playerColor, null);
            GameData gameData1 = db.getGame(gameId);
            connections.broadcast(notification, username, gameId);
        } catch (Exception ex) {
            var errorMessage = "cannot leave";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, errorMessage);
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
            if (!Objects.equals(username, gameData.whiteUsername()) && !Objects.equals(username, gameData.blackUsername())) {
                throw new DataAccessException(400, "not allowed to resign, not a player in game");
            }
            if (gameData.chessGame().getTeamTurn() == ChessGame.TeamColor.OVER) {
                throw new DataAccessException(400, "not allowed to resign, game already over");
            }

            var message = String.format("%s resigned", username);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null, null);
            gameData.chessGame().setTeamTurn(ChessGame.TeamColor.OVER);
            db.updateGame(gameData, "WHITE", username);
            connections.broadcast(notification, null, gameId);
        } catch (Exception ex) {
            var errorMessage = "could not resign";
            if (ex.getMessage() != null) {
                errorMessage = ex.getMessage();
            }
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, null, errorMessage);
            connections.broadcastLoad(errorNotification, username);
        }
    }

    public void loadGame(GameData gameData, String username) throws IOException {
        try {
//            var gameDataPlayerColor = new GameDataPlayerColor(gameData, playerColor);
            var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, gameData, null);

//            var username = gameData.blackUsername();

//            if (Objects.equals(playerColor, "WHITE")) {
//
//                username = gameData.whiteUsername();
//            }
            connections.broadcastLoad(loadGame, username);
        } catch (Exception ex) {
//            var username = gameData.blackUsername();
//            if (Objects.equals(playerColor, "WHITE")) {
//                username = gameData.whiteUsername();
//            }

            var errorMessage = "could not load game";
            var errorNotification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, errorMessage);

            connections.broadcastLoad(errorNotification, username);
        }
    }

    private Integer getRow(Integer num) {
        int row = 1;
        switch (num) {
            case 7 -> {
                row = 2;
            }
            case 6 -> {
                row = 3;
            }
            case 5 -> {
                row = 4;
            }
            case 4 -> {
                row = 5;
            }
            case 3 -> {
                row = 6;
            }
            case 2 -> {
                row = 7;
            }
            case 1 -> {
                row = 8;
            }
        }
        ;
        return row;
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
        ;
        return col;
    }

}


