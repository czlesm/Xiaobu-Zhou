package byow.Core.board;

import byow.Core.element.LightSource;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Room object, which may contains one or more pathways.
 *
 * @author Cosette Zhou
 */
public class Room {

    /**
     * The board it belongs to.
     */
    private byow.Core.board.Board board;

    /**
     * Pathway which is used to build the room that connected to other rooms.
     */
    private byow.Core.board.Point pathwayPoint;

    /**
     * Pathway that it is built with.
     */
    private byow.Core.board.Pathway pathway;

    /**
     * Pathway that is connected to and built from.
     */
    private byow.Core.board.Point prev;

    /**
     * Pathway direction which is used to build the room that connected to other rooms.
     */
    private byow.Core.board.Pathway.Direction direction;

    private byow.Core.board.Point center;

    /**
     * We use two points on the 2-D plane to build the room, up left corner and down right corner.
     */
    private byow.Core.board.Point upLeft;

    /**
     * We use two points on the 2-D plane to build the room, up left corner and down right corner.
     */
    private byow.Core.board.Point downRight;

    /**
     * All possible pathways that this room can have.
     */
    private List<byow.Core.board.Pathway> pathways;

    /**
     * Random generator with seeds.
     */
    private Random rand;

    private byow.Core.element.LightSource lightSource;

    /**
     * Create a new instance of Room.
     *
     * @param board
     * @param params
     * @param rand
     * @return
     */
    public static Room of(byow.Core.board.Board board, byow.Core.board.Pathway.Params params, Random rand) {
        return new Room(board, params, rand);
    }

    /**
     * Generate a new room given parameters. At the beginning, our room is just a 3 * 3 room, we'll do some random
     * extending so that it looks like a different room.
     *
     * @param board
     * @param params
     * @param rand
     */
    private Room(byow.Core.board.Board board, byow.Core.board.Pathway.Params params, Random rand) {
        this.board = board;
        this.center = params.getCenter();
        this.direction = params.getDirection();
        this.prev = params.getPrev();
        this.rand = rand;
        upLeft = byow.Core.board.Point.of(center.getX() - 1, center.getY() + 1);
        downRight = byow.Core.board.Point.of(center.getX() + 1, center.getY() - 1);
        this.pathway = byow.Core.board.Pathway.getPathwayFromCenter(center, true, direction);
        this.pathways = new ArrayList<>();
        pathways.add(pathway);
        this.pathwayPoint = pathway.getPoint();
        drawRoom();
        randomizeRoom(rand);
        generateLightSource();
    }

    public byow.Core.board.Board getBoard() {
        return board;
    }

    public byow.Core.board.Point getPathwayPoint() {
        return pathwayPoint;
    }

    public byow.Core.board.Pathway getPathway() {
        return pathway;
    }

    public byow.Core.board.Point getPrev() {
        return prev;
    }

    public byow.Core.board.Pathway.Direction getDirection() {
        return direction;
    }

    public byow.Core.board.Point getCenter() {
        return center;
    }

    public byow.Core.board.Point getUpLeft() {
        return upLeft;
    }

    public byow.Core.board.Point getDownRight() {
        return downRight;
    }

    public Random getRand() {
        return rand;
    }

    public LightSource getLightSource() {
        return lightSource;
    }

    /**
     * Returns a list of doors that are attached to this room.
     */
    public List<byow.Core.board.Pathway> getPathways() {
        return pathways;
    }

    /**
     * Mark the pathway used.
     *
     * @param path
     */
    public void usePathway(byow.Core.board.Pathway path) {
        for (byow.Core.board.Pathway p : pathways) {
            if (p.getPoint().equals(path.getPoint())) {
                p.use();
                break;
            }
        }
    }

    /**
     * Randomize the room after creating 3 * 3 to make it looks different.
     *
     * @param rand
     */
    private void randomizeRoom(Random rand) {
        double possibility = 0.85; // given a certain possibility.
        // Within the possibility, we'll try to keep expanding the room.
        while (byow.Core.RandomUtils.uniform(rand) < possibility) {
            byow.Core.board.Pathway.Direction toDirection = direction.getNextRandomDirection(rand);
            expandWallIfCan(toDirection);
        }
        attachPossiblePathways();
    }

    /**
     * Draw a 3 * 3 room with given points.
     */
    private void drawRoom() {
        // Draw the walls.
        for (int i = upLeft.getX(); i <= downRight.getX(); i++) {
            for (int j = downRight.getY(); j <= upLeft.getY(); j++) {
                board.setTETile(byow.Core.board.Point.of(i, j), Tileset.WALL);
            }
        }

        // Draw the floors
        board.setTETile(center, Tileset.FLOOR);
        board.setTETile(pathwayPoint, Tileset.FLOOR);
        board.setTETile(prev, Tileset.FLOOR);
    }

