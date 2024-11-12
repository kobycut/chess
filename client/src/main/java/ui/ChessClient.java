package ui;

import Facades.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private String username = null;
    private final ServerFacade server;
    private final String serverUrl;


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;

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
                case "login" -> login(params);
                case "register" -> register(params);
                case "createGame" -> createGame(params);
                case "listGames" -> listGames();
                case "playGame" -> playGame(params);
                case "observeGame" -> observeGame(params);
                case "logout" -> logout();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
                    """;
        }
        return """
                - createGame <GAME NAME>
                - listGames
                - playGame <ID> [WHITE|BLACK]
                - observeGame <GAME ID>
                - logout
                """;
    }

    public String quit() {
        return "quit";
    }

    public String login(String... params) {
        if (params.length > 1) {
            state = State.SIGNEDIN;
            username = String.join("-", params);

        }
        return "";
    }

    public String register(String... params) {
        if (params.length >= 2) {
            state = State.SIGNEDIN;
            username = String.join("-", params);



            return String.format("You signed in as %s.", username);
        }
//        throw new Exception(400,)
        return "";
    }

    public String logout() {
        checkSignedIn();
        return "";
    }

    public String createGame(String... params) {
        checkSignedIn();
        return "";
    }

    public String listGames() {
        checkSignedIn();
        return "";
    }

    public String playGame(String... params) {
        checkSignedIn();
        return "";
    }

    public String observeGame(String... params) {
        checkSignedIn();
        return "";
    }

    private void checkSignedIn() {
        if (state == State.SIGNEDOUT) {
//            throw new (400, "Sign in");
        }
    }

}

