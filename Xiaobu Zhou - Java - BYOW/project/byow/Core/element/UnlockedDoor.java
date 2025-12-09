package byow.Core.element;

import byow.TileEngine.Tileset;

import java.util.List;

/**
 * Door object which stands for door.
 *
 * @author Cosette Zhou
 */
public class UnlockedDoor extends byow.Core.element.AbstractElement {

    /**
     * Generate a new unlocked door instance.
     *
     * @param board
     * @param point
     * @return
     */
    public static UnlockedDoor of(byow.Core.board.Board board, byow.Core.board.Point point) {
        return new UnlockedDoor(board, point, Tileset.UNLOCKED_DOOR, null);
    }

    public UnlockedDoor(byow.Core.board.Board board, byow.Core.board.Point point, byow.TileEngine.TETile teTile,
        List<byow.Core.element.LightSource> lightSources) {
        super(board, point, teTile, lightSources);
    }

    @Override
    public boolean isValidTETile(byow.Core.board.Point point) {
        // Never moves.
        return false;
    }

}
