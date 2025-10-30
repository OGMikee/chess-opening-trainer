package com.ogmikee.chess.model;
/**
 * Represents a chess board - an 8x8 grid that holds pieces.
 * Coordinates: [rank][file] where rank=row (0-7) and file=column (0-7)
 * rank 0 = row 1 (white's back rank), rank 7 = row 8 (black's back rank)
 */
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

    /**
     * Set or remove a piece at a given square.
     *
     * TODO:
     * - Use square.getRank() and square.getFile() to access the array
     * - Set the piece (null means remove/empty square)
     */
    public void setPiece(Square square, Piece piece) {
        int rank = square.getRank();
        int file = square.getFile();
        squares[rank][file] = piece;
    }

    /**
     * Set up the standard chess starting position.
     * White pieces on ranks 0-1, black pieces on ranks 6-7.
     *
     * TODO:
     * - Place all 32 pieces in their starting positions
     * - White: rank 0 = back rank (ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK)
     * - White: rank 1 = all pawns
     * - Black: rank 6 = all pawns
     * - Black: rank 7 = back rank (same order as white)
     *
     * Hint: You can use loops for pawns, and manually place the back rank pieces
     */
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

    /**
     * Print the board to console (for debugging).
     *
     * TODO:
     * - Print the board in a readable format
     * - Show rank numbers and file letters
     * - Use piece symbols or toString()
     * - Empty squares can be "." or " "
     *
     * Example output:
     * 8 r n b q k b n r
     * 7 p p p p p p p p
     * 6 . . . . . . . .
     * ...
     * 1 P P P P P P P P
     * 0 R N B Q K B N R
     *   a b c d e f g h
     */
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

    /**
     * Override toString() - can just call print() or return a string representation.
     *
     * TODO: Return a string representation of the board
     */
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
}