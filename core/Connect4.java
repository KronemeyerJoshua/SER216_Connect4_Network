package core;

import java.util.Arrays;

/**
 * Connect 4 game controller
 *
 * @author Joshua Kronemeyer
 * @version 2.0
 * @date 3 /29/2019
 */
public class Connect4 {

    private final int COLUMN_MAX = 7;
    private final int ROW_MAX = 6;
    private final String[][] board;
    private String p;
    private int lastMoveC, lastMoveR;

    /**
     * Instantiates a new Connect 4.
     */
    public Connect4() {
        p = "O";
        lastMoveC = 0;
        lastMoveR = 0;
        board = new String[ROW_MAX][COLUMN_MAX];
        initBoard();

    }

    /**
     * Main game call, updates board
     *
     * @param s The column the current player chose
     * @return Updated board / Error Message
     */
    public boolean update(String s) {
        int in;
        try {
            in = Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }

        // Prevent Array Out of Bounds
        if (in > 0 && in < COLUMN_MAX + 1) {

            if (!addPiece(in))
                return false;
            else
                changePlayer();


            return true;
        }
        return false;
    }

    /**
     * Gets current player.
     *
     * @return the current player
     */
    public String getCurrentPlayer() {
        return p;
    }

    /**
     * Gets previous players last move by Row
     *
     * @return previous players last move by Row
     */
    public int getLastRow() {
        return lastMoveR + 1;
    }

    /**
     * Gets {@code COLUMN_MAX}
     *
     * @return The amount of columns on the board
     */
    public int getColumnBoardSize() {
        return COLUMN_MAX;
    }

    /**
     * Gets {@code ROW_MAX}
     *
     * @return The amount of rows on the board
     */
    public int getRowBoardSize() {
        return ROW_MAX;
    }

    /**
     * Gets previous players last move by Column
     *
     * @return previous players last move by Column
     */
    public int getLastColumn() {
        return lastMoveC + 1;
    }

    /**
     * Gets board.
     *
     * @return the board
     */
    public String getBoard() {
        StringBuilder builder = new StringBuilder();
        builder.append(" ");
        for (int i = 0; i < COLUMN_MAX; i++) {
            builder.append(i + 1 + " ");

        }
        builder.append(("\n"));
        for (int i = ROW_MAX - 1; i > -1; i--) {
            builder.append("|");
            for (int j = 0; j < COLUMN_MAX; j++) {
                builder.append((board[i][j] + "|"));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Gets non current player.
     *
     * @return the non current player
     */
    public String getNonCurrentPlayer() {
            if (p.equals("O"))
                return "X";
            else
                return "O";
    }
    /**
     * Changes the value of {@code p}
     */
    private void changePlayer() {
        if (p.equals("O"))
            p = "X";
        else
            p = "O";

    }

    /**
     * Init board.
     */
    public void initBoard() {
        for (String[] row : board)
            Arrays.fill(row, " ");
    }

    /**
     * @param c The column the current player chose
     * @return boolean if the piece was successfully added to our matrix
     */
    private boolean addPiece(int c) {
        for (int i = 0; i < ROW_MAX; i++) {
            if (board[i][c - 1].contains(" ")) {
                board[i][c - 1] = p;
                lastMoveR = i;
                lastMoveC = c - 1;
                return true;
            }
        }

        return false;
    }

    /**
     * Check win boolean.
     *
     * @return Boolean if current player won
     */
    public boolean checkWin() {
        changePlayer();

        // Check Vertical
        for (int i = 0; i < ROW_MAX - 3; i++) {
            for (int j = 0; j < COLUMN_MAX; j++) {
                if (board[i][j].equals(p) && board[i + 1][j].equals(p) && board[i + 2][j].equals(p) && board[i + 3][j].equals(p))
                    return true;
            }
        }

        // Check Horizontal
        for (int i = 0; i < COLUMN_MAX - 3; i++) {
            for (int j = 0; j < ROW_MAX; j++) {
                if (board[i][j].equals(p) && board[i][j + 1].equals(p) && board[i][j + 2].equals(p) && board[i][j + 3].equals(p))
                    return true;
            }
        }

        // Check Diagonal Up
        for (int i = 3; i < ROW_MAX; i++) {
            for (int j = 0; j < COLUMN_MAX - 3; j++) {
                if (board[i][j].equals(p) && board[i - 1][j + 1].equals(p) && board[i - 2][j + 2].equals(p) && board[i - 3][j + 3].equals(p))
                    return true;
            }
        }

        // Check Diagonal Down
        for (int i = 3; i < ROW_MAX; i++) {
            for (int j = 3; j < COLUMN_MAX; j++) {
                if (board[i][j].equals(p) && board[i - 1][j - 1].equals(p) && board[i - 2][j - 2].equals(p) && board[i - 3][j - 3].equals(p))
                    return true;
            }
        }
        changePlayer();
        return false;
    }
}
