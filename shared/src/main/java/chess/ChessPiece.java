package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;

    private final ChessGame.TeamColor color;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", color=" + color +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
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
        PieceType pieceType = board.getPiece(myPosition).getPieceType();
        if(pieceType == PieceType.BISHOP){
            return bishopMoves(board,myPosition);
        }
        if(pieceType == PieceType.KING){
            return kingMoves(board,myPosition);
        }
        if(pieceType == PieceType.KNIGHT){
            return knightMoves(board,myPosition);
        }
        if(pieceType == PieceType.PAWN){
            return pawnMoves(board,myPosition);
        }
        if(pieceType == PieceType.ROOK){
            return rookMoves(board,myPosition);
        }
        if(pieceType == PieceType.QUEEN){
            Collection<ChessMove> bishopMoves = new HashSet<>(bishopMoves(board,myPosition));
            Collection<ChessMove> rookMoves = new HashSet<>(rookMoves(board,myPosition));
            bishopMoves.addAll(rookMoves);
            return bishopMoves;
        }
        return null;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        Collection<ChessMove> bishopMoves;
        bishopMoves = new HashSet<>();
        ChessPiece currPiece = board.getPiece(myPosition);
        ChessGame.TeamColor color = currPiece.color;
        for(int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(row + i, col + i);
            if (validatePosition(board, newPosition, color)) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                bishopMoves.add(newMove);
                if (board.getPiece(newPosition) != null) {
                    break;
                }
            }
            else{
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(row + i, col - i);
            if (validatePosition(board, newPosition, color)) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                bishopMoves.add(newMove);
                if (board.getPiece(newPosition) != null) {
                    break;
                }
            }
            else{
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(row - i, col + i);
            if (validatePosition(board, newPosition, color)) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                bishopMoves.add(newMove);
                if (board.getPiece(newPosition) != null) {
                    break;
                }
            }
            else{
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(row - i, col - i);
            if(validatePosition(board, newPosition,color)){
                ChessMove newMove = new ChessMove(myPosition,newPosition, null);
                bishopMoves.add(newMove);
                if(board.getPiece(newPosition) != null){
                    break;
                }
            }
            else{
                break;
            }
        }
        return bishopMoves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> kingMoves;
        kingMoves = new HashSet<>();
        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        ChessPiece currPiece = board.getPiece(myPosition);
        ChessGame.TeamColor color = currPiece.color;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                ChessPosition newPosition = new ChessPosition(row + i, col + j);
                if (validatePosition(board, newPosition, color)) {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    kingMoves.add(newMove);
                }
            }
        }
        return kingMoves;
    }


    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        Collection<ChessMove> knightMoves;
        knightMoves = new HashSet<>();
        ChessPiece currPiece = board.getPiece(myPosition);
        ChessGame.TeamColor color = currPiece.color;
        ChessPosition newPosition = new ChessPosition(row - 1 , col - 2 );
        if (validatePosition(board, newPosition, color)) {
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            knightMoves.add(newMove);
        }
        newPosition = new ChessPosition(row - 2 , col - 1 );
        if (validatePosition(board, newPosition, color)) {
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            knightMoves.add(newMove);
        }
        newPosition = new ChessPosition(row - 2 , col + 1 );
        if (validatePosition(board, newPosition, color)) {
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            knightMoves.add(newMove);
        }
        newPosition = new ChessPosition(row - 1 , col + 2 );
        if (validatePosition(board, newPosition, color)) {
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            knightMoves.add(newMove);
        }
        newPosition = new ChessPosition(row + 1 , col + 2 );
        if (validatePosition(board, newPosition, color)) {
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            knightMoves.add(newMove);
        }
        newPosition = new ChessPosition(row + 2 , col + 1 );
        if (validatePosition(board, newPosition, color)) {
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            knightMoves.add(newMove);
        }
        newPosition = new ChessPosition(row + 2 , col - 1 );
        if (validatePosition(board, newPosition, color)) {
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            knightMoves.add(newMove);
        }
        newPosition = new ChessPosition(row + 1 , col - 2 );
        if (validatePosition(board, newPosition, color)) {
            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
            knightMoves.add(newMove);
        }
        return knightMoves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> rookMoves;
        rookMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece currPiece = board.getPiece(myPosition);
        ChessGame.TeamColor color = currPiece.color;
        for(int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(row + i, col );
            if (validatePosition(board, newPosition, color)) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                rookMoves.add(newMove);
                if (board.getPiece(newPosition) != null) {
                    break;
                }
            }
            else{
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(row - i, col);
            if (validatePosition(board, newPosition, color)) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                rookMoves.add(newMove);
                if (board.getPiece(newPosition) != null) {
                    break;
                }
            }
            else{
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(row , col + i);
            if (validatePosition(board, newPosition, color)) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                rookMoves.add(newMove);
                if (board.getPiece(newPosition) != null) {
                    break;
                }
            }
            else{
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(row, col - i);
            if(validatePosition(board, newPosition,color)){
                ChessMove newMove = new ChessMove(myPosition,newPosition, null);
                rookMoves.add(newMove);
                if(board.getPiece(newPosition) != null){
                    break;
                }
            }
            else{
                break;
            }
        }
        return rookMoves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> pawnMoves = new HashSet<>();
        boolean promotePiece = false;
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece currPiece = board.getPiece(myPosition);
        ChessGame.TeamColor color = currPiece.color;

        if ((color == ChessGame.TeamColor.WHITE && row == 7) || (color == ChessGame.TeamColor.BLACK && row == 2)) {
            promotePiece = true;
        }

        if((color == ChessGame.TeamColor.WHITE && row == 2) || (color == ChessGame.TeamColor.BLACK && row == 7)){
            for(int i = 1; i < 3; i++) {
                ChessPosition newPosition = new ChessPosition(0,0);
                if (color == ChessGame.TeamColor.WHITE) {
                    newPosition = new ChessPosition(row + i, col);
                }
                if (color == ChessGame.TeamColor.BLACK) {
                    newPosition = new ChessPosition(row - i, col);
                }
                if (validatePosition(board, newPosition, color) && board.getPiece(newPosition) == null) {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    pawnMoves.add(newMove);
                    if (board.getPiece(newPosition) != null) {
                        break;
                    }
                }
                else {
                    break;
                }
            }
        }
        else{
            ChessPosition newPosition = new ChessPosition(0,0);
            if (color == ChessGame.TeamColor.WHITE) {
                newPosition = new ChessPosition(row + 1, col);
            }
            if (color == ChessGame.TeamColor.BLACK) {
                newPosition = new ChessPosition(row - 1, col);
            }
            if (validatePosition(board, newPosition, color) && board.getPiece(newPosition) == null) {
                if(!promotePiece) {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    pawnMoves.add(newMove);
                }
                else{
                    Collection<ChessMove> newPieces = addAllPromotion(myPosition,newPosition);
                    pawnMoves.addAll(newPieces);
                }
            }
        }
        ChessPosition newPosition = new ChessPosition(0,0);
        if (color == ChessGame.TeamColor.WHITE) {
            newPosition = new ChessPosition(row + 1, col - 1 );
            if(validatePosition(board, newPosition, color) && board.getPiece(newPosition) != null) {
                if(!promotePiece) {
                    ChessMove newMove;
                    newMove = new ChessMove(myPosition, newPosition, null);
                    pawnMoves.add(newMove);
                }
                else { pawnMoves.addAll(addAllPromotion(myPosition,newPosition)); }
            }
            newPosition = new ChessPosition(row + 1, col + 1 );
            if(validatePosition(board, newPosition, color) && board.getPiece(newPosition) != null) {
                if(promotePiece == false) {
                    ChessMove newMove;
                    newMove = new ChessMove(myPosition, newPosition, null);
                    pawnMoves.add(newMove);
                }
                else{
                    pawnMoves.addAll(addAllPromotion(myPosition,newPosition));
                }
            }
        }
        if (color == ChessGame.TeamColor.BLACK) {
            newPosition = new ChessPosition(row - 1, col - 1);
            if(validatePosition(board, newPosition, color) && board.getPiece(newPosition) != null) {
                if(promotePiece == false) {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    pawnMoves.add(newMove);
                }
                else{ pawnMoves.addAll(addAllPromotion(myPosition,newPosition)); }
            }
            newPosition = new ChessPosition(row - 1, col + 1);
            if(validatePosition(board, newPosition, color) && board.getPiece(newPosition) != null) {
                if(!promotePiece) {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    pawnMoves.add(newMove);
                }
                else{ pawnMoves.addAll(addAllPromotion(myPosition,newPosition)); }
            }
        }
        return pawnMoves;
    }

    private boolean validatePosition(ChessBoard board, ChessPosition newPosition, ChessGame.TeamColor oldColor){
        int row = newPosition.getRow();
        int col = newPosition.getColumn();
        if (row < 1 || row > 8){
            return false;
        }
        if (col < 1 || col > 8){
            return false;
        }
        ChessPiece currPiece = board.getPiece(newPosition);
        if(currPiece == null){
            return true;
        }
        if(currPiece.color == oldColor){
            return false;
        }
        return true;
    }

    private Collection<ChessMove> addAllPromotion(ChessPosition myPosition, ChessPosition newPosition){
        Collection<ChessMove> pawnMoves = new HashSet<>();
        ChessMove newMove = new ChessMove(myPosition, newPosition, PieceType.QUEEN);
        pawnMoves.add(newMove);
        newMove = new ChessMove(myPosition, newPosition, PieceType.BISHOP);
        pawnMoves.add(newMove);
        newMove = new ChessMove(myPosition, newPosition, PieceType.ROOK);
        pawnMoves.add(newMove);
        newMove = new ChessMove(myPosition, newPosition, PieceType.KNIGHT);
        pawnMoves.add(newMove);
        return pawnMoves;
    }

}