package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DrawChessBoard {
    private final ChessBoard board;
    private final List<ChessPiece> pieces = new ArrayList<>();
    private int counter = 0;
    private List<Integer> validMoveList = new ArrayList<>();
    private final Collection<ChessMove> validMoves;
    private final ChessPosition position;

    public DrawChessBoard(ChessBoard board, Collection<ChessMove> validMoves, ChessPosition position) {
        board.mirrorBoard();
        this.board = board;
        this.position = position;
        this.validMoves = validMoves;

        for (int i = 7; i > -1; i--) {
            for (int j = 7; j > -1; j--) {
                func2(i, j);
            }
        }

    }

    private int func(int i, int j, int ifValid) {
        if (validMoves != null) {
            if (Objects.equals(position, new ChessPosition(i + 1, j + 1))) {
                ifValid = 2;
            }
            for (ChessMove validMove : validMoves) {
                if (Objects.equals(validMove.getEndPosition(), new ChessPosition(i + 1, j + 1))) {
                    ifValid = 1;
                    break;
                }
            }
        }
        return ifValid;
    }

    public void drawWhiteBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.SET_TEXT_BOLD);
        boolean reverse = true;
        drawHeaders(out, reverse);
        drawBoard(out, reverse, validMoves);
        out.println();
        drawHeaders(out, reverse);
        out.println();
    }

    public void drawBlackBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.SET_TEXT_BOLD);
        boolean reverse = false;
        pieces.clear();
        validMoveList.clear();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                func2(i, j);
            }
        }

        counter = 0;
        var out2 = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        drawHeaders(out2, reverse);
        drawBoard(out2, reverse, validMoves);
        out.println();
        drawHeaders(out2, reverse);
        out.println();
    }
    private void func2(int i, int j) {
        var ifValid = 0;
        ChessPiece piece = board.getPiece(new ChessPosition(i + 1, j + 1));
        ifValid = func(i, j, ifValid);
        pieces.add(piece);
        if (validMoves != null) {
            validMoveList.add(ifValid);
        }
    }
    private void drawHeaders(PrintStream out, boolean reverse) {
        String[] headers = {" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
        if (reverse) {
            headers = new String[]{" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        }

        for (int boardCol = 0; boardCol < 10; boardCol++) {
            drawHeader(out, headers[boardCol]);
        }
    }

    private void drawHeader(PrintStream out, String headerText) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 0 && j == 0 || i == 2 && j == 2 || i == 0 && j == 1 || i == 2 && j == 1 || i == 1 && j == 2 || i == 1 && j == 0) {
                    continue;
                }
                out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                if (i == 1) {
                    out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
                    out.print(headerText);
                    continue;
                }
                out.print(" ");

            }

        }
    }

    private void drawBoard(PrintStream out, boolean reverse, Collection<ChessMove> validMoves) {
        int bgNum = 1;
        for (int i = 0; i < 8; i++) {
            out.println();
            drawSquare(out, i, 1, bgNum, reverse, validMoves);
            bgNum += 1;
            for (int j = 0; j < 8; j++) {
                drawSquare(out, i, 0, bgNum, reverse, validMoves);
                bgNum += 1;
            }
            drawSquare(out, i, 1, bgNum, reverse, validMoves);
        }

    }

    private void drawSquare(PrintStream out, int index, int bool, int bgNum, boolean reverse, Collection<ChessMove> validMoves) {
        var tileColor = "BLACK";
        String[] sideHeaders = {"1", "2", "3", "4", "5", "6", "7", "8"};
        if (reverse) {
            sideHeaders = new String[]{"8", "7", "6", "5", "4", "3", "2", "1"};
        }
        out.print(EscapeSequences.SET_BG_COLOR_BLACK);


        if (bgNum % 2 == 0) {
            out.print(EscapeSequences.SET_BG_COLOR_WHITE);
            tileColor = "WHITE";
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boolean skip = false;
                if (bool == 1) {
                    out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                    skip = true;
                    out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
                }
                if (i == 0 && j == 0 || i == 2 && j == 2 || i == 0 && j == 1 || i == 2 && j == 1 || i == 1 && j == 2 || i == 1 && j == 0) {
                    continue;
                }
                if (!validMoveList.isEmpty() && !skip && validMoveList.size() > counter) {
                    if (validMoveList.get(counter) == 1 && i != 2 && tileColor.equals("BLACK")) {
                        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    }
                    if (validMoveList.get(counter) == 1 && i != 2 && !tileColor.equals("BLACK")) {
                        out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                    }
                    if (validMoveList.get(counter) == 2 && i != 2) {
                        out.print(EscapeSequences.SET_BG_COLOR_YELLOW);

                    }
                }
                if (i == 1) {
                    if (bool == 1) {
                        out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                        out.print(sideHeaders[index]);

                        continue;
                    }

                    out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                    ChessPiece piece = pieces.get(counter);


                    if (piece == null) {
                        out.print(" ");

                        counter++;
                        continue;
                    }
                    if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {

                        out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);


                        out.print(piece.toString().toUpperCase());
                    }
                    if (piece.getTeamColor() != ChessGame.TeamColor.BLACK) {
                        out.print(piece.toString().toUpperCase());
                    }

                    counter++;
                    continue;
                }
                out.print(" ");

            }
        }
    }
}
