package allreverse;
import allreverse.Coord;

import java.util.ArrayList;
import java.util.List;

public final class Ranges {

    private Ranges() {
        }

    private static Coord size;
    private static ArrayList<Coord> allCoords;

    public static void setSize(Coord newSize) {
        size = newSize;
        allCoords = new ArrayList<>();
        for (int y = 0; y < size.y; y++)
            for (int x = 0; x < size.x; x++)
                allCoords.add(new Coord(x, y));
    }

    public static Coord getSize() {
        return size;
    }

    public static List<Coord> getAllCoords() {
        return allCoords;
    }

    public static boolean inRange(Coord coord) {
        return coord.x >= 0 && coord.x < size.x &&
                coord.y >= 0 && coord.y < size.y;
    }
}