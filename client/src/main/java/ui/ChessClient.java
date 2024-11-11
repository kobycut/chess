package ui;

import java.util.Arrays;

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private String username = null;


    public ChessClient() {

    }
    public String eval(String input) {
        try {
            var cmd = "help";
            var tokens = input.toLowerCase().toLowerCase().split(" ");
            if (tokens.length > 0) {
               cmd = tokens[0];
            }
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> quit();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help() {

    }

    public String quit() {

    }

    public String login() {

    }

    public String register() {

    }

    public String logout() {

    }

    public String createGame() {

    }

    public String listGames() {

    }

    public String playGames() {
    }

    public String observeGame() {
    }

}

