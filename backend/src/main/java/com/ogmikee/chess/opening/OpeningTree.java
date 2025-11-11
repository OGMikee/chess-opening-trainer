package com.ogmikee.chess.opening;

import com.ogmikee.chess.logic.Game;
import com.ogmikee.chess.model.Color;
import com.ogmikee.chess.model.Move;

import java.util.ArrayList;
import java.util.List;

public class OpeningTree {
    private OpeningNode root;
    private Color playerColor;

    public OpeningTree(Color playerColor) {
        this.playerColor = playerColor;
        this.root = new OpeningNode(null, true);
    }

    public void addLine(List<String> moves) {
        OpeningNode current = this.root;
        for (int i = 0; i < moves.size(); i++){
            String move = moves.get(i);
            boolean isPlayer = isPlayerMove(i);
            OpeningNode child = current.findChild(move);
            if (child == null){
                child = new OpeningNode(move, isPlayer);
                current.addChild(child);
            }
            current = child;
        }
    }

    private boolean isPlayerMove(int moveIndex) {
        if (this.playerColor == Color.WHITE)
            return moveIndex % 2== 0;
        return moveIndex % 2 == 1;
    }

    public List<OpeningNode> getAllPlayerNodes() {
        List<OpeningNode> allPlayerNodes = new ArrayList<>();
        collectPlayerNodes(this.root, allPlayerNodes);
        return allPlayerNodes;
    }

    private void collectPlayerNodes(OpeningNode node, List<OpeningNode> result) {
        if (node.isEnabled() && node.isPlayerMove() && node.getMove() != null){
            result.add(node);
        }
        for (OpeningNode childrenNode : node.getChildren()){
            collectPlayerNodes(childrenNode, result);
        }
    }

    public OpeningNode findNode(List<String> movePath) {
        OpeningNode current = this.root;
        for (String move : movePath){
            OpeningNode child = current.findChild(move);
            if(child == null){
                return null;
            }
            current = child;
        }
        return current;
    }
    public boolean validateLine(List<String> moves) {
        Game game = new Game();
        for (String moveStr : moves) {
            Move move = SanConverter.sanToMove(moveStr, game);
            if (move == null) {
                return false;
            }
            if (!game.makeMove(move)) {
                return false;
            }
        }
        return true;
    }

    public OpeningNode getRoot() {
        return root;
    }

    public Color getPlayerColor() {
        return playerColor;
    }
}