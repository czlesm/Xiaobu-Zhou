package byow.Core.board;

import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.*;

/**
 * Builder to build the board and include different board information.
 *
 * @author Cosette Zhou
 */
public class GameBoard implements Serializable {

    private static final int DEFAULT_ENEMY_CNT = 3;

    private static final int DEFAULT_FLOWER_CNT = 8;

    private static final int DEFAULT_VISION_MATRIX_RANGE = 6;

    private byow.Core.board.Board board;

    private Random rand;

    private long seed;

    private boolean isDoorUnlocked;

    private byow.Core.element.LockedDoor lockedDoor;

    private byow.Core.element.UnlockedDoor unlockedDoor;

    private List<byow.Core.element.Flower> flowers;

    private List<byow.Core.element.Enemy> enemies;

    private byow.Core.element.Player player;

    private List<byow.Core.element.LightSource> lightSources;

    private boolean isVisionMatrixEnabled;

    /**
     * This is the matrix that we could see in our vision.
     */
    private int[][] visionMatrix;

    /**
     * Generate a new game board.
     *
     * @param width
     * @param height
     * @param seed
     * @return
     */
    public static GameBoard of(int width, int height, long seed) {
        return new GameBoard(width, height, seed);
    }

    public byow.Core.board.Board getBoard() {
        return board;
    }

    /**
     * Switch the light.
     */
    public void switchTheLight() {
        boolean isTurnedOn = false;
        for (byow.Core.element.LightSource lightSource : lightSources) {
            lightSource.switchTheLight();
            isTurnedOn = lightSource.isTurnedOn();
        }
        // Notify all clients.
        player.setTheLightTurnedOn(isTurnedOn);
        for (byow.Core.element.Enemy enemy : enemies) {
            enemy.setTheLightTurnedOn(isTurnedOn);
        }
        for (byow.Core.element.Flower flower : flowers) {
            flower.setTheLightTurnedOn(isTurnedOn);
        }
    }

