package com.ogmikee.chess.controller;

import com.ogmikee.chess.model.Color;
import com.ogmikee.chess.pgn.PgnParser;
import com.ogmikee.chess.opening.OpeningTree;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/opening")
@CrossOrigin(origins = "*")
public class OpeningController {

    @PostMapping("/parse-pgn")
    public Map<String, List<String>> parsePgn(@RequestBody Map<String, String> request) {
        String pgn = request.get("pgn");
        List<String> pgnMoves = PgnParser.parseMoves(pgn);
        return Map.of("moves", pgnMoves);
    }

    @PostMapping("/validate-line")
    public Map<String, Boolean> validateLine(@RequestBody Map<String, List<String>> request) {
        @SuppressWarnings("unchecked")
        List<String> moves = (List<String>) request.get("moves");
        OpeningTree temp = new OpeningTree(Color.WHITE);
        boolean isValid = temp.validateLine(moves);
        return Map.of("valid", isValid);
    }
}