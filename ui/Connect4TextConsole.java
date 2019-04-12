package ui;

import core.Connect4;
import core.Connect4ComputerPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

/**
 * The type Connect 4 console UI & Menu Selection
 *
 * @author Joshua Kronemeyer
 * @version 2.1
 * @date 4 /09/2019
 */
public class Connect4TextConsole {
    // Class Vars, our inputReader and game instance
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static Connect4 game;
    private static Connect4Client c;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception BufferedReader input
     */
    public static void main(String[] args) throws Exception {
        // Basic Menu Selection
        System.out.println("WARNING: ALL AI IS BROKEN - IT WILL CRASH YOUR CLIENT\nEnter G to launch the Connect 4 GUI or enter C to play against a computer P for Player to continue with console\n" +
                "You may press Q to quit at anytime.");
        while (true) {
            switch (br.readLine().toUpperCase()) {
                case "G":
                    Connect4GUI.launchGUI();
                    System.exit(0);
                    break;
                case "P":
                    gameLoop(false);
                    break;
                case "C":
                    gameLoop(true);
                    break;
                case "Q":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid Input, try again.");
                    break;
            }
        }
    }

    /**
     * Returns true/false based on whether player has a valid move
     * If it's valid, it will check to see if the player has won.
     * @param move The column the player/ai has chosen
     * @return     boolean if move is a valid input
     * @throws Exception Calling endgame function, which reads from BufferedReader
     */
    private static boolean gameTurn(String move) throws Exception {
        switch (c.sendMove(move)) {
            case -2:game.update(move);
                    System.out.println(game.getBoard());
                    endGame("You");
                    c.sendMove("r");
                    break;
            case 0: game.update(move);
                    System.out.println(game.getBoard());
                    System.out.println("Waiting on other player...");
                    move = Integer.toString(c.getPlayerMove());
                    if (move.equals("-2")) {
                        game.update(Integer.toString(c.getPlayerMove()));
                        System.out.println(game.getBoard());
                        endGame("Opponent");
                    } else {
                        game.update(move);
                        System.out.println(game.getBoard());
                    }
                    break;
            case -1: System.out.println("Uh oh... Something went wrong... Terminating");
                     System.exit(1);
                     break;
            case 1: return false;
        }

        return true;
    }

    /**
     * The main loop for the game
     * @param choice boolean if the AI is enabled or not
     * @throws Exception Grabbing input from BufferedReader, most exceptions should be handled already
     */
    private static void gameLoop(boolean choice) throws Exception {
        Connect4ComputerPlayer ai = null;
        game = new Connect4();
        game.initBoard();
        String player = "";

        /* TO-DO: IMPLEMENT AI VIA SERVER
        if (choice)
            ai = new Connect4ComputerPlayer();
        */

        try {
            c = new Connect4Client();
            System.out.println("Successfully connected.\nMatchmaking with other client...");
            player = c.getInitial();
            if (player.equals("X")) {
                System.out.println("Waiting on other player...");
                game.update(Integer.toString(c.getPlayerMove()));
                System.out.println(game.getBoard());
            }
        }
        catch (Exception e) {
            System.out.println("Failed to connect to server... Terminating.");
            System.exit(1);
        }

        // Check win
        while (true) {
            // Player Move
            System.out.println("Player " + player + " your turn, choose column 1-7");

            while (!gameTurn(br.readLine())) {
                System.out.println("Error, try again.\nPlayer" + player + " your turn, choose column 1-7");
            }

            /* TO-DO: IMPLEMENT AI VIA SERVER
            if (choice) {
                System.out.println("AI is thinking... Please do not enter anything.\n");
                sleep(2000);
                while (!gameTurn(ai.generate(game.getLastColumn()))) ;
                System.out.printf("AI Move: %d\n", game.getLastColumn());
            }*/
        }
    }

    /**
     * Ends game if Q is pressed, typically called after a player has won
     * @param player Player string
     * @throws Exception Grabbing BufferedReader input, anything invalid will just reset the board and start the game again
     */
    private static void endGame(String player) throws Exception {
        System.out.println(player + " won the game! Press q to quit");
        // Play again?
        if (br.readLine().equalsIgnoreCase("Q"))
            System.exit(0);
        //game.initBoard();
        System.exit(0);
    }
}