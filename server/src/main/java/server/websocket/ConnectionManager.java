package server.websocket;

import chess.ChessBoard;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session, ChessBoard board, Integer gameId) {
        var connection = new Connection(username, session, board, gameId);
        connections.put(username, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(ServerMessage notification, String excludeUser, Integer gameId) throws IOException {
        var removeList = new ArrayList<Connection>();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!Objects.equals(c.visitorName, excludeUser) && Objects.equals(c.gameId, gameId)) {

                    c.send(notification.toString());

                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void broadcastLoad(ServerMessage notification, String includeUser) throws IOException {
        var removeList = new ArrayList<Connection>();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (Objects.equals(c.visitorName, includeUser)) {
                    c.send(notification.toString());
                    break;
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void broadcastSession(ServerMessage notification, Session session) throws IOException {
        var removeList = new ArrayList<Connection>();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.session == session) {
                    c.send(notification.toString());
                    break;
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }
}
