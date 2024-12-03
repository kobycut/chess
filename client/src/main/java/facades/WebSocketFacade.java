package facades;

import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.DataAccessException;
import ui.ChessClient;
import ui.DrawChessBoard;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }



    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
    public void joinGame(String username, String teamColor, Integer gameId, String authToken) throws DataAccessException {
       try {
           var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId, username, teamColor, null);
           this.session.getBasicRemote().sendText(new Gson().toJson(command));
       } catch (Exception ex) {
           throw new DataAccessException(500, "could not join game");
       }

    }
    public void leaveGame(String username, Integer gameId, String teamColor) throws DataAccessException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, null, gameId, username, teamColor, null);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new DataAccessException(500, "could not leave game");
        }
    }
    public void resign(String username) throws DataAccessException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, null, null, username, null, null);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new DataAccessException(500, "could not resign game");
        }
    }
    public void makeMove(String username, ChessMove move, Integer gameId) throws DataAccessException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, null, gameId, username, null, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }catch (Exception ex) {
            throw new DataAccessException(500, "could not make move");
        }
    }
}
