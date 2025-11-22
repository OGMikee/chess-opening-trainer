package com.ogmikee.chess;

import com.ogmikee.chess.logic.Game;
import com.ogmikee.chess.model.Move;
import com.ogmikee.chess.model.Square;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChessEngineTest {

    @Test
    void testGameInitialization() {
        Game game = new Game();
        assertNotNull(game);
        assertNotNull(game.getBoard());
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", game.toFEN());
    }

    @Test
    void testPawnLegalMoves() {
        Game game = new Game();
        Square e2 = new Square("e2");
        var moves = game.getLegalMoves(e2);

        assertEquals(2, moves.size(), "Pawn on e2 should have 2 legal moves");
    }

    @Test
    void testMakeMove() {
        Game game = new Game();
        Square e2 = new Square("e2");
        var moves = game.getLegalMoves(e2);

        assertFalse(moves.isEmpty(), "Should have legal moves");

        Move move = moves.get(0);
        game.makeMove(move);

        assertNotEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                game.toFEN(),
                "FEN should change after move");
    }
}