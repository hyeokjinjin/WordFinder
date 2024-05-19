import java.util.ArrayList;
import java.util.List;

public class Graph {
    private int boardSize;
    private Node[][] nodes;

    public Graph(String[][] inputRows, int boardSize) {
        this.boardSize = boardSize;
        nodes = new Node[boardSize][boardSize];
        createNodes(inputRows);
        connectNodes();
    }

    private void createNodes(String[][] inputRows) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                String letter = inputRows[i][j];
                nodes[i][j] = new Node(i, j, letter);
            }
        }
    }

    private void connectNodes() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                // Connect to adjacent nodes
                if (i > 0) nodes[i][j].addNeighbor(nodes[i - 1][j]); // Up
                if (i < boardSize - 1) nodes[i][j].addNeighbor(nodes[i + 1][j]); // Down
                if (j > 0) nodes[i][j].addNeighbor(nodes[i][j - 1]); // Left
                if (j < boardSize - 1) nodes[i][j].addNeighbor(nodes[i][j + 1]); // Right

                // Connect to diagonal nodes
                if (i > 0 && j > 0) nodes[i][j].addNeighbor(nodes[i - 1][j - 1]); // Up-Left
                if (i > 0 && j < boardSize - 1) nodes[i][j].addNeighbor(nodes[i - 1][j + 1]); // Up-Right
                if (i < boardSize - 1 && j > 0) nodes[i][j].addNeighbor(nodes[i + 1][j - 1]); // Down-Left
                if (i < boardSize - 1 && j < boardSize - 1) nodes[i][j].addNeighbor(nodes[i + 1][j + 1]); // Down-Right
            }
        }
    }

    public void printGraph() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print("Node " + nodes[i][j] + " is connected to: ");
                for (Node neighbor : nodes[i][j].neighbors) {
                    System.out.print(neighbor + " ");
                }
                System.out.println();
            }
        }
    }
}

class Node {
    int x, y;
    List<Node> neighbors;
    String letter;
    
    Node(int x, int y, String letter) {
        this.x = x;
        this.y = y;
        this.letter = letter;
        this.neighbors = new ArrayList<>();
    }

    void addNeighbor(Node neighborNode) {
        neighbors.add(neighborNode);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + letter + ")";
    }
}