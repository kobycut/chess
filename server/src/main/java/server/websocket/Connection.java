package server.websocket;

import chess.ChessBoard;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String visitorName;
    public Session session;
    public ChessBoard board;

    public Connection(String visitorName, Session session, ChessBoard board) {
        this.visitorName = visitorName;
        this.session = session;
        this.board = board;
    }

    public void send(String msg) throws IOException {

//            System.out.println("Sesh open" + msg);
        session.getRemote().sendString(msg);


    }
}
