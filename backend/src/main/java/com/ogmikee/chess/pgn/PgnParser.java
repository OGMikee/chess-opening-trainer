package com.ogmikee.chess.pgn;

import java.util.ArrayList;
import java.util.List;

public class PgnParser {

    public static List<String> parseMoves(String pgn) {
        List<String> moves = new ArrayList<>();
        String[] tokens = pgn.split("\\s+");
        for (String token : tokens){
            if (!token.isEmpty() && !isMoveNumber(token) && !isGameResult(token)){
                moves.add(token);
            }
        }
        return moves;
    }

    private static boolean isMoveNumber(String token) {
        if (token.endsWith(".")){
            return token.substring(0, token.length() -1).matches("\\d+");
        }
        return false;
    }

    private static boolean isGameResult(String token) {
        return token.equals("1-0") ||
                token.equals("0-1") ||
                token.equals("1/2-1/2") ||
                token.equals("*");
    }
}