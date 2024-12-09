import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
        return "(" + (x + 1) + ", " + (y + 1) + ", " + letter + ")";
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
            System.out.println(count + ": \033[38;5;0;48;5;11m" + word + "\u001B[0m -> Path: " + wordPaths.get(word));
            printPath(wordPaths.get(word));
            count--;
        }
    }

    public void printPath(List<Node> path) {
        System.out.println((boardSize == 4) ? "    A     B     C     D   " : "    A     B     C     D     E   ");
        String divider = (boardSize == 4) ? " +-----+-----+-----+-----+" : " +-----+-----+-----+-----+-----+";
        System.out.println(divider);
        
        String[][] printBoard = new String[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                printBoard[i][j] = "     ";
            }
        }

        for (int i = 0; i < path.size(); i++) {
            if (i == 0) {
                printBoard[path.get(i).y][path.get(i).x] = "\033[4;38;5;0;48;5;10m  " + path.get(i).letter + "  \u001B[0m";
            } else {
                printBoard[path.get(i).y][path.get(i).x] = "\033[38;5;0;48;5;10m  " + path.get(i).letter + "  \u001B[0m";
            }
            
        }

        for (int i = 0; i < boardSize; i++) {
            String output = String.valueOf(i + 1); 
            for (int j = 0; j < boardSize; j++) {
                output += "|";
                output += printBoard[i][j];
            }
            System.out.println(output + "|");
            System.out.println(divider);
        }

        System.out.println();
        System.out.println();
    }

    public void saveWordPathsAsJson(String filePath) {
        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Enable pretty-printing for the output JSON
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Create a map to store the JSON structure
        Map<String, Object> jsonWordPaths = new HashMap<>();

        // Add the boardSize to the map
        jsonWordPaths.put("boardSize", boardSize);

        // Create a map to store the JSON structure, converting List<Node> to a list of node positions
        Map<String, List<String>> wordPathsJsonFormat = convertPathsToJsonFormat();

        // Add the word paths to the map
        jsonWordPaths.put("wordPaths", wordPathsJsonFormat);

        try {
            // Write the map to a JSON file
            objectMapper.writeValue(new File(filePath), jsonWordPaths);
            System.out.println("Word paths saved as JSON at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to convert word paths to a suitable format for JSON serialization
    private Map<String, List<String>> convertPathsToJsonFormat() {
        // Use a LinkedHashMap to maintain insertion order
        Map<String, List<String>> jsonWordPaths = new LinkedHashMap<>();

        // Sort the entries by path length in descending order
        wordPaths.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size())) // Sort by size descending
                .forEach(entry -> {
                    String word = entry.getKey();
                    List<Node> path = entry.getValue();

                    // Convert the path to a list of node positions (e.g., "(x,y)" format)
                    List<String> pathAsStrings = new ArrayList<>();
                    for (Node node : path) {
                        pathAsStrings.add("(" + (node.x) + "," + (node.y) + ")");
                    }

                    // Add the word and its corresponding path to the map
                    jsonWordPaths.put(word, pathAsStrings);
                });

        return jsonWordPaths;
    }
}