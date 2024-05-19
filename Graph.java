import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

public class Graph {
    private int boardSize;
    private Node[][] nodeList;
    private Set<String> validWords;
    private Trie trie;
    private Map<String, List<Node>> wordPaths;

    public Graph(String[][] inputGrid, int boardSize) {
        this.boardSize = boardSize;
        nodeList = new Node[boardSize][boardSize];
        validWords = new HashSet<>();
        wordPaths = new HashMap<>();
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
                nodeList[i][j] = new Node(j, i, inputGrid[i][j]);
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

    public void depthFirstSearch() {
        validWords.clear();
        wordPaths.clear();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                StringBuilder path = new StringBuilder();
                List<Node> currentPath = new ArrayList<>();
                dfs(nodeList[i][j], new HashSet<>(), path, trie.getRoot().children[nodeList[i][j].letter.charAt(0) - 'a'], currentPath);
            }
        }
    }

    private void dfs(Node node, Set<Node> visited, StringBuilder path, TrieNode trieNode, List<Node> currentPath) {
        visited.add(node);
        path.append(node.letter);
        currentPath.add(node);

        if (path.length() >= 3 && trieNode.isEndOfWord) {
            validWords.add(path.toString());
            wordPaths.put(path.toString(), new ArrayList<>(currentPath));
        }
        
        for (int i = 0; i < 26; i++) {
            TrieNode child = trieNode.children[i];
            if (child != null) {
                char letter = (char) ('a' + i);
                for (Node neighbor : node.neighbors) {
                    if (!visited.contains(neighbor) && neighbor.letter.equals(String.valueOf(letter))) {
                        dfs(neighbor, visited, path, child, currentPath);
                    }
                }
            }
        }

        visited.remove(node);
        path.deleteCharAt(path.length() - 1);
        currentPath.remove(currentPath.size() - 1);
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
            System.out.println(count + ": " + word + " -> Path: " + wordPaths.get(word));
            printPath();
            count--;
        }
    }

    public void printPath() {
        if (boardSize == 4) {
            System.out.println("    A     B     C     D   ");
            System.out.println(" +-----+-----+-----+-----+");
            for (int i = 1; i < 5; i++) {
                System.out.println(i + "|     |     |     |     |");
                System.out.println(" +-----+-----+-----+-----+");
            }
        } else if (boardSize == 5) {
            System.out.println("    A     B     C     D     E   ");
            System.out.println(" +-----+-----+-----+-----+-----+");
            for (int i = 1; i < 6; i++) {
                System.out.println(i + "|     |     |     |     |     |");
                System.out.println(" +-----+-----+-----+-----+-----+");
            }
        }
        System.out.println();
        System.out.println();
    }
}