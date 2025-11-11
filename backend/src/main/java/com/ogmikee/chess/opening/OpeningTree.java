package com.ogmikee.chess.opening;

import com.ogmikee.chess.model.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete opening tree for a repertoire.
 * Manages adding lines and traversing the tree.
 */
public class OpeningTree {
    private OpeningNode root;
    private Color playerColor;

    public OpeningTree(Color playerColor) {
        this.playerColor = playerColor;
        this.root = new OpeningNode(null, true);
    }

    /**
     * Add a line of moves to the tree.
     * Merges with existing branches where moves overlap.
     *
     * TODO:
     * - Start at root node
     * - For each move in the list:
     *   - Determine if it's player's move or opponent's move
     *   - Check if child with this move already exists (use findChild)
     *   - If exists: navigate to that child
     *   - If doesn't exist: create new node, add as child, navigate to it
     * - This way, common move sequences share nodes
     *
     * Example:
     * Line 1: ["e4", "e5", "Nf3", "Nc6"]
     * Line 2: ["e4", "e5", "Bc4", "Nc6"]
     * Result: e4 and e5 nodes are shared, then tree branches at move 3
     *
     * @param moves List of moves in SAN notation
     */
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

    public OpeningNode getRoot() {
        return root;
    }

    public Color getPlayerColor() {
        return playerColor;
    }
}