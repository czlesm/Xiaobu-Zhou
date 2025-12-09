package byow.Core.board;

import java.util.Random;

/**
 * The open path to a room, a room could have one or more different paths. A hallway is just a room with multiple pathways,
 * just like when we build a house.
 *
 * @author Cosette Zhou
 */
public class Pathway {

    private byow.Core.board.Point point;

    private boolean isUsed;

    private Direction direction;

    /**
     * The pathway in between two different rooms.
     *
     * @param point
     * @param isUsed
     * @param direction
     */
    public Pathway(byow.Core.board.Point point, boolean isUsed, Direction direction) {
        this.point = point;
        this.isUsed = isUsed;
        this.direction = direction;
    }

    /**
     * Given pathway point, isUsed flag and direction, generate pathway.
     *
     * @param point
     * @param isUsed
     * @param direction
     * @return
     */
    public static Pathway getPathway(byow.Core.board.Point point,
         boolean isUsed, Direction direction) {
        if (direction == null) {
            return null;
        }
        return new Pathway(point, isUsed, direction);
    }

    /**
     * Given center of the room, isUsed flag and direction, generate pathway.
     *
     * @param center
     * @param isUsed
     * @param direction
     * @return
     */
    public static Pathway getPathwayFromCenter(byow.Core.board.Point center,
        boolean isUsed, Direction direction) {
        if (direction == null) {
            return null;
        }
        return new Pathway(byow.Core.board.Point.of(center.getX() + direction.pathwayDiff[0],
                center.getY() + direction.pathwayDiff[1]), isUsed, direction);
    }

    public byow.Core.board.Point getPoint() {
        return point;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void use() {
        this.isUsed = true;
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Generate a new connected room parameters object given current pathway information.
     *
     * @return
     */
    public Params getNewConnectedRoomParams() {
        // Just generate the reverse direction of the room.
        if (direction == Direction.UP) {
            return Params.of(byow.Core.board.Point.of(point.getX() + direction.newRoomCenterDiff[0],
                    point.getY() + direction.newRoomCenterDiff[1]), Direction.DOWN, point);
        } else if (direction == Direction.DOWN) {
            return Params.of(byow.Core.board.Point.of(point.getX() + direction.newRoomCenterDiff[0],
                    point.getY() + direction.newRoomCenterDiff[1]), Direction.UP, point);
        } else if (direction == Direction.LEFT) {
            return Params.of(byow.Core.board.Point.of(point.getX() + direction.newRoomCenterDiff[0],
                    point.getY() + direction.newRoomCenterDiff[1]), Direction.RIGHT, point);
        }
        return Params.of(byow.Core.board.Point.of(point.getX() + direction.newRoomCenterDiff[0],
                point.getY() + direction.newRoomCenterDiff[1]), Direction.LEFT, point);
    }

    /**
     * Direction of the pathway, for instance, if up, then it's the up of the given room.
     */
    public enum Direction {

        UP(new int[]{0, 1}, new int[]{0, 2}),

        DOWN(new int[]{0, -1}, new int[]{0, -2}),

        LEFT(new int[]{-1, 0}, new int[]{-2, 0}),

        RIGHT(new int[]{1, 0}, new int[]{2, 0});

        /**
         * Given center of the newly generated room, this is the pathway point for each different direction.
         */
        private int[] pathwayDiff;

        /**
         * Given current pathway of the newly generated room, this is the next room center point for each different direction.
         */
        private int[] newRoomCenterDiff;

        private Direction(int[] pathwayDiff, int[] newRoomCenterDiff) {
            this.pathwayDiff = pathwayDiff;
            this.newRoomCenterDiff = newRoomCenterDiff;
        }

        /**
         * Generate a random direction that doesn't include itself.
         *
         * @param random
         * @return
         */
        public Direction getNextRandomDirection(Random random) {
            int option = byow.Core.RandomUtils.uniform(random, 4);
            Direction selected = null;
            for (Direction dir : Direction.values()) {
                if (option-- == 0) {
                    selected = dir;
                    break;
                }
            }
            if (selected != this) {
                return selected;
            }
            return getNextRandomDirection(random);
        }

        /**
         * Get a random direction.
         *
         * @param random
         * @return
         */
        public static Direction getRandomDirection(Random random) {
            int option = byow.Core.RandomUtils.uniform(random, 4);
            for (Direction dir : Direction.values()) {
                if (option-- == 0) {
                    return dir;
                }
            }
            return null;
        }

        public int[] getPathwayDiff() {
            return pathwayDiff;
        }

        public int[] getNewRoomCenterDiff() {
            return newRoomCenterDiff;
        }
    }

    /**
     * Pojo to carry some parameters about pathways.
     */
    public static class Params {

        /**
         * The center of the room.
         */
        private byow.Core.board.Point center;

        /**
         * Direction of current pathway which is connected to.
         */
        private Direction direction;

        /**
         * The point of previous room's pathway which is connected to.
         */
        private byow.Core.board.Point prev;

        /**
         * Generate a new Params object.
         *
         * @param center
         * @param direction
         * @param prev
         * @return
         */
        public static Params of(byow.Core.board.Point center, Direction direction, byow.Core.board.Point prev) {
            return new Params(center, direction, prev);
        }

        private Params(byow.Core.board.Point center, Direction direction, byow.Core.board.Point prev) {
            this.center = center;
            this.direction = direction;
            this.prev = prev;
        }

        public byow.Core.board.Point getCenter() {
            return center;
        }

        public Direction getDirection() {
            return direction;
        }

        public byow.Core.board.Point getPrev() {
            return prev;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "center=" + center +
                    ", direction=" + direction +
                    ", prev=" + prev +
                    '}';
        }
    }

    /**
     * Wrapper to help identify the room of the pathway.
     */
    public static class PathwayWrapper {

        private byow.Core.board.Room room;

        private Pathway pathway;

        public static PathwayWrapper of(byow.Core.board.Room room, Pathway pathway) {
            return new PathwayWrapper(room, pathway);
        }

        public PathwayWrapper(byow.Core.board.Room room, Pathway pathway) {
            this.room = room;
            this.pathway = pathway;
        }

        public byow.Core.board.Room getRoom() {
            return room;
        }

        public Pathway getPathway() {
            return pathway;
        }
    }

    @Override
    public String toString() {
        return "Pathway{" +
                "point=" + point +
                ", isUsed=" + isUsed +
                ", direction=" + direction +
                '}';
    }
}