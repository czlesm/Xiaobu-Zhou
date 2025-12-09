package byow.Core.game;

import byow.Core.board.GameBoard;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.*;
import java.nio.file.Paths;

/**
 * Game engine to include the status of games etc.
 *
 * @author Cosette Zhou
 */
public class GameEngine {

    public static final File CWD = new File(System.getProperty("user.dir"));

    public static final File SAVED_GAME = Paths.get(CWD.getPath(), "saved_game.txt").toFile();

    private int width;

    private int height;

    private GameState gameState;

    private long seed;

    private byow.Core.board.GameBoard gameBoard;

    /**
     * Create a new GameEngine and do the necessary init.
     *
     * @param width
     * @param height
     * @return
     */
    public static GameEngine of(int width, int height) {
        return new GameEngine(width, height);
    }

    public GameEngine(int width, int height) {
        this.width = width;
        this.height = height;
        this.gameState = GameState.TITLE_GREETINGS;
        greetings();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * New game.
     *
     * @param seed
     */
    public void newGame(long seed) {
        this.seed = seed;
        this.gameBoard = GameBoard.of(width, height, seed);
        this.gameState = GameState.GAME_IN_PROGRESS;
    }

    /**
     * Save the game into file.
     */
    public void saveGame() {
        try {
            if (!SAVED_GAME.exists()) {
                SAVED_GAME.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(SAVED_GAME);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(gameBoard);
            System.out.println("Successfully save the game.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error when saving the game. " + e);
        }
    }

    /**
     * Load the game from the file.
     * The game can be saved only when game in progress!
     *
     * @return
     */
    public boolean loadGame() {
        try {
            if (!SAVED_GAME.exists()) {
                System.out.println("No saved game found, please start a new game.");
                throw new Exception("No saved game found, please start a new game.");
            }
            FileInputStream fs = new FileInputStream(SAVED_GAME);
            ObjectInputStream os = new ObjectInputStream(fs);
            byow.Core.board.GameBoard board = (byow.Core.board.GameBoard) os.readObject();
            System.out.println("Successfully load the game.");
            // Update the state.
            this.gameBoard = board;
            this.width = gameBoard.getBoard().getWidth();
            this.height = gameBoard.getBoard().getHeight();
            this.seed = gameBoard.getSeed();
            this.gameState = GameState.GAME_IN_PROGRESS;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error when loading the game. " + e);
            return false;
        }
    }

    /**
     * Display the greetings page.
     */
    public void greetings() {
        StdDraw.setCanvasSize(width * 16, height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        // Draw Title
        StdDraw.text(width / 2.0, height / 1.5, "CS61B: THE GAME");

        // Draw Options
        Font subtitle = new Font("Monaco", Font.PLAIN, 24);
        StdDraw.setFont(subtitle);
        StdDraw.text(width / 2.0, height * 0.40, "New Game (N)");
        StdDraw.text(width / 2.0, height * 0.35, "Load Game (L)");
        StdDraw.text(width / 2.0, height * 0.30, "Quit (Q)");

        StdDraw.show();
        StdDraw.pause(20);
    }

    /**
     * Enter seed and start the game.
     *
     * @param seedValue
     */
    public void enterSeed(String seedValue) {
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);

        // Draw Title
        StdDraw.text(width / 2.0, height * 0.50, "Enter a seed: ");
        StdDraw.text(width / 2.0, height * 0.45, seedValue);
        StdDraw.text(width / 2.0, height * 0.40, "Press 'S' to start the game.");

        StdDraw.show();
        StdDraw.pause(20);
    }

    /**
     * Win or lose the game or just simply end the game.
     */
    public void exitGame() {
        String message = gameState == GameState.WIN ? "Congratz, you've won the game!" : "Game Over";
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        if (gameState == GameState.WIN || gameState == GameState.LOSE) {
            StdDraw.text(width / 2.0, height / 2.0, message);
        }
        StdDraw.text(width / 2.0, height / 2.0 - 5, "Press any key to continue.");
        StdDraw.show();
        StdDraw.pause(20);
    }

    /**
     * Pause the game. Although without any key press, there's nothing to pause, but we still give the option
     * to see the title screen.
     */
    public void pause() {
        StdDraw.setCanvasSize(width * 16, height * 16);
        Font font = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        // Draw Title
        StdDraw.text(width / 2.0, height / 1.5, "CS61B: THE GAME");

        // Draw Options
        Font subtitle = new Font("Monaco", Font.PLAIN, 28);
        StdDraw.setFont(subtitle);
        StdDraw.text(width / 2.0, height * 0.40, "Continue (C)");
        StdDraw.text(width / 2.0, height * 0.35, "New Game (N)");
        StdDraw.text(width / 2.0, height * 0.30, "Load Game (L)");
        StdDraw.text(width / 2.0, height * 0.25, "Quit (Q)");

        StdDraw.show();
        StdDraw.pause(20);
    }

    /**
     * Reset to the start of the game.
     */
    public void reset() {
        this.gameState = GameState.TITLE_GREETINGS;
        this.gameBoard = GameBoard.of(width, height, seed);
        greetings();
    }

    /**
     * State machine to indicate the state of the game.
     */
    public enum GameState {
        TITLE_GREETINGS,
        TITLE_INPUT_SEEDS,
        GAME_IN_PROGRESS,
        PAUSE,
        SAVE_GAME,
        WIN,
        LOSE,
        EXIT;
    }

}
