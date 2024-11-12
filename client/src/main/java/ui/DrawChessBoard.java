package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DrawChessBoard {
    private ChessBoard board;
    private List<ChessPiece> pieces = new ArrayList<>();
    private int counter = 0;

    public DrawChessBoard(ChessBoard board) {
        this.board = board;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i + 1, j + 1));
                pieces.add(piece);
            }
        }
    }

    public void main() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.SET_TEXT_BOLD);
        drawHeaders(out);
        drawBoard(out);
        out.println();
        drawHeaders(out);
        out.println();
    }

    private void drawHeaders(PrintStream out) {
        String[] headers = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        for (int boardCol = 0; boardCol < 10; boardCol++) {
            drawHeader(out, headers[boardCol]);
        }
    }

    private void drawHeader(PrintStream out, String headerText) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                if (i == 1 && j == 1) {
                    out.print(EscapeSequences.SET_TEXT_COLOR_BLACK);
                    out.print(headerText);
                    continue;
                }
                out.print(" ");
            }

        }
    }

    private void drawBoard(PrintStream out) {
        int bgNum = 1;
        for (int i = 0; i < 8; i++) {
            out.println();
            drawSquare(out, i, 1, bgNum);
            bgNum += 1;
            for (int j = 0; j < 8; j++) {
                drawSquare(out, i, 0, bgNum);
                bgNum += 1;
            }
            drawSquare(out, i, 1, bgNum);
        }

    }

    private void drawSquare(PrintStream out, int index, int bool, int bgNum) {
        String[] sideHeaders = {"8", "7", "6", "5", "4", "3", "2", "1"};
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
                if (i == 1 && j == 1) {
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
                        out.print(piece);

                    } else {
                        out.print(piece);
                    }
                    counter++;
                    continue;
                }
                out.print(" ");
            }
        }
    }
}
