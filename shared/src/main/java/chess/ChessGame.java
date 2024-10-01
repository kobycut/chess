package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    int round = 0;
    TeamColor teamTurn;
    ChessBoard board = new ChessBoard();

    public ChessGame() {


    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;

    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        var moves = board.getPiece(startPosition).pieceMoves(board, startPosition);

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var start = move.getStartPosition();
        var end = move.getEndPosition();
        board.addPiece(end, board.getPiece(start));
        board.removePiece(start);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    private ArrayList<ChessMove> getAllMoves(TeamColor teamColor) {
        var lst = new ArrayList<ChessMove>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                ChessPosition position = new ChessPosition(i, j);
                if (board.getPiece(position).getTeamColor() == teamColor) {
                    lst.addAll(board.getPiece(position).pieceMoves(board, position));
                }
            }
        }
        return lst;
    }

    public boolean isInCheck(TeamColor teamColor) {

        var oppColor = TeamColor.WHITE;
        ChessPosition kingPosition = board.findKing(teamColor);
        if (teamColor == TeamColor.WHITE) {
            oppColor = TeamColor.BLACK;
        }
        var lst = this.getAllMoves(oppColor);
        for (ChessMove chessMove : lst) {
            if (chessMove.getEndPosition() == kingPosition) {
                return true;
            }
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (int i = 0; i <)
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
