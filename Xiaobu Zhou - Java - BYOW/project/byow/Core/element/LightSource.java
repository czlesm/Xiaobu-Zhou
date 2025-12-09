package byow.Core.element;

import byow.Core.board.Board;
import byow.Core.board.Pathway;
import byow.Core.board.Point;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

/**
 * Light source object which stands for light source.
 *
 * @author Cosette Zhou
 */
public class LightSource extends byow.Core.element.AbstractElement {

    /**
     * Light radius for full light.
     */
    public static final int DEFAULT_FULL_RADIUS = 2;

    /**
     * Light radius for half light.
     */
    public static final int DEFAULT_HALF_RADIUS = 4;

    /**
     * This will be fixed when lightsource is created, so we don't need to create it all the time.
     */
    private Map<Point, int[]> lightPointsMap;

    /**
     * Indicate whether the light should be turned on.
     */
    private boolean isTurnedOn;

    /**
     * Generate a new LightSource instance.
     *
     * @param board
     * @param point
     * @param upLeft
     * @param downRight
     * @return
     */
    public static LightSource of(byow.Core.board.Board board, byow.Core.board.Point point, Point upLeft, Point downRight) {
        return new LightSource(board, point, null, Tileset.LIGHT_SOURCE, upLeft, downRight);
    }

    public LightSource(Board board, Point point, List<LightSource> lightSources, TETile teTile, Point upLeft, Point downRight) {
        super(board, point, teTile, lightSources);
        init(upLeft, downRight);
    }

    /**
     * Turn on/off the light.
     */
    public void switchTheLight() {
        this.isTurnedOn = !this.isTurnedOn;
        if (isTurnedOn) {
            turnOnTheLight();
        } else {
            turnOffTheLight();
        }
    }

    public boolean isTurnedOn() {
        return isTurnedOn;
    }

    /**
     * Turn on the light.
     * NOTE: For simplicity, we don't actually change the background of exising elements on the board.
     */
    public void turnOnTheLight() {
        // System.out.println("Turn on the light.");
        for (int[] p : lightPointsMap.values()) {
            // If the area contains other stuff.
            if (!Objects.equals(getBoard().getTEtile(Point.of(p[0], p[1])), Tileset.FLOOR)) {
                continue;
            }
            if (p[2] == 0) {
                getBoard().setTETile(Point.of(p[0], p[1]), Tileset.FLOOR_FULL_LIGHT);
            } else {
                getBoard().setTETile(Point.of(p[0], p[1]), Tileset.FLOOR_HALF_LIGHT);
            }
        }
    }

    /**
     * Turn off the light.
     */
    public void turnOffTheLight() {
        // System.out.println("Turn off the light.");
        for (int[] p : lightPointsMap.values()) {
            // If the area contains other stuff.
            if (!Objects.equals(getBoard().getTEtile(Point.of(p[0], p[1])), Tileset.FLOOR_HALF_LIGHT)
                && !Objects.equals(getBoard().getTEtile(Point.of(p[0], p[1])), Tileset.FLOOR_FULL_LIGHT)) {
                continue;
            }
            getBoard().setTETile(Point.of(p[0], p[1]), Tileset.FLOOR);
        }
    }

    public void disable() {
        getBoard().setTETile(getPoint(), Tileset.FLOOR);
    }

    public Map<Point, int[]> getLightPointsMap() {
        return lightPointsMap;
    }

    private void init(Point upLeft, Point downRight) {
        lightPointsMap = new HashMap<>();
        generateLightPoints(getPoint(), upLeft, downRight, new HashSet<>());
    }

    /**
     * DFS to get all places which are required to be turned on and off.
     *
     * @param cp
     * @param visited
     */
    private void generateLightPoints(Point cp, Point upLeft, Point downRight, Set<Point> visited) {
        // If it's visited.
        if (visited.contains(cp)) {
            return;
        }
        int w = getBoard().getWidth();
        int h = getBoard().getHeight();
        // If it's not within the board.
        if (cp.getX() <= upLeft.getX() || cp.getX() >= downRight.getX()
           || cp.getY() <= downRight.getY() || cp.getY() >= upLeft.getY()) {
            return;
        }
        // If it's not within the radius.
        if (cp.getX() < getPoint().getX() - DEFAULT_HALF_RADIUS || cp.getX() > getPoint().getX() + DEFAULT_HALF_RADIUS
            || cp.getY() < getPoint().getY() - DEFAULT_HALF_RADIUS || cp.getY() > getPoint().getY() + DEFAULT_HALF_RADIUS) {
            return;
        }
        // If the texture is not floor. when generating the light points, no lights are turning on.
        if (getBoard().getTEtile(cp) != Tileset.FLOOR && getBoard().getTEtile(cp) != Tileset.LIGHT_SOURCE) {
            return;
        }
        visited.add(cp);
        if (!cp.equals(getPoint()) && getBoard().getTEtile(cp) != Tileset.LIGHT_SOURCE) {
            // If it's the light source itself, skip.
            // radius indicator: 0 -> full radius, 1 -> half radius.
            lightPointsMap.put(Point.of(cp.getX(), cp.getY()), new int[] {cp.getX(), cp.getY(), isFullRadius(cp) ? 0 : 1});
        }

        // Go through all directions.
        generateLightPoints(Point.getDirection(cp, Pathway.Direction.UP), upLeft, downRight, visited);
        generateLightPoints(Point.getDirection(cp, Pathway.Direction.DOWN), upLeft, downRight, visited);
        generateLightPoints(Point.getDirection(cp, Pathway.Direction.LEFT), upLeft, downRight, visited);
        generateLightPoints(Point.getDirection(cp, Pathway.Direction.RIGHT), upLeft, downRight, visited);
    }

    /**
     * Verify whether point is within full radius area.
     *
     * @param cp
     * @return
     */
    private boolean isFullRadius(Point cp) {
        return cp.getX() >= getPoint().getX() - DEFAULT_FULL_RADIUS
                && cp.getX() <= getPoint().getX() + DEFAULT_FULL_RADIUS
                && cp.getY() >= getPoint().getY() - DEFAULT_FULL_RADIUS
                && cp.getY() <= getPoint().getY() + DEFAULT_FULL_RADIUS;
    }

    @Override
    public boolean isValidTETile(Point point) {
        return true;
    }
}
