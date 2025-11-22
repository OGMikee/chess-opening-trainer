package com.ogmikee.chess;

import com.ogmikee.chess.model.Color;
import com.ogmikee.chess.opening.OpeningNode;
import com.ogmikee.chess.opening.OpeningTree;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OpeningTreeTest {

    @Test
    void testTreeCreation() {
        OpeningTree tree = new OpeningTree(Color.WHITE);
        assertNotNull(tree);
        assertNotNull(tree.getRoot());
        assertEquals(Color.WHITE, tree.getPlayerColor());
    }

    @Test
    void testAddLine() {
        OpeningTree tree = new OpeningTree(Color.WHITE);
        List<String> line = Arrays.asList("e4", "e5", "Nf3", "Nc6");

        tree.addLine(line);

        OpeningNode e4 = tree.getRoot().findChild("e4");
        assertNotNull(e4, "Should have e4 node");

        OpeningNode e5 = e4.findChild("e5");
        assertNotNull(e5, "Should have e5 node");
    }

    @Test
    void testNodeSharing() {
        OpeningTree tree = new OpeningTree(Color.WHITE);
        tree.addLine(Arrays.asList("e4", "e5", "Nf3", "Nc6"));
        tree.addLine(Arrays.asList("e4", "e5", "Bc4", "Nf6"));

        OpeningNode e4 = tree.getRoot().findChild("e4");
        OpeningNode e5 = e4.findChild("e5");

        assertEquals(2, e5.getChildren().size(),
                "e5 should have 2 children (Nf3 and Bc4)");
    }

    @Test
    void testFindNode() {
        OpeningTree tree = new OpeningTree(Color.WHITE);
        tree.addLine(Arrays.asList("e4", "e5", "Nf3", "Nc6"));

        OpeningNode found = tree.findNode(Arrays.asList("e4", "e5", "Nf3"));
        assertNotNull(found, "Should find node at path");
        assertEquals("Nf3", found.getMove());

        OpeningNode notFound = tree.findNode(Arrays.asList("e4", "d5"));
        assertNull(notFound, "Should not find non-existent path");
    }

    @Test
    void testValidateLine() {
        OpeningTree tree = new OpeningTree(Color.WHITE);

        List<String> validLine = Arrays.asList("e4", "e5", "Nf3", "Nc6");
        assertTrue(tree.validateLine(validLine), "Valid line should pass validation");

        // Test with a move that's definitely illegal (nonsense notation)
        List<String> invalidLine = Arrays.asList("e4", "e5", "Zz9");
        assertFalse(tree.validateLine(invalidLine), "Invalid notation should fail validation");
    }

    @Test
    void testGetAllPlayerNodes() {
        OpeningTree tree = new OpeningTree(Color.WHITE);
        tree.addLine(Arrays.asList("e4", "e5", "Nf3", "Nc6"));
        tree.addLine(Arrays.asList("e4", "c5", "Nf3", "d6"));

        List<OpeningNode> playerNodes = tree.getAllPlayerNodes();

        assertTrue(playerNodes.size() >= 2, "Should have at least 2 player nodes");
    }
}