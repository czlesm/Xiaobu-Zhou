package byow.Core.element;

import byow.TileEngine.Tileset;

import java.util.List;

/**
 * Flower object which stands for flower.
 *
 * @author Cosette Zhou
 */
public class Flower extends byow.Core.element.AbstractElement {

    /**
     * Generate a new flower instance.
     *
     * @param board
     * @param point
     * @param lightSources
     * @return
     */
    public static Flower of(byow.Core.board.Board board, byow.Core.board.Point point,
        List<byow.Core.element.LightSource> lightSources) {
        return new Flower(board, point, Tileset.FLOWER, lightSources);
    }

    public Flower(byow.Core.board.Board board, byow.Core.board.Point point, byow.TileEngine.TETile teTile,
        List<byow.Core.element.LightSource> lightSources) {
        super(board, point, teTile, lightSources);
    }

    @Override
    public boolean isValidTETile(byow.Core.board.Point point) {
        // Never moves.
        return false;
    }

    public void pickup() {
        getBoard().setTETile(getPoint(), Tileset.AVATAR);
    }

}
