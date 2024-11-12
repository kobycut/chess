package ui;

import Facades.ServerFacade;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Arrays;

import static java.lang.Integer.parseInt;

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
            var tokens = input.split(" ");
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
        return EscapeSequences.SET_TEXT_COLOR_BLUE + "quit";
    }

    public String login(String... params) {
        checkSignedOut();
        if (params.length == 2) {
            state = State.SIGNEDIN;
            username = params[0];
            // server facade login stuff

            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "logged in as %s \n", username);
        }
        // throw error
        return "login error";
    }

    public String register(String... params) {
        checkSignedOut();
        if (params.length == 3) {
            state = State.SIGNEDIN;
            username = params[0];
            // server facade register stuff

            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "registered %s.", username);
        }
//        throw error
        return "error";
    }

    public String logout() {
        checkSignedIn();
        state = State.SIGNEDOUT;

        return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "logged out %s", username);
    }

    public String createGame(String... params) {
        checkSignedIn();
        if (params.length == 1) {
            // server facade stuff

            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "game %s created", params[0]);
        }
        // throw error
        return "error";
    }

    public String listGames() {
        checkSignedIn();
        // server facade stuff
        var result = new StringBuilder();
//        var gson = new Gson();
        // for loop that iterates through games, converts them to readable, then appends to string builder.
        // return result
        return "";
    }

    public String playGame(String... params) {
        checkSignedIn();
        if (params.length == 2) {
            Integer id = parseInt(params[0]);
            String playerColor = params[1];
            // cool server facade stuff
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "joined game %s as %s player", id, playerColor);
        }
        // throw error
        return "error";
    }

    public String observeGame(String... params) {
        checkSignedIn();
        if (params.length == 1) {
            Integer id = parseInt(params[0]);
            // more cool server facade stuff
//            ChessBoard board = server
//            drawBoard(board);
            ChessBoard chess = new ChessBoard(); // testes
            chess.resetBoard(); // testing
            drawBoard(chess); // testing
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "observing game %s", id);
        }
        // throw error
        return "error";
    }

    private void checkSignedIn() {
        if (state == State.SIGNEDOUT) {
//            throw new (400, "Sign in");
        }
    }

    private void checkSignedOut() {
        if (state == State.SIGNEDIN) {
//            throw new (400, "Sign in");
        }
    }

    private void drawBoard(ChessBoard board) {
        DrawChessBoard drawChessBoard = new DrawChessBoard(board);
        drawChessBoard.main();


    }


}

