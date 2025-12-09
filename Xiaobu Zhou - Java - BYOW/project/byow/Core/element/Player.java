package byow.Core.element;

import byow.Core.board.Board;
import byow.Core.board.Point;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Player object which stands for player.
 *
 * @author Cosette Zhou
 */
public class Player extends byow.Core.element.AbstractElement {

    /**
     * Generate a new player instance.
     *
     * @param board
     * @param point
     * @param lightSources
     * @return
     */
    public static Player of(Board board, Point point, List<byow.Core.element.LightSource> lightSources) {
        return new Player(board, point, Tileset.AVATAR, lightSources);
    }

    public Player(Board board, Point point, TETile teTile, List<byow.Core.element.LightSource> lightSources) {
        super(board, point, teTile, lightSources);
    }

    @Override
    public boolean isValidTETile(Point point) {
        Set<TETile> validTETiles = new HashSet<>();
        validTETiles.add(Tileset.FLOOR);
        validTETiles.add(Tileset.FLOOR_FULL_LIGHT);
        validTETiles.add(Tileset.FLOOR_HALF_LIGHT);
        validTETiles.add(Tileset.FLOWER);
        validTETiles.add(Tileset.UNLOCKED_DOOR);
        validTETiles.add(Tileset.TREE);
        TETile teTile = getBoard().getTEtile(point);
        return validTETiles.contains(teTile);
    }
}
