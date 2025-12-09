package byow.Core.element;

import byow.TileEngine.Tileset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Enemy object which stands for enemy.
 *
 * @author Cosette Zhou
 */
public class Enemy extends byow.Core.element.AbstractElement {

    /**
     * Generate a new enemy instance.
     *
     * @param board
     * @param point
     * @param lightSources
     * @return
     */
    public static Enemy of(byow.Core.board.Board board, byow.Core.board.Point point,
        List<byow.Core.element.LightSource> lightSources) {
        return new Enemy(board, point, Tileset.TREE, lightSources);
    }

    public Enemy(byow.Core.board.Board board, byow.Core.board.Point point, byow.TileEngine.TETile teTile,
        List<byow.Core.element.LightSource> lightSources) {
        super(board, point, teTile, lightSources);
    }

    @Override
    public boolean isValidTETile(byow.Core.board.Point point) {
        Set<byow.TileEngine.TETile> validTETiles = new HashSet<>();
        validTETiles.add(Tileset.AVATAR);
        validTETiles.add(Tileset.FLOOR);
        validTETiles.add(Tileset.FLOOR_FULL_LIGHT);
        validTETiles.add(Tileset.FLOOR_HALF_LIGHT);
        byow.TileEngine.TETile teTile = getBoard().getTEtile(point);
        return validTETiles.contains(teTile);
    }

}