    /**
     * Expand the wall if it can.
     *
     * @param direction
     */
    private void expandWallIfCan(byow.Core.board.Pathway.Direction direction) {
        List<byow.Core.board.Point> expandWalls = getExpandedWallPoints(direction);
        // If either points are not on the board or something are already there, just abort.
        if (!board.isValid(expandWalls) || !canDraw(expandWalls)) {
            return;
        }
        // Expand wall by 1.
        for (byow.Core.board.Point wall : expandWalls) {
            board.setTETile(wall, Tileset.WALL);
        }
        // Expand floor by 1.
        List<byow.Core.board.Point> expandFloors = getExpandedFloorPoints(direction);
        for (byow.Core.board.Point floor : expandFloors) {
            board.setTETile(floor, Tileset.FLOOR);
        }
        // refresh the coordinate.
        refreshRoomCoordinate(direction);
    }

    /**
     * Refresh the room coordinate after we expand the room.
     *
     * @param direction
     */
    private void refreshRoomCoordinate(byow.Core.board.Pathway.Direction direction) {
        if (direction == byow.Core.board.Pathway.Direction.UP) {
            upLeft = byow.Core.board.Point.of(upLeft.getX(), upLeft.getY() + 1);
        } else if (direction == byow.Core.board.Pathway.Direction.DOWN) {
            downRight = byow.Core.board.Point.of(downRight.getX(), downRight.getY() - 1);
        } else if (direction == byow.Core.board.Pathway.Direction.LEFT) {
            upLeft = byow.Core.board.Point.of(upLeft.getX() - 1, upLeft.getY());
        } else {
            downRight = byow.Core.board.Point.of(downRight.getX() + 1, downRight.getY());
        }
    }

    /**
     * Generate expanded floor points.
     *
     * @param direction
     * @return
     */
    private List<byow.Core.board.Point> getExpandedFloorPoints(byow.Core.board.Pathway.Direction direction) {
        List<byow.Core.board.Point> result = new ArrayList<>();
        if (direction == byow.Core.board.Pathway.Direction.UP) {
            for (int i = upLeft.getX() + 1; i < downRight.getX(); i++) {
                result.add(byow.Core.board.Point.of(i, upLeft.getY()));
            }
        } else if (direction == byow.Core.board.Pathway.Direction.DOWN) {
            for (int i = upLeft.getX() + 1; i < downRight.getX(); i++) {
                result.add(byow.Core.board.Point.of(i, downRight.getY()));
            }
        } else if (direction == byow.Core.board.Pathway.Direction.LEFT) {
            for (int i = downRight.getY() + 1; i < upLeft.getY(); i++) {
                result.add(byow.Core.board.Point.of(upLeft.getX(), i));
            }
        } else {
            for (int i = downRight.getY() + 1; i < upLeft.getY(); i++) {
                result.add(byow.Core.board.Point.of(downRight.getX(), i));
            }
        }
        return result;
    }

    /**
     * Collect all new possible points given certain direction that we'd like to expand.
     *
     * @param direction
     * @return
     */
    private List<byow.Core.board.Point> getExpandedWallPoints(byow.Core.board.Pathway.Direction direction) {
        List<byow.Core.board.Point> walls = new ArrayList<>();
        if (direction == byow.Core.board.Pathway.Direction.UP) {
            for (int i = upLeft.getX(); i <= downRight.getX(); i++) {
                walls.add(byow.Core.board.Point.of(i, upLeft.getY() + 1));
            }
        } else if (direction == byow.Core.board.Pathway.Direction.DOWN) {
            for (int i = upLeft.getX(); i <= downRight.getX(); i++) {
                walls.add(byow.Core.board.Point.of(i, downRight.getY() - 1));
            }
        } else if (direction == byow.Core.board.Pathway.Direction.LEFT) {
            for (int i = downRight.getY(); i <= upLeft.getY(); i++) {
                walls.add(byow.Core.board.Point.of(upLeft.getX() - 1, i));
            }
        } else {
            for (int i = downRight.getY(); i <= upLeft.getY(); i++) {
                walls.add(byow.Core.board.Point.of(upLeft.getX() + 1, i));
            }
        }
        return walls;
    }

