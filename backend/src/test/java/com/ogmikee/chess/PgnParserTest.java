package com.ogmikee.chess;

import com.ogmikee.chess.pgn.PgnParser;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PgnParserTest {

    @Test
    void testBasicPgnParsing() {
        String pgn = "1. e4 e5 2. Nf3 Nc6 3. Bb5 1-0";
        List<String> moves = PgnParser.parseMoves(pgn);

        assertEquals(5, moves.size(), "Should parse 5 moves");
        assertEquals("e4", moves.get(0));
        assertEquals("e5", moves.get(1));
        assertEquals("Nf3", moves.get(2));
        assertEquals("Nc6", moves.get(3));
        assertEquals("Bb5", moves.get(4));
    }

    @Test
    void testPgnWithCastling() {
        String pgn = "1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5 4. O-O O-O";
        List<String> moves = PgnParser.parseMoves(pgn);

        assertTrue(moves.contains("O-O"), "Should parse castling");
        assertEquals(8, moves.size(), "Should parse all moves including castling");
    }

    @Test
    void testPgnWithCaptures() {
        String pgn = "1. e4 e5 2. Nf3 Nc6 3. d4 exd4 4. Nxd4";
        List<String> moves = PgnParser.parseMoves(pgn);

        assertTrue(moves.contains("exd4"), "Should parse pawn capture");
        assertTrue(moves.contains("Nxd4"), "Should parse piece capture");
    }

    @Test
    void testEmptyPgn() {
        String pgn = "";
        List<String> moves = PgnParser.parseMoves(pgn);

        assertTrue(moves.isEmpty(), "Empty PGN should return empty list");
    }
}