package ui;

import java.util.Scanner;

import exceptions.DataAccessException;
import facades.NotificationHandler;
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
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_MAGENTA);
    }

    @Override
    public void notify(ServerMessage notification) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            return;
        }
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            GameDataPlayerColor obj = (GameDataPlayerColor) notification.gameData;
            var board = obj.getGameData();
            var playerColor = obj.getPlayerColor();


//            var drawBoard = new DrawChessBoard(board);
//            drawBoard.drawWhiteBoard();
            return;
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + notification.getServerMessageType() + " " + EscapeSequences.SET_TEXT_COLOR_BLUE + notification.message);
        printPrompt();
    }
}
