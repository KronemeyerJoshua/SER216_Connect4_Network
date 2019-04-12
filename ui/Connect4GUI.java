package ui;

import core.Connect4;
import core.Connect4ComputerPlayer;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

/**
 * Connect 4 GUI
 *
 * @author Joshua Kronemeyer
 * @version 1.1
 * @date 4 /09/2019
 */
public class Connect4GUI extends Application {
    private final boolean DEBUG = true; // DEBUGGING MODE, Adds slider for animation speed
    private int animationSpeed = 150; // DEBUG VALUE - If you wish to speed up the rate at which the pieces fall
    private boolean ai_Enabled = false;
    private String playerId;
    private Connect4ComputerPlayer ai;

    // Our Connect4 Game Instance
    private Connect4 game = new Connect4();

    // UI Elements
    private GridPane board;      // Represents our game board
    private VBox root;
    private Label lblPlayer;        // Top of root screen to show player turn
    private Circle[][] crcPieces;   // Represent our game pieces
    private Connect4Client c;

    /**
     * Simple launch for our GUI
     * This is package-private
     */
    static void launchGUI() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Basic Framework for our GUI
        try {
            System.out.println("Waiting on other client...");
            c = new Connect4Client();
            root = new VBox();
            lblPlayer = new Label();
            board = new GridPane();
            crcPieces = new Circle[7][7];
            root.getChildren().add(lblPlayer);
            root.getChildren().add(board);
            // Populate all of our UI elements
            initialize();
            primaryStage.setScene(new Scene(root, 800, 800));
            primaryStage.setMinHeight(800);
            primaryStage.setMinWidth(800);
            primaryStage.setTitle("Connect4GUI");
            primaryStage.show();

            /* TO DO: PROMPT FOR AI/PLAYER -- AI CURRENTLY DISABLED
            ButtonType aiBtn = new ButtonType("ai");
            ButtonType playerBtn = new ButtonType("player");
            // Show our instructions
            Alert instructions = new Alert(Alert.AlertType.NONE, "Welcome to Connect4! Simply click anywhere on the column to drop your piece in.\n We are waiting for another client to connect.", ButtonType.OK);
            Optional<ButtonType> in = instructions.showAndWait();
            if (in.get() == aiBtn) {
                ai_Enabled = true;
                ai = new Connect4ComputerPlayer();
            }*/

            // DEBUG ANIMATION SPEED SLIDER - THIS WILL BREAK MULTIPLAYER IN SOME WAYS
            if (DEBUG) {
                Slider slider;
                slider = new Slider(1, animationSpeed, 25);
                root.getChildren().add(slider);

                slider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                    animationSpeed = newVal.intValue();
                });
            }

            // X Indicates 2nd player
            if (playerId.equals("X")) {
                lblPlayer.setText("Waiting on other player...");
                waitOnOpponent();
            }

        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.NONE, "Could not connect to server... Terminating.", ButtonType.OK);
            a.showAndWait();
            Platform.exit();
        }
    }

    /**
     * Assigns actual values to all of our GUI elements and populates them
     *
     * @See Debugging modules at bottom of this function
     * @throws IOException Server connection
     */
    private void initialize() throws IOException {
        playerId = c.getInitial();
        lblPlayer.setText("Player " + playerId);
        Label lblColNum; // Temporary label to print our column numbers at the top
        lblPlayer.setFont(Font.font("Times New Roman", 50));
        lblPlayer.setTextFill(Color.WHITE);

        root.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setFillWidth(false); // Don't auto-resize our UI elements
        root.setAlignment(Pos.CENTER);

        // Grid Spacing
        board.setHgap(8);
        board.setVgap(8);

        for (int i = 0; i < game.getRowBoardSize() + 1; i++) {
            // Column Numbers
            lblColNum = new Label(Integer.toString(i + 1));
            lblColNum.setFont(Font.font("Times New Roman", 30));
            lblColNum.setTextFill(Color.WHITE);
            board.add(lblColNum, i, 0);
            board.setHalignment(lblColNum, HPos.CENTER);

            for (int j = 1; j < game.getColumnBoardSize(); j++) {
                // Our board, composed of "empty" circles
                Circle n = new Circle(50, 50, 50, Color.LIGHTGRAY);
                n.radiusProperty().bind(Bindings.min(root.widthProperty().divide(15), root.heightProperty().divide(15))); // Moderate scaling on resize
                n.getProperties().put("row", j);
                n.getProperties().put("column", i);
                crcPieces[i][j] = n;
                n.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    try {
                        fxGameLoop((int) n.getProperties().get("column"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                board.add(crcPieces[i][j], i, j);
            }
        }
    }

    private void waitOnOpponent() throws IOException {
        lblPlayer.setText("Waiting on other player...");
        board.setDisable(true);
        PauseTransition p = new PauseTransition(Duration.millis(animationSpeed * 30));
        PauseTransition win = new PauseTransition(Duration.millis(animationSpeed * 30));
        win.setOnFinished(e -> checkWin("Opponent"));

        p.setOnFinished(e -> {
            try {
                int opponentMove = c.getPlayerMove();
                if (opponentMove != -2) {
                    game.update(Integer.toString(opponentMove));
                    animateFall(opponentMove);
                    board.setDisable(false);
                    lblPlayer.setText("Player " + playerId);
                } else if (opponentMove == -2) {
                    opponentMove = c.getPlayerMove();
                    game.update(Integer.toString(opponentMove));
                    animateFall(opponentMove);
                    win.play();
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        });

        p.play();
    }

    /**
     * Our main game "loop" that runs on MouseClick of the board
     *
     * @param col Our column the player has selected via MouseClick
     * @throws IOException Server connection
     */
    private void fxGameLoop(int col) throws IOException {
        lblPlayer.setText("Player " + playerId);
        PauseTransition p = new PauseTransition(Duration.millis(animationSpeed * 30));
        PauseTransition win = new PauseTransition(Duration.millis(animationSpeed * 30));
        int query = c.sendMove(Integer.toString(col));
        p.setOnFinished(e -> {
            try {
                waitOnOpponent();
            } catch (Exception ex) {

            }
        });

        win.setOnFinished(e -> checkWin("You"));

        switch (query) {
            case 1:
                lblPlayer.setText("Invalid Move: Try again.");
                break;
            case -2:
                game.update(Integer.toString(col));
                animateFall(col);
                win.play();
                break;
            case 0:
                game.update(Integer.toString(col));
                animateFall(col);
                waitOnOpponent();
                break;
            case -1:
                new Alert(Alert.AlertType.NONE, "CAPTAIN! SOMETHING WENT WRONG! THE SHIPS GOING DOWN!", ButtonType.CLOSE);
                Platform.exit();
        }

        /* TO DO: IMPLEMENT PROPER AI CONTROL VIA SERVER
        *  CODE BELOW NEVER GETS CALLED CURRENTLY */
        if (ai_Enabled && query != -2) {
            lblPlayer.setText("Player " + game.getCurrentPlayer() + " is thinking...");
            query = c.sendMove(ai.generate(col));
            p.setOnFinished(e -> {
                if (c.getLastQuery() == -2) {
                    checkWin(game.getCurrentPlayer());
                } else {
                    animateFall(ai.getAiMove());
                    lblPlayer.setText("Player " + game.getCurrentPlayer());
                }
            });
            while (query == 1) {
                query = c.sendMove(ai.generate(ai.getAiMove()));
            }
            game.update(Integer.toString(ai.getAiMove()));
            p.play();
        }
    }

    /**
     * Checks and handles win conditions
     * @param player The player who won
     * @return Future use for replay / matchmaking
     */
    private boolean checkWin(String player) {
        board.setDisable(true);
        lblPlayer.setText(player + " won! Please close...");
        /* TO DO: Fix Animation / Alert overlap, implement reset via alert panel
        Alert playAgain = new Alert(Alert.AlertType.NONE, "Congratulations to Player " + game.getCurrentPlayer() + "!\nWould you like to play again?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> r = playAgain.showAndWait();
        if (r.get() == ButtonType.YES) {
            game.initBoard();
            try {
                c.sendMove("r");
            } catch (Exception e) {

            }
            for (int i = 0; i < game.getRowBoardSize() + 1; i++) {
                for (int j = 1; j < game.getColumnBoardSize(); j++) {
                    crcPieces[i][j].setFill(Color.LIGHTGRAY);
                }
            }
            board.setDisable(false);
            return true;
        } else {
            Platform.exit();
        }*/
        return false;
    }

    /**
     * Animates falling game piece on board
     *
     * @param col The column we are "falling" through
     */
    private void animateFall(int col) throws NullPointerException {
        board.setDisable(true); // Prevent spam clicks which can cause strange errors
        SequentialTransition st = new SequentialTransition();
        Color color;
        // Color of game pieces by player
        if (game.getCurrentPlayer().equalsIgnoreCase("x"))
            color = Color.DARKCYAN;
        else
            color = Color.YELLOW;

        // Our game pieces are inverse of the actual board, need to fix this to start at the "top"
        int row = (game.getRowBoardSize() + 1) - game.getLastRow();

        // Each circle will light up and fade, cycling through the column
        for (int i = 1; i < row; i++) {
            FadeTransition ft = new FadeTransition(Duration.millis(animationSpeed), crcPieces[col][i]);
            FillTransition ft2 = new FillTransition(Duration.millis(animationSpeed), crcPieces[col][i], color.LIGHTGRAY, color);
            ft.setToValue(1);
            ft.setAutoReverse(true);
            ft.setCycleCount(2);
            ft2.setAutoReverse(true);
            ft2.setCycleCount(2);
            st.getChildren().add(ft);
            st.getChildren().add(ft2);
        }
        // Our last piece needs to stay highlighted
        FadeTransition ft = new FadeTransition(Duration.millis(animationSpeed), crcPieces[col][row]);
        FillTransition ft2 = new FillTransition(Duration.millis(animationSpeed), crcPieces[col][row], Color.LIGHTGRAY, color);
        ft.setToValue(1.0);
        st.getChildren().add(ft);
        st.getChildren().add(ft2);
        // Play all of our animations sequentially
        st.play();
    }
}
