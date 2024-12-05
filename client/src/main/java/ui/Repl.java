package ui;

import java.util.Objects;
import java.util.Scanner;

import chess.ChessBoard;
import com.google.gson.Gson;
import exceptions.DataAccessException;
import facades.NotificationHandler;
import model.GameData;
import model.GameDataPlayerColor;
import ui.EscapeSequences.*;
import websocket.messages.ServerMessage;

import javax.xml.crypto.Data;

public class Repl implements NotificationHandler {
    private final ChessClient client;


    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess! Sign in to begin. ♕");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (!Objects.equals(result, "NONE")) {
                    System.out.print(result);
                }
            } catch (Throwable e) {
                var msg = e.toString();

                System.out.print(msg);

            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + ">>> " +
                EscapeSequences.SET_TEXT_COLOR_MAGENTA);
    }

    @Override
    public void notify(ServerMessage notification) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            return;
        }
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            System.out.println("\n");
            GameData obj = notification.game;
            var board = obj.chessGame().getBoard();
            client.chessBoard = board;
            var playerColor = client.teamColor;
            var drawBoard = new DrawChessBoard(board, null, null);
            if (playerColor == null) {
                drawBoard.drawWhiteBoard();
            }
            if (playerColor.equals("WHITE")) {
                drawBoard.drawWhiteBoard();

            } else if (playerColor.equals("BLACK")) {
                drawBoard.drawBlackBoard();

            } else {
                drawBoard.drawWhiteBoard();
            }

            printPrompt();
            return;
        }
        if (notification.message == null && notification.errorMessage != null) {
            notification.message = notification.errorMessage;
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + notification.getServerMessageType() + " " +
                EscapeSequences.SET_TEXT_COLOR_BLUE + notification.message);
        printPrompt();
    }
}
