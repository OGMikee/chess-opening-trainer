package com.ogmikee.chess.controller;

import com.ogmikee.chess.logic.Game;
import com.ogmikee.chess.model.Move;
import com.ogmikee.chess.model.Square;
import com.ogmikee.chess.opening.SanConverter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chess")
@CrossOrigin(origins = "*")
public class ChessController {

    @PostMapping("/execute-move")
    public Map<String, Object> executeMove(@RequestBody Map<String, String> request) {
        String fen = request.get("fen");
        String from = request.get("from");
        String to = request.get("to");

        Game game = new Game(fen);
        Square fromSquare = new Square(from);
        Square toSquare = new Square(to);

        List<Move> legalMoves = game.getLegalMoves(fromSquare);
        Move moveToExecute = null;

        for (Move move : legalMoves) {
            if (move.getTo().equals(toSquare)) {
                moveToExecute = move;
                break;
            }
        }

        if (moveToExecute != null) {
            String sanMove = SanConverter.moveToSan(moveToExecute, game);

            if (game.makeMove(moveToExecute)) {
                return Map.of(
                        "success", true,
                        "newFen", game.toFEN(),
                        "move", sanMove
                );
            }
        }

        return Map.of("success", false);
    }

    @PostMapping("/legal-moves")
    public Map<String, Object> getLegalMoves(@RequestBody Map<String, String> request) {
        String fen = request.get("fen");
        String square = request.get("square");

        Game game = new Game(fen);
        Square fromSquare = new Square(square);

        List<Move> legalMoves = game.getLegalMoves(fromSquare);
        List<String> moveSquares = new ArrayList<>();

        for (Move move : legalMoves) {
            moveSquares.add(move.getTo().toAlgebraic());
        }

        return Map.of("moves", moveSquares);
    }

    @PostMapping("/get-position")
    public Map<String, String> getPosition(@RequestBody Map<String, List<String>> request) {
        List<String> moves = request.get("moves");
        Game game = new Game();

        for (String moveStr : moves) {
            Move move = SanConverter.sanToMove(moveStr, game);
            if (move != null) {
                game.makeMove(move);
            }
        }

        return Map.of("fen", game.toFEN());
    }
}