    /**
     * For the other three different sides of the room, we can attach a possible point so that it can be used as a pathway.
     */
    private void attachPossiblePathways() {
        if (direction != byow.Core.board.Pathway.Direction.UP) {
            List<byow.Core.board.Point> wallPoints = getAllHorizontalWallsExceptBoarder(upLeft,
                    byow.Core.board.Point.of(downRight.getX(), upLeft.getY()));
            byow.Core.board.Point newWallPoint = wallPoints.remove(byow.Core.RandomUtils.uniform(rand, wallPoints.size()));
            pathways.add(byow.Core.board.Pathway.getPathway(newWallPoint, false, byow.Core.board.Pathway.Direction.UP));
        }

        if (direction != byow.Core.board.Pathway.Direction.DOWN) {
            List<byow.Core.board.Point> wallPoints = getAllHorizontalWallsExceptBoarder(
                    byow.Core.board.Point.of(upLeft.getX(), downRight.getY()), downRight);
            byow.Core.board.Point newWallPoint = wallPoints.remove(byow.Core.RandomUtils.uniform(rand, wallPoints.size()));
            pathways.add(byow.Core.board.Pathway.getPathway(newWallPoint, false, byow.Core.board.Pathway.Direction.DOWN));
        }

        if (direction != byow.Core.board.Pathway.Direction.LEFT) {
            List<byow.Core.board.Point> wallPoints = getAllVerticalWallsExceptBoarder(upLeft,
                    byow.Core.board.Point.of(upLeft.getX(), downRight.getY()));
            byow.Core.board.Point newWallPoint = wallPoints.remove(byow.Core.RandomUtils.uniform(rand, wallPoints.size()));
            pathways.add(byow.Core.board.Pathway.getPathway(newWallPoint, false, byow.Core.board.Pathway.Direction.LEFT));
        }

        if (direction != byow.Core.board.Pathway.Direction.RIGHT) {
            List<byow.Core.board.Point> wallPoints = getAllVerticalWallsExceptBoarder(
                    byow.Core.board.Point.of(downRight.getX(), upLeft.getY()), downRight);
            byow.Core.board.Point newWallPoint = wallPoints.remove(byow.Core.RandomUtils.uniform(rand, wallPoints.size()));
            pathways.add(byow.Core.board.Pathway.getPathway(newWallPoint, false, byow.Core.board.Pathway.Direction.RIGHT));
        }
    }

    /**
     * Try to get all points in between the wall of the room vertically, either left or right wall.
     * Assumption is a's y is larger than b's y.
     *
     * @param a
     * @param b
     * @return
     */
    private List<byow.Core.board.Point> getAllVerticalWallsExceptBoarder(byow.Core.board.Point a, byow.Core.board.Point b) {
        List<byow.Core.board.Point> result = new ArrayList<>();
        if (a.getX() != b.getX()) {
            return result;
        }
        for (int i = b.getY() + 1; i < a.getY(); i++) {
            result.add(byow.Core.board.Point.of(a.getX(), i));
        }
        return result;
    }

    /**
     * Try to get all points in between the wall of the room horizontally, either up or down wall.
     * Assumption is a's x is smaller than b's x.
     *
     * @param a
     * @param b
     * @return
     */
    private List<byow.Core.board.Point> getAllHorizontalWallsExceptBoarder(byow.Core.board.Point a, byow.Core.board.Point b) {
        List<byow.Core.board.Point> result = new ArrayList<>();
        if (a.getY() != b.getY()) {
            return result;
        }
        for (int i = a.getX() + 1; i < b.getX(); i++) {
            result.add(byow.Core.board.Point.of(i, a.getY()));
        }
        return result;
    }

    /**
     * Verify whether we've already drawn something on given tiles.
     *
     * @param tiles
     * @return
     */
    private boolean canDraw(List<byow.Core.board.Point> tiles) {
        for (byow.Core.board.Point tile : tiles) {
            if (board.getTEtile(tile) != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }

    private void generateLightSource() {
        // If the room is too small, there will be no light source.
        if (downRight.getX() - upLeft.getX() <= 3 || upLeft.getY() - downRight.getY() <= 3) {
            return;
        }
        byow.Core.board.Point lightsourcePoint = getARandomLightSource();
        if (lightsourcePoint == null) {
            return;
        }
        this.lightSource = byow.Core.element.LightSource.of(board, lightsourcePoint, upLeft, downRight);
    }

    /**
     * Get a random floor to fill in lightSource element in the room area.
     *
     * @return
     */
    private byow.Core.board.Point getARandomLightSource() {
        int defaultMaxRetry = 1000;
        int x = byow.Core.RandomUtils.uniform(rand, upLeft.getX() + 1, downRight.getX() - 1);
        int y = byow.Core.RandomUtils.uniform(rand, downRight.getY() + 1, upLeft.getY() - 1);
        byow.Core.board.Point result = byow.Core.board.Point.of(x, y);
        while (!isValidLightSourcePoint(result) && --defaultMaxRetry >= 0) {
            x = byow.Core.RandomUtils.uniform(rand, upLeft.getX() + 1, downRight.getX() - 1);
            y = byow.Core.RandomUtils.uniform(rand, downRight.getY() + 1, upLeft.getY() - 1);
            result = byow.Core.board.Point.of(x, y);
        }
        return result;
    }

    private boolean isValidLightSourcePoint(byow.Core.board.Point p) {
        if (board.getTEtile(p) != Tileset.FLOOR) {
            return false;
        }
        // Don't block the entrance, and by design, we don't set light source in the hallway.
        for (byow.Core.board.Pathway path: pathways) {
            byow.Core.board.Point pp = path.getPoint();
            if (byow.Core.board.Point.getDirection(p, byow.Core.board.Pathway.Direction.UP).equals(pp)
                    || byow.Core.board.Point.getDirection(p, byow.Core.board.Pathway.Direction.DOWN).equals(pp)
                    || byow.Core.board.Point.getDirection(p, byow.Core.board.Pathway.Direction.LEFT).equals(pp)
                    || byow.Core.board.Point.getDirection(p, byow.Core.board.Pathway.Direction.RIGHT).equals(pp)) {
                return false;
            }
        }
        return true;
    }
}
