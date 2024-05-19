import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    private int boardSize;
    private Node[][] nodeList;
    private Set<String> validWords;
    private Trie trie;

    public Graph(String[][] inputGrid, int boardSize) {
        this.boardSize = boardSize;
        nodeList = new Node[boardSize][boardSize];
        validWords = new HashSet<>();
        trie = new Trie();        
        loadDictionary();
        createNodes(inputGrid);
        connectNodes();
    }

    private void loadDictionary() {
        try (BufferedReader reader = new BufferedReader(new FileReader("words.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                trie.insert(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNodes(String[][] inputGrid) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                String letter = inputGrid[i][j];
                nodeList[i][j] = new Node(i, j, letter);
            }
        }
    }

    private void connectNodes() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                // Connect to adjacent nodes
                if (i > 0) nodeList[i][j].addNeighbor(nodeList[i - 1][j]); // Up
                if (i < boardSize - 1) nodeList[i][j].addNeighbor(nodeList[i + 1][j]); // Down
                if (j > 0) nodeList[i][j].addNeighbor(nodeList[i][j - 1]); // Left
                if (j < boardSize - 1) nodeList[i][j].addNeighbor(nodeList[i][j + 1]); // Right

                // Connect to diagonal nodes
                if (i > 0 && j > 0) nodeList[i][j].addNeighbor(nodeList[i - 1][j - 1]); // Up-Left
                if (i > 0 && j < boardSize - 1) nodeList[i][j].addNeighbor(nodeList[i - 1][j + 1]); // Up-Right
                if (i < boardSize - 1 && j > 0) nodeList[i][j].addNeighbor(nodeList[i + 1][j - 1]); // Down-Left
                if (i < boardSize - 1 && j < boardSize - 1) nodeList[i][j].addNeighbor(nodeList[i + 1][j + 1]); // Down-Right
            }
        }
    }

    public void printGraph() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print("Node " + nodeList[i][j] + " is connected to: ");
                for (Node neighbor : nodeList[i][j].neighbors) {
                    System.out.print(neighbor + " ");
                }
                System.out.println();
            }
        }
    }

    public void printWords() {
        List<String> sortedWords = new ArrayList<>(validWords);
        sortedWords.sort(Comparator.comparingInt(String::length));
        System.out.println("Valid words found:");
        int count = sortedWords.size();
        for (String word : sortedWords) {
            System.out.println(count + ": " + word);
            count--;
        }
    }

    public void depthFirstSearch() {
        validWords.clear();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                StringBuilder path = new StringBuilder();
                dfs(nodeList[i][j], new HashSet<>(), path, trie.getRoot().children[nodeList[i][j].letter.charAt(0) - 'a']);
            }
        }
    }

    private void dfs(Node node, Set<Node> visited, StringBuilder path, TrieNode trieNode) {
        visited.add(node);
        path.append(node.letter);


        if (path.length() >= 3 && trieNode.isEndOfWord) {
            validWords.add(path.toString());
        }
        
        for (int i = 0; i < 26; i++) {
            TrieNode child = trieNode.children[i];
            if (child != null) {
                char letter = (char) ('a' + i);
                for (Node neighbor : node.neighbors) {
                    if (!visited.contains(neighbor) && neighbor.letter.equals(String.valueOf(letter))) {
                        dfs(neighbor, visited, path, child);
                    }
                }
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