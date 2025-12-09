package byow.Core.board;

import java.io.Serializable;
import java.util.Objects;

/**
 * Point object, basically x is the row and y is the column.
 *
 * @author Cosette Zhou
 */
public class Point implements Serializable {
    private int x;
    private int y;

    /**
     * Generate a new Point instance.
     *
     * @param x
     * @param y
     * @return
     */
    public static Point of(int x, int y) {
        return new Point(x, y);
    }

    private Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point) obj;
        return this.x == other.x & this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Util to get point in all directions.
     *
     * @param point
     * @param direction
     * @return
     */
    public static Point getDirection(Point point, byow.Core.board.Pathway.Direction direction) {
        return Point.of(point.getX() + direction.getPathwayDiff()[0], point.getY() + direction.getPathwayDiff()[1]);
    }
}
