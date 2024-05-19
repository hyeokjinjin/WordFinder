import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
    private int boardSize;
    private Node[][] nodes;
    private Set<String> combinations;
    private Set<String> dictionary;

    public Graph(String[][] inputRows, int boardSize) {
        this.boardSize = boardSize;
        nodes = new Node[boardSize][boardSize];
        combinations = new HashSet<>();
        dictionary = new HashSet<>();
        loadDictionary("words.txt");
        createNodes(inputRows);
        connectNodes();
    }

    private void loadDictionary(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void depthFirstSearch() {
        combinations.clear();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                StringBuilder path = new StringBuilder();
                dfs(nodes[i][j], new HashSet<>(), path);
            }
        }
        Set<String> validWords = new HashSet<>();
        for (String combo : combinations) {
            if (dictionary.contains(combo)) {
                validWords.add(combo);
            }
        }
        for (String word : validWords) {
            System.out.println(word);
        }
    }

    private void dfs(Node node, Set<Node> visited, StringBuilder path) {
        visited.add(node);
        path.append(node.letter);

        if (path.length() >= 3) {
            combinations.add(path.toString());
        }
        
        for (Node neighbor : node.neighbors) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited, path);
            }
        }

        visited.remove(node);
        path.deleteCharAt(path.length() - 1);
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