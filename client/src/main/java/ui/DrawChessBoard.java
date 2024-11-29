package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DrawChessBoard {
    private ChessBoard board;
    private List<ChessPiece> pieces = new ArrayList<>();
    private int counter = 0;

    public DrawChessBoard(ChessBoard board) {
        this.board = board;
        for (int i = 7; i > -1; i--) {
            for (int j = 7; j > -1; j--) {
                ChessPiece piece = board.getPiece(new ChessPosition(i + 1, j + 1));
                pieces.add(piece);
            }
        }
    }

//    public void main() {
//        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//        out.print(EscapeSequences.SET_TEXT_BOLD);
//        boolean reverse = false;
//        drawHeaders(out, reverse);
//        drawBoard(out, reverse);
//        out.println();
//        drawHeaders(out, reverse);
//        out.println();
//        out.println();
//        out.println();
//        out.println();
//
//        reverse = true;
//        pieces.clear();
//
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                ChessPiece piece = board.getPiece(new ChessPosition(i + 1, j + 1));
//                pieces.add(piece);
//            }
//        }
//
//        counter = 0;
//        var out2 = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//        drawHeaders(out2, reverse);
//        drawBoard(out2, reverse);
//        out.println();
//        drawHeaders(out2, reverse);
//        out.println();
//    }

    public void drawWhiteBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.SET_TEXT_BOLD);
        boolean reverse = false;
        drawHeaders(out, reverse);
        drawBoard(out, reverse);
        out.println();
        drawHeaders(out, reverse);
        out.println();
        out.println();
        out.println();
        out.println();
    }
    public void drawBlackBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        boolean reverse = true;
        pieces.clear();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i + 1, j + 1));
                pieces.add(piece);
            }
        }

        counter = 0;
        var out2 = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        drawHeaders(out2, reverse);
        drawBoard(out2, reverse);
        out.println();
        drawHeaders(out2, reverse);
        out.println();
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

    private void drawBoard(PrintStream out, boolean reverse) {
        int bgNum = 1;
        for (int i = 0; i < 8; i++) {
            out.println();
            drawSquare(out, i, 1, bgNum, reverse);
            bgNum += 1;
            for (int j = 0; j < 8; j++) {
                drawSquare(out, i, 0, bgNum, reverse);
                bgNum += 1;
            }
            drawSquare(out, i, 1, bgNum, reverse);
        }

    }

    private void drawSquare(PrintStream out, int index, int bool, int bgNum, boolean reverse) {

        String[] sideHeaders = {"1", "2", "3", "4", "5", "6", "7", "8"};
        if (reverse) {
            sideHeaders = new String[]{"8", "7", "6", "5", "4", "3", "2", "1"};
        }
        out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        if (bgNum % 2 == 0) {
            out.print(EscapeSequences.SET_BG_COLOR_WHITE);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                if (bool == 1) {
                    out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                    out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
                }
                if (i == 0 && j == 0 || i == 2 && j == 2 || i == 0 && j == 1 || i == 2 && j == 1 || i == 1 && j == 2 || i == 1 && j == 0) {
                    continue;
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
                    } else {


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
