package chess;

import model.GameData;

import java.lang.reflect.Array;
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

    private void addMoves(ArrayList<ChessMove> moves, ChessPosition uniquePosition, ChessPosition myPosition) {
        moves.add(new ChessMove(myPosition, uniquePosition, PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, uniquePosition, PieceType.ROOK));
        moves.add(new ChessMove(myPosition, uniquePosition, PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, uniquePosition, PieceType.BISHOP));
    }

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
            List<ChessPosition> lst = Arrays.asList(leftPosition, diagonalUpLeftPosition,
                    upPosition, diagonalUpRightPosition, rightPosition, diagonalDownRightPosition, downPosition,
                    diagonalDownLeftPosition);
            for (int i = 0; i < 8; i++) {
                ChessPosition uniquePosition = lst.get(i);
                if (uniquePosition.getColumn() < 9 && uniquePosition.getRow() < 9 && uniquePosition.getColumn() > 0
                        && uniquePosition.getRow() > 0) {
                    if (board.getPiece(uniquePosition) == null) {
                        moves.add(new ChessMove(myPosition, uniquePosition, null));
                    } else if (board.getPiece(myPosition).getTeamColor() != board.getPiece(uniquePosition).getTeamColor()) {
                        moves.add(new ChessMove(myPosition, uniquePosition, null));
                    }
                }
            }
        }
        if (myPiece.type == PieceType.QUEEN) {
            bishopMoves(board, myPosition, moves);
            int i;
            i = 1;
            queenMoves(board, myPosition, moves, i);
        }
        if (myPiece.type == PieceType.BISHOP) {
            bishopMoves(board, myPosition, moves);
        }
        if (myPiece.type == PieceType.KNIGHT) {
            ChessPosition knightPosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1);
            ChessPosition knightPosition2 = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1);
            ChessPosition knightPosition3 = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2);
            ChessPosition knightPosition4 = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2);
            ChessPosition knightPosition5 = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1);
            ChessPosition knightPosition6 = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1);
            ChessPosition knightPosition7 = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2);
            ChessPosition knightPosition8 = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
            List<ChessPosition> lst = Arrays.asList(knightPosition, knightPosition2, knightPosition3,
                    knightPosition4, knightPosition5, knightPosition6, knightPosition7, knightPosition8);
            for (ChessPosition position : lst) {
                if (position.getColumn() < 1 || position.getColumn() > 8 || position.getRow() < 1 || position.getRow() > 8) {
                    continue;
                }
                if (board.getPiece(position) != null) {
                    if (board.getPiece(position).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        moves.add(new ChessMove(myPosition, position, null));
                        continue;
                    }
                    if (board.getPiece(position).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                        continue;
                    }
                }
                if (board.getPiece(position) == null) {
                    moves.add(new ChessMove(myPosition, position, null));
                }
            }
        }
        if (myPiece.type == PieceType.ROOK) {
            int i = 1;
            queenMoves(board, myPosition, moves, i);
        }
        if (myPiece.type == PieceType.PAWN) {
            List<ChessPosition> whitediagonallst = Arrays.asList(diagonalUpLeftPosition, diagonalUpRightPosition);
            List<ChessPosition> blackdiagonallst = Arrays.asList(diagonalDownLeftPosition, diagonalDownRightPosition);
            if (board.getPiece(myPosition).teamColor == ChessGame.TeamColor.WHITE) {
                for (int i = 0; i < 2; i++) {
                    ChessPosition uniquePosition = whitediagonallst.get(i);
                    if (uniquePosition.getColumn() < 9 && uniquePosition.getRow() < 9
                            && uniquePosition.getColumn() > 0 && uniquePosition.getRow() > 0) {
                        if (board.getPiece(uniquePosition) != null) {
                            if (board.getPiece(myPosition).getTeamColor() != board.getPiece(uniquePosition).getTeamColor()) {
                                if (myPosition.getRow() == 7) {
                                    addMoves(moves, uniquePosition, myPosition);
                                } else {
                                    moves.add(new ChessMove(myPosition, uniquePosition, null));
                                }
                            }
                        }
                    }
                }
                if (board.getPiece((upPosition)) == null) {
                    if (myPosition.getRow() == 7) {
                        addMoves(moves, upPosition, myPosition);
                    } else {
                        moves.add(new ChessMove(myPosition, upPosition, null));
                    }
                }
                ChessPosition doublePosition = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                if (myPosition.getRow() == 2 && board.getPiece(doublePosition) == null && board.getPiece(upPosition) == null) {
                    moves.add(new ChessMove(myPosition, doublePosition, null));
                }
            } else {
                for (int i = 0; i < 2; i++) {
                    ChessPosition uniquePosition = blackdiagonallst.get(i);
                    if (uniquePosition.getColumn() < 9 && uniquePosition.getRow() < 9 && uniquePosition.getColumn() > 0
                            && uniquePosition.getRow() > 0) {
                        if (board.getPiece(uniquePosition) != null) {
                            if (board.getPiece(myPosition).getTeamColor() != board.getPiece(uniquePosition).getTeamColor()) {
                                if (myPosition.getRow() == 2) {
                                    addMoves(moves, uniquePosition, myPosition);
                                } else {
                                    moves.add(new ChessMove(myPosition, uniquePosition, null));
                                }
                            }
                        }
                    }
                    if (board.getPiece((downPosition)) == null) {
                        if (myPosition.getRow() == 2) {
                            addMoves(moves, downPosition, myPosition);
                        } else {
                            moves.add(new ChessMove(myPosition, downPosition, null));
                        }
                    }
                    ChessPosition doubleDownPosition = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                    if (myPosition.getRow() == 7 && board.getPiece(doubleDownPosition) == null && board.getPiece(downPosition) == null) {
                        moves.add(new ChessMove(myPosition, doubleDownPosition, null));
                    }
                }
            }
        }
        return moves;
    }

    private int rookMoves(ChessPosition rookPosition, ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        if (board.getPiece(rookPosition) != null) {
            if (board.getPiece(rookPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, rookPosition, null));
                return 1;
            }
            if (board.getPiece(rookPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                return 1;
            }
        }
        if (board.getPiece(rookPosition) == null) {
            moves.add(new ChessMove(myPosition, rookPosition, null));
        }
        return 0;

    }

    private void queenMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves, int i) {
        while (true) {


            if (myPosition.getColumn() - i < 1 || myPosition.getColumn() - i > 8) {
                break;
            }
            ChessPosition rookLeftPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i);

//            rookMoves(rookLeftPosition, )

            int breakingInt = rookMoves(rookLeftPosition, board, myPosition, moves);
            if (breakingInt == 1) {
                break;
            }

            i += 1;
        }


        int j = 1;
        while (true) {
            if (myPosition.getColumn() + j < 1 || myPosition.getColumn() + j > 8) {
                break;
            }
            ChessPosition rookRightPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + j);
            int breakingInt = rookMoves(rookRightPosition, board, myPosition, moves);
            if (breakingInt == 1) {
                break;
            }

            j += 1;
        }
        int k = 1;
        while (true) {
            if (myPosition.getRow() + k < 1 || myPosition.getRow() + k > 8) {
                break;
            }
            ChessPosition rookUpPosition = new ChessPosition(myPosition.getRow() + k, myPosition.getColumn());
            int breakingInt = rookMoves(rookUpPosition, board, myPosition, moves);
            if (breakingInt == 1) {
                break;
            }

            k += 1;
        }

        int o = 1;
        while (true) {
            if (myPosition.getRow() - o < 1 || myPosition.getRow() - o > 8) {
                break;
            }
            ChessPosition rookDownPosition = new ChessPosition(myPosition.getRow() - o, myPosition.getColumn());
            int breakingInt = rookMoves(rookDownPosition, board, myPosition, moves);
            if (breakingInt == 1) {
                break;
            }

            o += 1;
        }
    }

    private void bishopMoves(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        int i = 1;

        while (true) {
            if (myPosition.getRow() + i > 8 || myPosition.getColumn() - i < 1) {
                break;
            }
            ChessPosition bishopUpLeftDiagonalPosition = new ChessPosition(myPosition.getRow() + i,
                    myPosition.getColumn() - i);
            if (board.getPiece(bishopUpLeftDiagonalPosition) != null) {
                if (board.getPiece(bishopUpLeftDiagonalPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, bishopUpLeftDiagonalPosition, null));
                    break;
                }
                if (board.getPiece(bishopUpLeftDiagonalPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    break;
                }
            }
            if (board.getPiece(bishopUpLeftDiagonalPosition) == null) {
                moves.add(new ChessMove(myPosition, bishopUpLeftDiagonalPosition, null));
            }

            i += 1;
        }

        i = 1;

        while (true) {
            if (myPosition.getRow() + i > 8 || myPosition.getColumn() + i > 8) {
                break;
            }
            ChessPosition bishopUpRightDiagonalPosition = new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i);
            if (board.getPiece(bishopUpRightDiagonalPosition) != null) {
                if (board.getPiece(bishopUpRightDiagonalPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, bishopUpRightDiagonalPosition, null));
                    break;
                }
                if (board.getPiece(bishopUpRightDiagonalPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    break;
                }
            }
            if (board.getPiece(bishopUpRightDiagonalPosition) == null) {
                moves.add(new ChessMove(myPosition, bishopUpRightDiagonalPosition, null));
            }

            i += 1;
        }

        i = 1;

        while (true) {
            if (myPosition.getRow() - i < 1 || myPosition.getColumn() - i < 1) {
                break;
            }
            ChessPosition bishopDownLeftDiagonalPosition = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i);
            if (board.getPiece(bishopDownLeftDiagonalPosition) != null) {
                if (board.getPiece(bishopDownLeftDiagonalPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, bishopDownLeftDiagonalPosition, null));
                    break;
                }
                if (board.getPiece(bishopDownLeftDiagonalPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    break;
                }
            }
            if (board.getPiece(bishopDownLeftDiagonalPosition) == null) {
                moves.add(new ChessMove(myPosition, bishopDownLeftDiagonalPosition, null));
            }

            i += 1;
        }

        i = 1;

        while (true) {
            if (myPosition.getRow() - i < 1 || myPosition.getColumn() + i > 8) {
                break;
            }
            ChessPosition bishopDownRightDiagonalPosition = new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i);
            if (board.getPiece(bishopDownRightDiagonalPosition) != null) {
                if (board.getPiece(bishopDownRightDiagonalPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, bishopDownRightDiagonalPosition, null));
                    break;
                }
                if (board.getPiece(bishopDownRightDiagonalPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                    break;
                }
            }
            if (board.getPiece(bishopDownRightDiagonalPosition) == null) {
                moves.add(new ChessMove(myPosition, bishopDownRightDiagonalPosition, null));
            }

            i += 1;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, teamColor);
    }

    @Override
    public String toString() {
        String string = switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
        if (teamColor == ChessGame.TeamColor.BLACK) {
            string = string.toLowerCase();
        }
        return string;
    }
}
