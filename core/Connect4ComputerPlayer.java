package core;

import java.util.Random;

/**
 * Connect 4 AI
 *
 * @author Joshua Kronemeyer
 * @version 1.0
 * @date 3/15/2019
 */

/**
 * The type Connect 4 computer player.
 */
public class Connect4ComputerPlayer {
    /**
     * The Ai move.
     */
    private int aiMove;

    /**
     * Instantiates a new Connect 4 computer player.
     */
    public Connect4ComputerPlayer() {
        aiMove = 1;
    }

    /**
     * Generate string.
     *
     * @param i Player input
     * @return AI input generated
     */
    public String generate(int i) {
        Random rnd = new Random(System.currentTimeMillis());
        aiMove = rnd.nextInt((i + 3)) + 1;
        while (aiMove > 6) {
            aiMove = rnd.nextInt((i + 2)) + 1;
        }
        return Integer.toString(aiMove);
    }

    /**
     * Gets last AI Move
     *
     * @return AI Move
     */
    public int getAiMove() {
        return aiMove;
    }
}
