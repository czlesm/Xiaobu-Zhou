package byow.TileEngine;

import java.awt.*;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final byow.TileEngine.TETile AVATAR = new byow.TileEngine.TETile('@', Color.white, Color.black, "you");
    public static final byow.TileEngine.TETile WALL = new byow.TileEngine.TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final byow.TileEngine.TETile FLOOR = new byow.TileEngine.TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final byow.TileEngine.TETile FLOOR_HALF_LIGHT = new byow.TileEngine.TETile('·', new Color(128, 192, 128),
            new Color(125, 190, 250), "floor half light");
    public static final byow.TileEngine.TETile FLOOR_FULL_LIGHT = new byow.TileEngine.TETile('·', new Color(128, 192, 128),
            new Color(183, 226, 252), "floor full light");
    public static final byow.TileEngine.TETile NOTHING = new byow.TileEngine.TETile(' ', Color.black, Color.black, "nothing");
    public static final byow.TileEngine.TETile GRASS = new byow.TileEngine.TETile('"', Color.green, Color.black, "grass");
    public static final byow.TileEngine.TETile WATER = new byow.TileEngine.TETile('≈', Color.blue, Color.black, "water");
    public static final byow.TileEngine.TETile FLOWER = new byow.TileEngine.TETile('❀', Color.magenta, Color.pink, "flower");
    public static final byow.TileEngine.TETile LOCKED_DOOR = new byow.TileEngine.TETile('█', Color.orange, Color.black,
            "locked door");
    public static final byow.TileEngine.TETile UNLOCKED_DOOR = new byow.TileEngine.TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final byow.TileEngine.TETile SAND = new byow.TileEngine.TETile('▒', Color.yellow, Color.black, "sand");
    public static final byow.TileEngine.TETile MOUNTAIN = new byow.TileEngine.TETile('▲', Color.gray, Color.black, "mountain");
    public static final byow.TileEngine.TETile TREE = new byow.TileEngine.TETile('♠', Color.green, Color.black, "tree");
    public static final byow.TileEngine.TETile LIGHT_SOURCE = new byow.TileEngine.TETile('⊹', Color.blue, Color.white, "light source");
}


