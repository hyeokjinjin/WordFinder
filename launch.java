import java.util.Scanner;

public class Launch {

    private static String GAME_MODE = null;

    public static void main(String[] args) {
        
        Launch launch = new Launch();
        if (GAME_MODE.equals("word hunt")) {
            launch.wordHunt();
        } else if (GAME_MODE.equals("anagrams")) {
            launch.anagrams();
        }
    }


    public Launch() {
        Scanner scan = new Scanner(System.in);
        while (GAME_MODE == null) {
            printLogo();
            System.out.print("Enter Game Option: ");
            String userInput = scan.nextLine();
            if (userInput.equals("anagrams") || userInput.equals("word hunt")) {
                GAME_MODE = userInput;
            }
            System.out.print("\033\143");
        }
        scan.close();
    }


    public void anagrams() {

    }


    public void wordHunt() {

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
}