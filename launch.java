import java.util.Scanner;

public class Launch {

    private static String GAME_MODE = null;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Launch launch = new Launch(scan);
        
        if (GAME_MODE.equals("word hunt")) {
            launch.wordHunt(scan);
        } else if (GAME_MODE.equals("anagrams")) {
            launch.anagrams();
        }
    }


    public Launch(Scanner scan) {
        printLogo();
        System.out.print("Enter Game Option: ");
        while (GAME_MODE == null) {
            String userInput = scan.nextLine().toLowerCase();
            if (userInput.equals("anagrams") || userInput.equals("word hunt")) {
                GAME_MODE = userInput;
                System.out.println("\033\143");
            } else {
                System.out.print("\033\143");
                printLogo();
                System.out.print("Invalid Option. Please Enter Valid Game Option: ");
            }
        }
    }


    public void anagrams() {
        //TO-DO
    }


    public void wordHunt(Scanner scan) {
        printWordHunt();
        System.out.print("Enter the size of the board (4 or 5): ");
        int size = -1;
        while (size == -1) {
            try {
                int input = Integer.valueOf(scan.nextLine());
                if (input != 4 && input != 5 && input != 2) {
                    throw new IllegalArgumentException();
                }
                size = input;
            } catch (Exception e) {
                System.out.println("\033\143");
                printWordHunt();
                System.out.println("Invalid Input. Please enter the size of the board (4 or 5): ");
            }
        }

        String[][] inputGrid = new String[size][size];

        int i = 0;
        while (i < size) {
            System.out.println("Enter row " + (i + 1) + " letters: ");
            try {
                String rowLetters = scan.nextLine();
                if (rowLetters.length() != size) {
                    throw new IllegalArgumentException();
                }
                for (int j = 0; j < size; j++) {
                    if (Character.isDigit(rowLetters.charAt(j))) {
                        throw new IllegalArgumentException();
                    }
                    inputGrid[i][j] = String.valueOf(rowLetters.charAt(j));
                }
                i++;
            } catch (Exception e) {
                System.out.println("\033\143");
                printWordHunt();
                System.out.println("Invalid row input. Please enter the letters of row " + (i + 1) + ": ");
            }
        }
        long startTime = System.nanoTime();

        Graph graph = new Graph(inputGrid, size);
        // graph.printGraph();
        graph.depthFirstSearch();

        long endTime = System.nanoTime();
        long durationMillis = (endTime - startTime) / 1_000_000; // Convert nanoseconds to milliseconds
        double durationSeconds = durationMillis / 1000.0; // Convert milliseconds to seconds
        System.out.println("Execution time: " + durationSeconds + " seconds");
    }


    public void printLogo() {
        System.out.println("                                                           !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("    Welcome to                                             !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("                             _   _                _        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("   __ _  __ _ _ __ ___   ___| | | | __ ___      _| | __    !!!!!!!!!!!!!!!!7?JJYYYYYYYYYYJJ?77!!!!!!!!!!!!!!!");
        System.out.println("  / _` |/ _` | '_ ` _ \\ / _ \\ |_| |/ _` \\ \\ /\\ / / |/ /    !!!!!!!!!!!!7JPB#####&&###B#&#####BPJ7!!!!!!!!!!!!");
        System.out.println(" | (_| | (_| | | | | | |  __/  _  | (_| |\\ V  V /|   <     !!!!!!!!!!75B#&#&&#&&#B5?77B&&##&&#&#B5?!!!!!!!!!!");
        System.out.println("  \\__, |\\__,_|_| |_| |_|\\___|_| |_|\\__,_| \\_/\\_/ |_|\\_\\    !!!!!!!!!JB&####BGBGY?!!!!!7?5#BGB#####BY7!!!!!!!!");
        System.out.println("  |___/                                                    !!!!!!!!?####&5~:....:::::::..::.:~P&####Y7!!!!!!!");
        System.out.println("                                                           !!!!!!!!G####Y.   :^           !7~ :5####G?7!!!!!!");
        System.out.println("                                                           !!!!!!!7B#B#G.  ^!Y5!~         ^!:  .G#BBBY7!!!!!!");
        System.out.println("                                                           !!!!!!!7GBBBP...!?PG?7......~~^. ...:GBBBBJ7!!!!!!");
        System.out.println("                                                           !!!!!!!!5BBBBY~:::!7::^^^^^^7J!::::~5BBBBP?7!!!!!!");
        System.out.println("                                                           !!!!!!!!7PBBBBGY!!~!7JJ?77JY?!~~~75GBBBBGJ7!!!!!!!");
        System.out.println("                                                           !!!!!!!!!7YGGGB57JJYYJ?!^~7JYYJ???PBGGG5J7!!!!!!!!");
        System.out.println("                                                           !!!!!!!!!!!?YPJ~!!!!!7?!~!?7!!!!!!!PG5J77!!!!!!!!!");
        System.out.println("                                                           !!!!!!!!!!!!!77777!!!!7?7?7!!!!77????7!!!!!!!!!!!!");
        System.out.println("    Game Options:                                          !!!!!!!!!!!!!!!777777777?7777777777!!!!!!!!!!!!!!!");
        System.out.println("     - \"anagrams\"                                          !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("     - \"word hunt\"                                         !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("                                                           !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");        
    }

    public void printWordHunt() {
        System.out.println("                                                                                                     ");
        System.out.println("                                                            ,--,                                     ");
        System.out.println("           .---.                                          ,--.'|                             ___     ");
        System.out.println("          /. ./|                       ,---,           ,--,  | :                           ,--.'|_   ");
        System.out.println("      .--'.  ' ;   ,---.    __  ,-.  ,---.'|        ,---.'|  : '         ,--,      ,---,   |  | :,'  ");
        System.out.println("     /__./ \\ : |  '   ,'\\ ,' ,'/ /|  |   | :        |   | : _' |       ,'_ /|  ,-+-. /  |  :  : ' :  ");
        System.out.println(" .--'.  '   \\' . /   /   |'  | |' |  |   | |        :   : |.'  |  .--. |  | : ,--.'|'   |.;__,'  /   ");
        System.out.println("/___/ \\ |    ' '.   ; ,. :|  |   ,',--.__| |        |   ' '  ; :,'_ /| :  . ||   |  ,\"' ||  |   |    ");
        System.out.println(";   \\  \\;      :'   | |: :'  :  / /   ,'   |        '   |  .'. ||  ' | |  . .|   | /  | |:__,'| :    ");
        System.out.println(" \\   ;  `      |'   | .; :|  | ' .   '  /  |        |   | :  | '|  | ' |  | ||   | |  | |  '  : |__  ");
        System.out.println("  .   \\    .\\  ;|   :    |;  : | '   ; |:  |        '   : |  : ;:  | : ;  ; ||   | |  |/   |  | '.'| ");
        System.out.println("   \\   \\   ' \\ | \\   \\  / |  , ; |   | '/  '        |   | '  ,/ '  :  `--'   \\   | |--'    ;  :    ; ");
        System.out.println("    :   '  |--\"   `----'   ---'  |   :    :|        ;   : ;--'  :  ,      .-./   |/        |  ,   /  ");
        System.out.println("     \\   \\ ;                      \\   \\  /          |   ,/       `--`----'   '---'          ---`-'   ");
        System.out.println("      '---\"                        `----'           '---'                                            ");
        System.out.println("                                                                                                     ");
    }
}