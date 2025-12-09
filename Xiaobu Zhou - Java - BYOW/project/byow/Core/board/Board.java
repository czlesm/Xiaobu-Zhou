package byow.Core.board;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic board object which holds the tetile of current GUI.
 *
 * @author Cosette Zhou
 */
public class Board implements Serializable {
    private int width;

    private int height;

    private TETile[][] board;

    /**
     * An empty board which is used for clean up purpose.
     */
    private TETile[][] emptyBoard;

    /**
     * Constructor to build the board.
     *
     * @param width
     * @param height
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.board = new TETile[width][height];
        this.emptyBoard = new TETile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Default the tetile to nothing.
                board[i][j] = Tileset.NOTHING;
                emptyBoard[i][j] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Copy the board other to a new board, must have same width and height.
     *
     * @param other
     */
    public Board(Board other) {
        int width = other.width;
        int height = other.height;
        TETile[][] newBoard = new TETile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newBoard[i][j] = other.getTEtile(byow.Core.board.Point.of(i, j));
            }
        }
    }

    /**
     * Return the Tetile board.
     *
     * @return
     */
    public TETile[][] getBoard() {
        return board;
    }

    /**
     * Return the empty board.
     *
     * @return
     */
    public TETile[][] getEmptyBoard() {
        return emptyBoard;
    }

    /**
     * Verify whether given point is valid within current board.
     *
     * @param point
     * @return
     */
    public boolean isValid(byow.Core.board.Point point) {
        return point.getX() >= 2 && point.getX() < width - 2 && point.getY() >= 2 && point.getY() < height - 2;
    }

    /**
     * Verify whether given a list of points are valid within current board.
     *
     * @param points
     * @return
     */
    public boolean isValid(List<byow.Core.board.Point> points) {
        if (points == null || points.isEmpty()) {
            return true;
        }
        for (byow.Core.board.Point point : points) {
            if (!isValid(point)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the texture given coordinate Point.
     *
     * @param point
     * @return
     */
    public TETile getTEtile(byow.Core.board.Point point) {
        // NPE check.
        if (!isValid(point)) {
            return null;
        }
        return board[point.getX()][point.getY()];
    }

    /**
     * Override and set the texture given coordinate Point.
     *
     * @param point
     * @param tile
     */
    public void setTETile(byow.Core.board.Point point, TETile tile) {
        // NPE check.
        if (!isValid(point)) {
            return;
        }
        board[point.getX()][point.getY()] = tile;
    }

    /**
     * Return a list of points with certain texture.
     *
     * @param tile
     * @return
     */
    public List<byow.Core.board.Point> getPointsWithTETile(TETile tile) {
        List<byow.Core.board.Point> result = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == tile) {
                    result.add(byow.Core.board.Point.of(i, j));
                }
            }
        }
        return result;
    }

    /**
     * Get the total width of current board.
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the total height of current board.
     *
     * @return
     */
    public int getHeight() {
        return height;
    }
}