package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard chessBoard = null;
    private TeamColor currTurn = null;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currTurn = team;
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
        if(startPosition == null){
            return null;
        }
        ChessPiece object = new ChessPiece(null,null);
        Collection<ChessMove> moves = object.pieceMoves(chessBoard,startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();
        TeamColor color = chessBoard.getPiece(startPosition).getTeamColor();

        for(ChessMove move : moves){
            if(!putsKingInDanger(move,chessBoard,color)){
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    private boolean isMoveValid(ChessMove move, TeamColor color){
        return !putsKingInDanger(move, chessBoard, color);
    }

    private Collection<ChessMove> allMoves(TeamColor color){
        ChessPiece object = new ChessPiece(null,null);
        Collection<ChessPosition> positions = currPositions(chessBoard, color);
        Collection<ChessMove> allMoves = new HashSet<>();
        for(ChessPosition position : positions){
            allMoves.addAll(object.pieceMoves(chessBoard,position));
        }
        return allMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = chessBoard.getPiece(move.getStartPosition());
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPiece.PieceType promotion = move.getPromotionPiece();
        boolean madeMove = false;
        for(ChessMove myMove : validMoves){
            if (myMove == move){
                if(promotion != null){
                    piece = new ChessPiece(piece.getTeamColor(),promotion);
                }
                chessBoard.addPiece(move.getStartPosition(),null);
                chessBoard.addPiece(move.getEndPosition(),piece);
                madeMove = true;
                break;
            }
        }
        if(madeMove){
            swapTeamColor();
        }
        else {
            throw new InvalidMoveException("Invalid Move");
        }
    }

    private void swapTeamColor(){
        if(currTurn == TeamColor.BLACK){
            currTurn = TeamColor.WHITE;
        }
        if(currTurn == TeamColor.WHITE){
            currTurn = TeamColor.BLACK;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return kingIsInDanger(chessBoard, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
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

    private boolean kingIsInDanger(ChessBoard board, TeamColor color){
        ChessPosition kingPosition = findKing(board,color);
        TeamColor otherTeamColor;
        if(color == TeamColor.WHITE){
            otherTeamColor = TeamColor.BLACK;
        }
        else{
            otherTeamColor = TeamColor.WHITE;
        }
        Collection<ChessMove> allPossibleMoves = allMoves(otherTeamColor);
        ChessMove currMove;
        for(int i = 0; i < allPossibleMoves.size(); i++) {
            currMove = allPossibleMoves[i];
            if (currMove.getEndPosition() == kingPosition) {
                if(isMoveValid(currMove,otherTeamColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean putsKingInDanger(ChessMove move, ChessBoard board, TeamColor color){
        //ChessPosition kingPosition = findKing(board,color);
        //TeamColor otherTeamColor;
        ChessBoard hypotheticalBoard = chessBoard;
        ChessPiece.PieceType promotion = move.getPromotionPiece();
        ChessPiece piece = hypotheticalBoard.getPiece(move.getStartPosition());
        /*if(color == TeamColor.WHITE){
            otherTeamColor = TeamColor.BLACK;
        }
        else{
            otherTeamColor = TeamColor.WHITE;
        }*/

        if(promotion != null){
            piece = new ChessPiece(piece.getTeamColor(),promotion);
        }
        hypotheticalBoard.addPiece(move.getStartPosition(),null);
        hypotheticalBoard.addPiece(move.getEndPosition(),piece);

        /*Collection<ChessPosition> positions = currPositions(hypotheticalBoard,otherTeamColor);
        Collection<ChessMove> allPossibleMoves = new HashSet<>();
        for(ChessPosition position : positions){
            allPossibleMoves.addAll(validMoves(position));
        }
        for(ChessMove currMove : allPossibleMoves){
            if(currMove.getEndPosition() == kingPosition){
                return true;
            }
        }
        return false;*/
        return kingIsInDanger(hypotheticalBoard, color);
    }

    private ChessPosition findKing(ChessBoard board, TeamColor color){
        ChessPosition position;
        ChessPiece piece;
        if(color == TeamColor.WHITE){
            for(int i = 1; i < 9; i++){
                for(int j = 1; j < 9; j++){
                    position = new ChessPosition(i,j);
                    piece = board.getPiece(position);
                    if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.WHITE){
                        return position;
                    }
                }
            }
        }

        if(color == TeamColor.BLACK){
            for(int i = 1; i < 9; i++){
                for(int j = 8; j > 0; j--){
                    position = new ChessPosition(i,j);
                    piece = board.getPiece(position);
                    if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.BLACK){
                        return position;
                    }
                }
            }
        }
        return null;
    }

    private Collection<ChessPosition> currPositions(ChessBoard board, TeamColor color){
        Collection<ChessPosition> positions = new HashSet<>();
        ChessPosition currPosition;
        ChessPiece currPiece;
        for(int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                currPosition = new ChessPosition(i,j);
                currPiece = board.getPiece(currPosition);
                if(currPiece == null){
                    continue;
                }
                if(currPiece.getTeamColor() == color){
                    positions.add(currPosition);
                }
            }
        }
        return positions;
    }
}
