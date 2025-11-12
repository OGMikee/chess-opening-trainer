package com.ogmikee.chess.service;

import com.ogmikee.chess.logic.Game;
import com.ogmikee.chess.model.Move;
import com.ogmikee.chess.opening.OpeningNode;
import com.ogmikee.chess.opening.OpeningTree;
import com.ogmikee.chess.opening.SanConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrainingService {
    private Random random = new Random();

    public QuizQuestion getRandomQuiz(OpeningTree tree) {
        List<OpeningNode> allPlayerNodes = tree.getAllPlayerNodes();
        if (allPlayerNodes.isEmpty()) return null;
        List<OpeningNode> nodesWithChildren = new ArrayList<>();
        for (OpeningNode node : allPlayerNodes){
            if (node.hasChildren()){
                nodesWithChildren.add(node);
            }
        }
        if (nodesWithChildren.isEmpty()) return null;
        OpeningNode randomNode = nodesWithChildren.get(random.nextInt(nodesWithChildren.size()));
        List<String> path = getPathToNode(tree, randomNode);
        Game game = new Game();
        for (String moveStr : path){
            Move move = SanConverter.sanToMove(moveStr, game);
            if (move != null) game.makeMove(move);
        }
        String fen = game.toFEN();
        List<String> correctMoves = new ArrayList<>();
        for (OpeningNode child: randomNode.getEnabledChildren()){
            correctMoves.add(child.getMove());
        }
        return new QuizQuestion(fen, correctMoves, path);
    }

    private List<String> getPathToNode(OpeningTree tree, OpeningNode targetNode) {
        List<String> path = new ArrayList<>();
        if (findPath(tree.getRoot(), targetNode, path)) return path;
        return new ArrayList<>();
    }

    private boolean findPath(OpeningNode current, OpeningNode target, List<String> path) {
        if (current == target) return true;

        for (OpeningNode child : current.getChildren()) {
            path.add(child.getMove());
            if (findPath(child, target, path)) return true;
            path.remove(path.size() - 1);
        }
        return false;
    }

    public boolean checkQuizAnswer(OpeningTree tree, List<String> path, String userMove) {
        OpeningNode node = tree.findNode(path);
        if (node == null) return false;
        List<OpeningNode> allChildren = node.getEnabledChildren();
        for (OpeningNode child : allChildren){
            if (userMove.equals(child.getMove())){
                return true;
            }
        }
        return false;
    }

    public PlaythroughSession startPlaythrough(OpeningTree tree, List<String> startPath) {
        PlaythroughSession session = new PlaythroughSession(tree, startPath);
        OpeningNode currentNode = session.getCurrentNode();
        if (!currentNode.hasChildren()) {
            session.setComplete(true);
            return session;
        }
        List<OpeningNode> children = currentNode.getChildren();
        if (!children.isEmpty() && !children.get(0).isPlayerMove()){
            OpeningNode opponentMove = children.get(random.nextInt(children.size()));
            Move move = SanConverter.sanToMove(opponentMove.getMove(), session.getGame());
            if (move != null){
                session.getGame().makeMove(move);
            }
            session.addMovePlayed(opponentMove.getMove());
            session.setCurrentNode(opponentMove);
            if(!opponentMove.hasChildren()){
                session.setComplete(true);
            }
        }
        return session;
    }

    public MoveResult processUserMove(PlaythroughSession session, String userMove) {
        OpeningNode currentNode = session.getCurrentNode();
        OpeningNode matchingChild = null;
        for (OpeningNode child : currentNode.getEnabledChildren()){
            if (child.getMove().equals(userMove)){
                matchingChild = child;
                break;
            }
        }
        if (matchingChild == null) return new MoveResult(false, null, false);
        Move move = SanConverter.sanToMove(userMove, session.getGame());
        if (move != null )session.getGame().makeMove(move);
        session.addMovePlayed(userMove);
        session.setCurrentNode(matchingChild);
        if (!matchingChild.hasChildren()){
            session.setComplete(true);
            return new MoveResult(true, null, true);
        }
        List<OpeningNode> opponentPossibleMoves = matchingChild.getEnabledChildren();
        if (opponentPossibleMoves.isEmpty()){
            session.setComplete(true);
            return new MoveResult(true, null, true);
        }
        OpeningNode opponentNode = opponentPossibleMoves.get(random.nextInt(opponentPossibleMoves.size()));
        String opponentMoveStr = opponentNode.getMove();
        Move opponentMove = SanConverter.sanToMove(opponentMoveStr, session.getGame());
        if (opponentMove!= null) session.getGame().makeMove(opponentMove);
        session.addMovePlayed(opponentMoveStr);
        session.setCurrentNode(opponentNode);
        boolean lineComplete = !opponentNode.hasChildren();
        if (lineComplete) session.setComplete(true);
        return new MoveResult(true, opponentMoveStr, lineComplete);
    }
}