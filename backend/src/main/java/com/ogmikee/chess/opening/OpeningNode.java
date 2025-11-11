package com.ogmikee.chess.opening;

import java.util.ArrayList;
import java.util.List;


public class OpeningNode {
    private String move;
    private boolean isPlayerMove;
    private boolean enabled;
    private List<OpeningNode> children;

    public OpeningNode(String move, boolean isPlayerMove) {
        this.move = move;
        this.isPlayerMove = isPlayerMove;
        this.enabled = true;
        this.children = new ArrayList<>();
    }

    public void addChild(OpeningNode child) {
        this.children.add(child);
    }

    public OpeningNode findChild(String move) {
        for (OpeningNode node : this.children){
            if (node.getMove().equals(move)) return node;
        }
        return null;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public List<OpeningNode> getEnabledChildren() {
        List<OpeningNode> enabledChildren = new ArrayList<>();
        for (OpeningNode node: this.children){
            if (node.isEnabled()){
                enabledChildren.add(node);
            }
        }
        return enabledChildren;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public boolean isPlayerMove() {
        return isPlayerMove;
    }

    public void setPlayerMove(boolean playerMove) {
        isPlayerMove = playerMove;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<OpeningNode> getChildren() {
        return children;
    }

    public void setChildren(List<OpeningNode> children) {
        this.children = children;
    }
}