package ui;

import Facades.ServerFacade;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exceptions.*;
import model.AuthData;
import model.GameData;
import model.GameDataCollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private String username = null;
    private final ServerFacade server;
    private final String serverUrl;
    private AuthData authData;


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
        return "quit";
    }

    public String login(String... params) throws DataAccessException {
        checkSignedOut();
        if (params.length == 2) {

            username = params[0];
            String password = params[1];
            authData = server.login(username, password);
            // websocket
            state = State.SIGNEDIN;
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "logged in as %s \n", username);
        }
        throw new DataAccessException(400, "provide the correct login information");
    }

    public String register(String... params) throws DataAccessException {
        checkSignedOut();
        if (params.length == 3) {

            username = params[0];
            String password = params[1];
            String email = params[2];
            authData = server.register(username, password, email);
            authData = server.login(username, password);
            state = State.SIGNEDIN;
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "registered %s.", username);
        }
        throw new DataAccessException(400, "username is already taken or the inputs were not correct");

    }

    public String logout() throws DataAccessException {
        checkSignedIn();

        server.logout(authData);
        state = State.SIGNEDOUT;
        authData = null;
        return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "logged out %s", username);
    }

    public String createGame(String... params) throws DataAccessException {
        checkSignedIn();
        if (params.length == 1) {
            server.createGame(params[0], authData);
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "game %s created", params[0]);
        }
        throw new DataAccessException(400, "provide the correct createGame information");

    }

    public String listGames() throws DataAccessException {
        checkSignedIn();
        Object games = server.listGames(authData);
        var result = new StringBuilder();
        var gson = new Gson();
        int i = 0;
        if (games instanceof GameDataCollection collection) {

            for (GameData game : collection.games()) {
                i++;
                var gameId = game.gameID();
                var gameName = game.gameName();
                var blackUser = game.blackUsername();
                var whiteUser = game.whiteUsername();
                if (blackUser == null) {
                    blackUser = "NONE";
                }
                if (whiteUser == null) {
                    whiteUser = "NONE";
                }
                result.append(gson.toJson(i)).append(": ").append("(GAMEID: ").append(gameId).append(") (GAMENAME: ").append(gameName).
                        append(") (BLACK PLAYER: ").append(blackUser).append(") (WHITE PLAYER: ").append(whiteUser).append(")\n");
            }
        }
        return result.toString();
    }

    public String playGame(String... params) throws DataAccessException {
        checkSignedIn();
        if (params.length == 2) {
            Integer id = parseInt(params[0]);
            String playerColor = params[1];
            server.joinGame(id, playerColor, authData);

            ChessBoard chessBoard = new ChessBoard(); // testes
            chessBoard.resetBoard();
            drawBoard(chessBoard);
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "joined game %s as %s player", id, playerColor);
        }
        throw new DataAccessException(400, "provide the correct playGame information");
    }

    public String observeGame(String... params) throws DataAccessException {
        checkSignedIn();
        if (params.length == 1) {
            Integer id = parseInt(params[0]);
            server.observeGame(id);

            ChessBoard chessBoard = new ChessBoard(); // testes
            chessBoard.resetBoard();
            drawBoard(chessBoard);

            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "observing game %s", id);
        }
        throw new DataAccessException(400, "provide the correct observeGame information");

    }

    private void checkSignedIn() throws DataAccessException {
        if (state == State.SIGNEDOUT) {
            throw new DataAccessException(400, "please sign in");
        }
    }

    private void checkSignedOut() throws DataAccessException {
        if (state == State.SIGNEDIN) {
            throw new DataAccessException(400, "please sign out");
        }
    }

    private void drawBoard(ChessBoard board) {
        DrawChessBoard drawChessBoard = new DrawChessBoard(board);
        drawChessBoard.main();


    }


}

