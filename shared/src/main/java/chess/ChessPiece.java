package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType type;
    private final ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.teamColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        ChessPosition leftPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
        ChessPosition diagonalUpLeftPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        ChessPosition upPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        ChessPosition diagonalUpRightPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        ChessPosition rightPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
        ChessPosition diagonalDownRightPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        ChessPosition downPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        ChessPosition diagonalDownLeftPosition = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);

        if (myPiece.type == PieceType.KING) {

            List<ChessPosition> lst = Arrays.asList(leftPosition, diagonalUpLeftPosition, upPosition, diagonalUpRightPosition, rightPosition, diagonalDownRightPosition, downPosition, diagonalDownLeftPosition);


            for (int i = 0; i < 8; i++) {
                ChessPosition uniquePosition = lst.get(i);
                if (uniquePosition.getColumn() < 9 && uniquePosition.getRow() < 9 && uniquePosition.getColumn() > 0 && uniquePosition.getRow() > 0) {
                    if (board.getPiece(uniquePosition) == null) {
                        moves.add(new ChessMove(myPosition, uniquePosition, null));
                    } else if (board.getPiece(myPosition).getTeamColor() != board.getPiece(uniquePosition).getTeamColor()) {
                        moves.add(new ChessMove(myPosition, uniquePosition, null));
                    }

                }
            }


        }
        if (myPiece.type == PieceType.QUEEN) {

        }
        if (myPiece.type == PieceType.BISHOP) {

        }
        if (myPiece.type == PieceType.KNIGHT) {

        }
        if (myPiece.type == PieceType.ROOK) {

        }
        if (myPiece.type == PieceType.PAWN) {
            List<ChessPosition> diagonallst = Arrays.asList(diagonalUpLeftPosition, diagonalUpRightPosition);

            if (board.getPiece(myPosition).teamColor == ChessGame.TeamColor.WHITE) {
                for (int i = 0; i < 2; i++) {
                    ChessPosition uniquePosition = diagonallst.get(i);
                    if (uniquePosition.getColumn() < 9 && uniquePosition.getRow() < 9 && uniquePosition.getColumn() > 0 && uniquePosition.getRow() > 0) {
                        if (board.getPiece(uniquePosition) != null) {
                            if (board.getPiece(myPosition).getTeamColor() != board.getPiece(uniquePosition).getTeamColor()) {
                                moves.add(new ChessMove(myPosition, uniquePosition, null));
                            }
                        }
                    }
                }
                if (board.getPiece((upPosition)) == null) {
                    moves.add(new ChessMove(myPosition, upPosition, null));
                }
                ChessPosition doublePosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());

                if (myPosition.getRow() == 2 && board.getPiece(doublePosition) == null && board.getPiece(upPosition) == null) {

                    moves.add(new ChessMove(myPosition, doublePosition, null));
                }
                if (myPosition.getRow() == 8) {
                    
                }
// add for when straight in front of pawn.

            }

        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, teamColor);
    }

    @Override
    public String toString() {
        String string = "";
        switch (type) {
            case KING:
                string = "K";
                break;
            case QUEEN:
                string = "Q";
                break;
            case BISHOP:
                string = "B";
                break;
            case KNIGHT:
                string = "N";
                break;
            case ROOK:
                string = "R";
                break;
            case PAWN:
                string = "P";
                break;
        }
        if (teamColor == ChessGame.TeamColor.BLACK) {
            string = string.toLowerCase();
        }
        return string;
    }
}
