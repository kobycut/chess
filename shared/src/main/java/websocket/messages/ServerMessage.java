package websocket.messages;

import chess.ChessBoard;
import com.google.gson.Gson;
import model.GameData;
import model.GameDataPlayerColor;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    public String message;
    public GameDataPlayerColor gameData;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message, GameDataPlayerColor gameData) {
        this.serverMessageType = type;
        this.message = message;
        this.gameData = gameData;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    public String toString() {
        return new Gson().toJson(this);
    }


    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}