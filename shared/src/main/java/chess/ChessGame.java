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
    TeamColor teamTurn = TeamColor.WHITE;
    ChessBoard chessBoard = new ChessBoard();

    public ChessGame() {
        chessBoard.resetBoard();

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
        BLACK,
        OVER
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var validMoves = new ArrayList<ChessMove>();
        if (this.chessBoard.getPiece(startPosition) == null) {
            return null;
        }
        var lst = this.chessBoard.getPiece(startPosition).pieceMoves(this.chessBoard, startPosition);
        for (ChessMove move : lst) {
            var color = this.chessBoard.getPiece(startPosition).getTeamColor();
            boolean pieceTaken = false;
            ChessPiece undoPiece = null;
            if (this.chessBoard.getPiece(move.getEndPosition()) != null) {
                undoPiece = this.chessBoard.getPiece(move.getEndPosition());
                pieceTaken = true;
            }
            this.makeFakeMove(move);

            if (!this.isInCheck(color)) {
                validMoves.add(move);
            }
            this.makeReverseMove(move);
            if (pieceTaken) {
                chessBoard.addPiece(move.getEndPosition(), undoPiece);
            }
        }
        return validMoves;


    }

    public void makeReverseMove(ChessMove move) {
        var start = move.getEndPosition();
        var end = move.getStartPosition();
        chessBoard.addPiece(end, chessBoard.getPiece(start));
        chessBoard.removePiece(start);
    }

    public void makeFakeMove(ChessMove move) {
        var start = move.getStartPosition();
        var end = move.getEndPosition();

        chessBoard.addPiece(end, chessBoard.getPiece(start));
        chessBoard.removePiece(start);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (chessBoard.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No Piece here");
        }
        TeamColor color = chessBoard.getPiece(move.getStartPosition()).getTeamColor();
        if (chessBoard.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No valid moves");
        }
        var validMoves = validMoves(move.getStartPosition());
        TeamColor teamTurn = getTeamTurn();

        if (chessBoard.getPiece(move.getStartPosition()).getTeamColor() != teamTurn) {
            throw new InvalidMoveException("No valid moves");
        }

        if (validMoves == null) {
            throw new InvalidMoveException("No valid moves");
        }
        for (ChessMove validMove : validMoves) {
            if (validMove.equals(move)) {
                var start = move.getStartPosition();
                var end = move.getEndPosition();
                if (move.getPromotionPiece() != null) {
                    chessBoard.addPiece(end, new ChessPiece(color, move.getPromotionPiece()));
                    chessBoard.removePiece(start);
                } else {
                    chessBoard.addPiece(end, chessBoard.getPiece(start));
                    chessBoard.removePiece(start);
                }
                TeamColor teamColor = TeamColor.WHITE;
                if (teamTurn == TeamColor.WHITE) {
                    teamColor = TeamColor.BLACK;
                }
                setTeamTurn(teamColor);
                return;
            }


        }
        throw new InvalidMoveException("Invalid Moves");
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

                ChessPosition position = new ChessPosition(i + 1, j + 1);
//                var color = getTeamTurn();

                if (chessBoard.getPiece(position) == null) {
                    continue;
                }
                if (chessBoard.getPiece(position).getTeamColor() == teamColor) {
                    lst.addAll(chessBoard.getPiece(position).pieceMoves(chessBoard, position));

                }
            }
        }
        return lst;
    }

    private TeamColor getOppColor(TeamColor teamColor) {
        var oppColor = TeamColor.WHITE;

        if (teamColor == TeamColor.WHITE) {
            oppColor = TeamColor.BLACK;
        }
        return oppColor;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = this.chessBoard.findKing(teamColor);
        var oppColor = getOppColor(teamColor);
        var lst = getAllMoves(oppColor);
        for (ChessMove chessMove : lst) {
            if (chessMove.getEndPosition().equals(kingPosition)) {
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
        if (!isInCheck(teamColor)) {
            return false;
        }

        var teamMoveLst = getAllMoves(teamColor);

        for (ChessMove move : teamMoveLst) {
            boolean pieceTaken = false;
            ChessPiece undoPiece = null;
            if (this.chessBoard.getPiece(move.getEndPosition()) != null) {
                undoPiece = this.chessBoard.getPiece(move.getEndPosition());
                pieceTaken = true;
            }
            this.makeFakeMove(move);
            if (!this.isInCheck(teamColor)) {
                return false;
            }
            this.makeReverseMove(move);
            if (pieceTaken) {
                chessBoard.addPiece(move.getEndPosition(), undoPiece);
            }

        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        var validTeamMoveLst = new ArrayList<ChessMove>();
        var allTeamMoves = new ArrayList<ChessMove>();
        var teamPiecesLeftCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                ChessPosition position = new ChessPosition(i + 1, j + 1);
                ChessPiece piece = chessBoard.getPiece(position);
                if (piece != null) {
                    if (piece.getTeamColor() == teamColor) {
                        teamPiecesLeftCount++;
                        validTeamMoveLst.addAll(validMoves(position));
                        allTeamMoves.addAll(piece.pieceMoves(chessBoard, position));
                    }

                }

            }
        }
        if (teamPiecesLeftCount == 1 && validTeamMoveLst.isEmpty()) {
            return true;
        }
        if (validTeamMoveLst.isEmpty() && allTeamMoves.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
