package com.ogmikee.chess.model;

public class Board {
    private final Piece[][] squares;

    public Board() {
        squares = new Piece[8][8];
    }

    public Piece getPiece(Square square) {
        int rank = square.getRank();
        int file = square.getFile();
        return squares[rank][file];
    }

    public void setPiece(Square square, Piece piece) {
        int rank = square.getRank();
        int file = square.getFile();
        squares[rank][file] = piece;
    }
    public void setupInitialPosition() {
        PieceType[] backRank = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
                PieceType.QUEEN, PieceType.KING, PieceType.BISHOP,
        PieceType.KNIGHT, PieceType.ROOK};
        for (int i = 0; i < 8; i++){
            squares[0][i] = new Piece(backRank[i], Color.WHITE);
            squares[1][i] = new Piece(PieceType.PAWN, Color.WHITE);
            squares[6][i] = new Piece(PieceType.PAWN, Color.BLACK);
            squares[7][i] = new Piece(backRank[i], Color.BLACK);
        }
    }

    public boolean isEmpty(Square square) {
        return getPiece(square) == null;
    }

    public void clear() {
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                squares[i][j] = null;
            }
        }
    }

    public void print() {
        for (int i = 7; i > -1; i--){
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 8; j++){
                Piece piece = squares[i][j];
                if (piece == null) System.out.print(". ");
                else System.out.print(piece.toString() + " ");
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; i--){
            sb.append((i + 1)).append(" ");
            for (int j = 0; j < 8; j++){
                Piece piece = squares[i][j];
                if (piece == null) sb.append(". ");
                else sb.append(piece.toString()).append(" ");
            }
            sb.append("\n");
        }
        sb.append("  a b c d e f g h");
        return sb.toString();
    }

    public Board copy(){
        Board newBoard = new Board();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                newBoard.squares[i][j] = this.squares[i][j];
            }
        }
        return newBoard;
    }

}