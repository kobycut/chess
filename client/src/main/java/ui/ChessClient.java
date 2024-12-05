package ui;

import chess.*;
import facades.NotificationHandler;
import facades.ServerFacade;
import com.google.gson.Gson;
import exceptions.*;
import facades.WebSocketFacade;
import model.AuthData;
import model.GameData;
import model.GameDataCollection;

import java.util.Arrays;
import java.util.Collection;


import static java.lang.Integer.parseInt;

public class ChessClient {
    private State state = State.SIGNEDOUT;
    private String username = null;
    private final ServerFacade server;
    private AuthData authData;
    private Playing playing = Playing.NOTPLAYING;
    private Observing observing = Observing.NOTOBSERVING;
    public String teamColor;
    private Integer gameId;
    public ChessBoard chessBoard;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
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
                case "redraw" -> redrawBoard();
                case "leave" -> leave();
                case "makeMove" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help() {

        if (observing == Observing.OBSERVING) {
            return """
                    - redraw
                    - leave
                    - highlight (highlights legal moves)
                    """;
        }

        if (playing == Playing.PLAYING) {
            return """
                    - redraw
                    - leave
                    - makeMove <START POSITION> <END POSITION> (e.g. e7 e5)
                    - resign
                    - highlight (highlights legal moves)
                    """;
        }
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
                result.append(gson.toJson(i)).append(": ").append("(GAMEID: ").append(gameId).append(")" +
                        " (GAMENAME: ").append(gameName).append(") (BLACK PLAYER: ").append(blackUser).append(")" +
                        " (WHITE PLAYER: ").append(whiteUser).append(")\n");
            }
        }
        return result.toString();
    }

    public String playGame(String... params) throws DataAccessException {
        checkSignedIn();
        if (params.length == 2) {
            Integer id = parseInt(params[0]);
            gameId = id;
            String playerColor = params[1];
            teamColor = playerColor;
            server.joinGame(id, playerColor, authData);
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.joinGame(username, playerColor, id, authData.authToken());


            playing = Playing.PLAYING;
            return String.format(EscapeSequences.SET_TEXT_COLOR_BLUE + "joined game %s as %s player", id, playerColor);
        }
        throw new DataAccessException(400, "provide the correct playGame information");
    }

    public String observeGame(String... params) throws DataAccessException {
        checkSignedIn();
        if (params.length == 1) {
            Integer id = parseInt(params[0]);
            observing = Observing.OBSERVING;
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.joinGame(username, "Observer", id, authData.authToken());
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

    private void drawBoard(ChessBoard board, String playerColor) {
        DrawChessBoard drawChessBoard = new DrawChessBoard(board, null, null);
        if (observing == Observing.OBSERVING) {
            drawChessBoard.drawWhiteBoard();
        } else if (playerColor.equals("WHITE")) {
            drawChessBoard.drawWhiteBoard();
        } else if (playerColor.equals("BLACK")) {
            drawChessBoard.drawBlackBoard();
        }
    }


    public String redrawBoard() throws DataAccessException {

        var board = chessBoard;

        if (board == null) {
            board = new ChessBoard();
            board.resetBoard();
        }
        board.mirrorBoard();
        if (playing == Playing.PLAYING || observing == Observing.OBSERVING) {
            if (observing == Observing.OBSERVING) {
                drawBoard(board, "WHITE");
                return "redrew the board";
            }
            drawBoard(board, teamColor);
            return "redrew the board";
        } else {
            throw new DataAccessException(400, "please play or observe a game to complete this action");
        }
    }

    public String leave() throws DataAccessException {
        if (playing == Playing.NOTPLAYING && observing == Observing.NOTOBSERVING) {
            throw new DataAccessException(400, "Can't leave if you are not in a game");
        }
        ws.leaveGame(username, gameId, teamColor);
        observing = Observing.NOTOBSERVING;
        playing = Playing.NOTPLAYING;
        return String.format(username + " left the game");
    }

    public String makeMove(String... params) throws DataAccessException {
        if (playing == Playing.NOTPLAYING) {
            throw new DataAccessException(400, "Can't make a move if not playing a game");
        }
        if (params.length == 2) {
            try {
                String startLet = params[0];
                String endLet = params[1];
                char startLetter = startLet.charAt(0);
                char endLetter = endLet.charAt(0);
                char startNum = startLet.charAt(1);
                char endNum = endLet.charAt(1);

                int startRow = Character.getNumericValue(startNum);
                int endRow = Character.getNumericValue(endNum);
                Integer startCol = getCol(startLetter);
                Integer endCol = getCol(endLetter);
                ChessPosition startPos = new ChessPosition(startRow, startCol);
                ChessPosition endPos = new ChessPosition(endRow, endCol);
                ChessMove move = new ChessMove(startPos, endPos, null);
                String moveString = startLetter + Integer.toString(startRow) + " to " + endLetter +
                        Integer.toString(endRow);


                ws = new WebSocketFacade(serverUrl, notificationHandler);

                ws.makeMove(username, move, gameId, teamColor, moveString, authData);


            } catch (Exception ex) {
                throw new DataAccessException(500, "move invalid");
            }
        } else {
            throw new DataAccessException(400, "provide the correct move information");
        }
        return "NONE";


    }

    public String resign() throws DataAccessException {
        if (playing == Playing.NOTPLAYING) {
            throw new DataAccessException(400, "Can't resign if you are not in a game");
        }
        ws.resign(username, gameId, authData.authToken());
        return String.format(username + " resigned");
    }

    public String highlight(String... params) throws DataAccessException {

        if (playing == Playing.PLAYING || observing == Observing.OBSERVING) {
            var board = chessBoard;

            if (board == null) {
                board = new ChessBoard();
                board.resetBoard();
            }
            String stringWrongPos = params[0];
            var charCol = stringWrongPos.charAt(0);
            var charRow = stringWrongPos.charAt(1);
            int col = getCol(charCol);
            ChessPosition position = new ChessPosition(Character.getNumericValue(charRow), col);
            position = findMirroredPosition(position);
            var pieceAtPos = board.getPiece(position);
            ChessPiece piece = new ChessPiece(pieceAtPos.getTeamColor(), pieceAtPos.getPieceType());
            Collection<ChessMove> validMoves = piece.pieceMoves(board, position);
            board.mirrorBoard();
            highlightBoard(board, teamColor, validMoves, position);
            return "highlighted the board";
        } else {
            throw new DataAccessException(400, "please play or observe a game to complete this action");
        }
    }

    private Integer getCol(char letter) {
        int col = 1;
        switch (letter) {
            case 'b' -> {
                col = 2;
            }
            case 'c' -> {
                col = 3;
            }
            case 'd' -> {
                col = 4;
            }
            case 'e' -> {
                col = 5;
            }
            case 'f' -> {
                col = 6;
            }
            case 'g' -> {
                col = 7;
            }
            case 'h' -> {
                col = 8;
            }
        }
        ;
        return col;
    }


    private void highlightBoard(ChessBoard board, String playerColor, Collection<ChessMove> validMoves, ChessPosition position) {
        DrawChessBoard drawChessBoard = new DrawChessBoard(board, validMoves, position);

        if (observing == Observing.OBSERVING) {
            drawChessBoard.drawWhiteBoard();
        } else if (playerColor.equals("WHITE")) {
            drawChessBoard.drawWhiteBoard();
        } else if (playerColor.equals("BLACK")) {
            drawChessBoard.drawBlackBoard();
        }
    }


    private ChessPosition findMirroredPosition(ChessPosition position) {
        int mirroredRow = position.getRow();
        int mirroredCol = 9 - position.getColumn();
        return new ChessPosition(mirroredRow, mirroredCol);
    }


}