    /**
     * Only see limited area.
     *
     * @return
     */
    public byow.Core.board.Board getBoardInVision() {
        if (!isVisionMatrixEnabled) {
            return board;
        }
        Set<byow.Core.board.Point> lightedPoints = new HashSet<>();
        for (int[] diff : visionMatrix) {
            int nx = player.getPoint().getX() + diff[0];
            int ny = player.getPoint().getY() + diff[1];
            lightedPoints.add(byow.Core.board.Point.of(nx, ny));
        }
        byow.Core.board.Board vision = new byow.Core.board.Board(board.getWidth(), board.getHeight());
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                byow.Core.board.Point np = byow.Core.board.Point.of(i, j);
                if (lightedPoints.contains(np)) {
                    vision.setTETile(np, board.getTEtile(np));
                }
            }
        }
        return vision;
    }

    public Random getRand() {
        return rand;
    }

    public long getSeed() {
        return seed;
    }

    public boolean isDoorUnlocked() {
        return isDoorUnlocked;
    }

    public byow.Core.element.LockedDoor getLockedDoor() {
        return lockedDoor;
    }

    public byow.Core.element.UnlockedDoor getUnlockedDoor() {
        return unlockedDoor;
    }

    public List<byow.Core.element.Flower> getFlowers() {
        return flowers;
    }

    public List<byow.Core.element.Enemy> getEnemies() {
        return enemies;
    }

    public byow.Core.element.Player getPlayer() {
        return player;
    }

    /**
     * Switch map mists.
     */
    public void switchMapMists() {
        this.isVisionMatrixEnabled = !this.isVisionMatrixEnabled;
    }

    /**
     * Unlock the door.
     */
    public void unlockDoor() {
        this.isDoorUnlocked = true;
        this.unlockedDoor = this.lockedDoor.unlock();
        this.lockedDoor = null;
    }

    private GameBoard(int width, int height, long seed) {
        this.seed = seed;
        this.rand = new Random(seed);
        generateMatrix();
        generateBoard(width, height, rand);
    }

    /**
     * Generate one board.
     *
     * @param width
     * @param height
     * @param rand
     * @return
     */
    private void generateBoard(int width, int height, Random rand) {
        this.board = new byow.Core.board.Board(width, height);

        // Create one room and try to avoid hit the boarder.
        int initX = byow.Core.RandomUtils.uniform(rand, 3, width - 5);
        int initY = byow.Core.RandomUtils.uniform(rand, 3, height - 5);
        // Init the first room with pathway on the right.
        byow.Core.board.Point initPoint = byow.Core.board.Point.of(initX, initY);
        // Since this is the first room, we don't have any pre attached pathway yet.
        byow.Core.board.Point initPrev = byow.Core.board.Point.of(initX + 2, initY);
        byow.Core.board.Pathway.Params initParam = byow.Core.board.Pathway.Params.of(initPoint,
                byow.Core.board.Pathway.Direction.RIGHT, initPrev);
        byow.Core.board.Room initRoom = byow.Core.board.Room.of(board, initParam, rand);

        // Init a list to contain all rooms.
        List<byow.Core.board.Room> rooms = new ArrayList<>();
        rooms.add(initRoom);

        // Collect all possible pathways to build more rooms.
        List<byow.Core.board.Pathway.PathwayWrapper> availablePathways = new ArrayList<>();
        availablePathways.addAll(initRoom.getPathways().stream().filter(p -> !p.isUsed())
                .map(p -> byow.Core.board.Pathway.PathwayWrapper.of(initRoom, p)).toList());

        // Create a second room.
        byow.Core.board.Room secondRoom = byow.Core.board.Room.of(board, initRoom.getPathway().getNewConnectedRoomParams(), rand);
        rooms.add(secondRoom);
        availablePathways.addAll(secondRoom.getPathways().stream().filter(p -> !p.isUsed())
                .map(p -> byow.Core.board.Pathway.PathwayWrapper.of(secondRoom, p)).toList());

        // Collect all not used door and randomly select one as the locked door.
        List<byow.Core.board.Pathway> notUsedPathways = new ArrayList<>();

        // Generate pathways.
        while (!availablePathways.isEmpty()) {
            byow.Core.board.Pathway.PathwayWrapper availableWrapper = getARandomPathway(rand, availablePathways, notUsedPathways);
            byow.Core.board.Room from = availableWrapper.getRoom();
            from.usePathway(availableWrapper.getPathway());
            byow.Core.board.Room next = byow.Core.board.Room.of(board, availableWrapper.getPathway()
                    .getNewConnectedRoomParams(), rand);
            rooms.add(next);
            availablePathways.addAll(next.getPathways().stream().filter(p -> !p.isUsed())
                    .map(p -> byow.Core.board.Pathway.PathwayWrapper.of(next, p)).toList());
            availablePathways.removeIf(p -> !isValidParam(p.getPathway(), notUsedPathways));
        }

        // System.out.println("Done generating basic rooms.");

        // Randomly generate light sources.
        generateRandomLightSources(rooms);
        // Randomly generate a locked door in the not used pathways.
        generateRandomLockedDoor(notUsedPathways);
        // Randomly generate player.
        generatePlayer();
        // Randomly generate enemies.
        generateRandomEnemies();
        // Randomly generate flowers.
        generateRandomFlowers();
    }

    /**
     * Generate a square matrix for vision.
     */
    private void generateMatrix() {
        int len = (DEFAULT_VISION_MATRIX_RANGE * 2 + 1) * (DEFAULT_VISION_MATRIX_RANGE * 2 + 1);
        int index = 0;
        this.visionMatrix = new int[len][2];
        for (int i = -DEFAULT_VISION_MATRIX_RANGE; i <= DEFAULT_VISION_MATRIX_RANGE; i++) {
            for (int j = -DEFAULT_VISION_MATRIX_RANGE; j <= DEFAULT_VISION_MATRIX_RANGE; j++) {
                visionMatrix[index][0] = i;
                visionMatrix[index][1] = j;
                index++;
            }
        }
    }

    /**
     * Get a random floor to fill in different element.
     *
     * @return
     */
    private byow.Core.board.Point getARandomFloor() {
        int x = byow.Core.RandomUtils.uniform(rand, 0, board.getWidth());
        int y = byow.Core.RandomUtils.uniform(rand, 0, board.getHeight());
        byow.Core.board.Point result = byow.Core.board.Point.of(x, y);
        while (board.getTEtile(result) != Tileset.FLOOR) {
            x = byow.Core.RandomUtils.uniform(rand, 0, board.getWidth());
            y = byow.Core.RandomUtils.uniform(rand, 0, board.getHeight());
            result = byow.Core.board.Point.of(x, y);
        }
        return result;
    }

    private void generateRandomLightSources(List<byow.Core.board.Room> rooms) {
        lightSources = new ArrayList<>();
        for (byow.Core.board.Room r : rooms) {
            if (r.getLightSource() != null) {
                lightSources.add(r.getLightSource());
            }
        }
    }

    private void generateRandomLockedDoor(List<byow.Core.board.Pathway> notUsedPathways) {
        // Generate a random exit door.
        byow.Core.board.Pathway pathway = notUsedPathways.get(byow.Core.RandomUtils.uniform(rand, notUsedPathways.size()));
        this.lockedDoor = byow.Core.element.LockedDoor.of(board, pathway.getPoint());
        this.isDoorUnlocked = false;
    }

    private void generatePlayer() {
        player = byow.Core.element.Player.of(board, getARandomFloor(), lightSources);
    }

    private void generateRandomEnemies() {
        enemies = new ArrayList<>();
        for (int i = 0; i < DEFAULT_ENEMY_CNT; i++) {
            byow.Core.board.Point floor = getARandomFloor();
            enemies.add(byow.Core.element.Enemy.of(board, floor, lightSources));
        }
    }

    private void generateRandomFlowers() {
        flowers = new ArrayList<>();
        for (int i = 0; i < DEFAULT_FLOWER_CNT; i++) {
            byow.Core.board.Point floor = getARandomFloor();
            flowers.add(byow.Core.element.Flower.of(board, floor, lightSources));
        }
    }

    /**
     * Generate a random pathway for use.
     *
     * @param rand
     * @param availablePathways
     * @param notUsedPathways
     * @return
     */
    private byow.Core.board.Pathway.PathwayWrapper getARandomPathway(Random rand,
        List<byow.Core.board.Pathway.PathwayWrapper> availablePathways, List<byow.Core.board.Pathway> notUsedPathways) {
        if (availablePathways == null || availablePathways.isEmpty()) {
            return null;
        }
        byow.Core.board.Pathway.PathwayWrapper wrapper = availablePathways
                .remove(byow.Core.RandomUtils.uniform(rand, availablePathways.size()));
        if (isValidParam(wrapper.getPathway(), notUsedPathways)) {
            return wrapper;
        } else {
            return getARandomPathway(rand, availablePathways, notUsedPathways);
        }
    }

    private boolean isValidParam(byow.Core.board.Pathway pathway, List<byow.Core.board.Pathway> notUsedPathways) {
        byow.Core.board.Pathway.Params params = pathway.getNewConnectedRoomParams();
        byow.Core.board.Point center = params.getCenter();
        for (int i = center.getX() - 1; i <= center.getX() + 1; i++) {
            for (int j = center.getY() - 1; j <= center.getY() + 1; j++) {
                byow.Core.board.Point point = byow.Core.board.Point.of(i, j);
                if (!board.isValid(point) || board.getTEtile(point) != Tileset.NOTHING) {
                    notUsedPathways.add(pathway);
                    return false;
                }
            }
        }
        return true;
    }
}