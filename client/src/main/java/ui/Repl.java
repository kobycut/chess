package ui;
import java.util.Scanner;

import facades.NotificationHandler;
import ui.EscapeSequences.*;
import websocket.messages.ServerMessage;

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
        System.out.println(notification.getServerMessageType()); // may not give message
        printPrompt();
    }
}
