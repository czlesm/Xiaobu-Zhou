package byow.Core.element;

import byow.Core.board.Pathway;

import java.io.Serializable;

/**
 * Elements in the board, such as players, keys etc.
 *
 * @author Cosette Zhou
 */
public interface Element extends Serializable {

    /**
     * If this is a movable element, move to the new place.
     *
     * @param direction
     */
    void move(Pathway.Direction direction);

    /**
     * If this is a movable element, move to the new place.
     *
     * @param c
     */
    void move(char c);

    /**
     * This will be used to decide whether we can move to the next point.
     *
     * @param point
     * @return
     */
    boolean isValidTETile(byow.Core.board.Point point);

    /**
     * Return the texture of current element.
     *
     * @return
     */
    byow.TileEngine.TETile getTETile();

}
