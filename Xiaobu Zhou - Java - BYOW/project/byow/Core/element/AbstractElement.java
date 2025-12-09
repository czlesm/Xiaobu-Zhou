package byow.Core.element;

import byow.Core.board.Board;
import byow.Core.board.Pathway;
import byow.Core.board.Point;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Elements in the board, such as players, keys etc.
 *
 * @author Cosette Zhou
 */
public abstract class AbstractElement implements byow.Core.element.Element {

    /**
     * Board reference.
     */
    private byow.Core.board.Board board;

    /**
     * Current location on the board.
     */
    private byow.Core.board.Point point;

    /**
     * Current teTile;
     */
    private byow.TileEngine.TETile teTile;

    private Map<Point, int[]> lightSourceMap;

    private boolean isTheLightTurnedOn;

    public AbstractElement(byow.Core.board.Board board, byow.Core.board.Point point, byow.TileEngine.TETile teTile,
        List<byow.Core.element.LightSource> lightSources) {
        this.board = board;
        this.point = point;
        this.teTile = teTile;
        this.board.setTETile(this.point, this.teTile);
        this.lightSourceMap = initLightSourceMap(lightSources);
    }

    @Override
    public void move(Pathway.Direction direction) {
        Point next = Point.of(point.getX() + direction.getPathwayDiff()[0],
                point.getY() + direction.getPathwayDiff()[1]);
        if (isValidTETile(next)) {
            // Set current location as floor.
            if (isTheLightTurnedOn && lightSourceMap.containsKey(point)) {
                int[] p = lightSourceMap.get(point);
                TETile texture = p[2] == 0 ? Tileset.FLOOR_FULL_LIGHT : Tileset.FLOOR_HALF_LIGHT;
                board.setTETile(point, texture);
            } else {
                board.setTETile(point, byow.TileEngine.Tileset.FLOOR);
            }
            // Update new point.
            point = next;
            // Update the board.
            board.setTETile(point, teTile);
        }
    }

    @Override
    public void move(char c) {
        if (c == 'W') {
            move(Pathway.Direction.UP);
        } else if (c == 'S') {
            move(Pathway.Direction.DOWN);
        } else if (c == 'A') {
            move(Pathway.Direction.LEFT);
        } else if (c == 'D') {
            move(Pathway.Direction.RIGHT);
        }
    }

    @Override
    public TETile getTETile() {
        return teTile;
    }

    public Board getBoard() {
        return board;
    }

    public Point getPoint() {
        return point;
    }

    public TETile getTeTile() {
        return teTile;
    }

    public boolean isTheLightTurnedOn() {
        return isTheLightTurnedOn;
    }

    public void setTheLightTurnedOn(boolean theLightTurnedOn) {
        isTheLightTurnedOn = theLightTurnedOn;
    }

    private Map<Point, int[]> initLightSourceMap(List<byow.Core.element.LightSource> lightSources) {
        Map<Point, int[]> result = new HashMap<>();
        if (lightSources == null || lightSources.isEmpty()) {
            return result;
        }
        for (byow.Core.element.LightSource lightSource : lightSources) {
            result.putAll(lightSource.getLightPointsMap());
        }
        return result;
    }
}
