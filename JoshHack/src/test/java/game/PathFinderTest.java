package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathFinderTest {
    private Tile[][][] tiles;
    private World world;
    private Creature creature;

    @BeforeEach
    void setUp() {
        tiles = new Tile[7][7][2];
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                for (int z = 0; z < 2; z++) {
                    tiles[x][y][z] = Tile.FLOOR;
                }
            }
        }
        world = new World(tiles);
        creature = new Creature(world, 'Z', Color.GREEN, "Zombie", 30, 5, 2);
        creature.x = 1;
        creature.y = 2;
        creature.z = 0;
        new CreatureAi(creature);
    }

    @Test
    void openMapProducesNonemptyPathToDestination() {
        List<Point> path = new PathFinder().findPath(
            creature, new Point(1, 2, 0), new Point(5, 2, 0), 300);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertEquals(new Point(5, 2, 0), path.get(path.size() - 1));
    }

    @Test
    void detoursAroundWallMaze() {
        tiles[3][1][0] = Tile.WALL;
        tiles[3][2][0] = Tile.WALL;
        tiles[3][3][0] = Tile.WALL;

        List<Point> path = new PathFinder().findPath(
            creature, new Point(1, 2, 0), new Point(5, 2, 0), 300);

        assertNotNull(path);
        assertEquals(new Point(5, 2, 0), path.get(path.size() - 1));
        for (Point point : path) {
            assertNotEquals(Tile.WALL, world.tile(point.x, point.y, point.z));
        }
    }

    @Test
    void fullyEnclosedDestinationReturnsNull() {
        tiles[5][2][0] = Tile.WALL;
        tiles[4][2][0] = Tile.WALL;
        tiles[5][1][0] = Tile.WALL;
        tiles[5][3][0] = Tile.WALL;
        tiles[6][2][0] = Tile.WALL;

        List<Point> path = new PathFinder().findPath(
            creature, new Point(1, 2, 0), new Point(5, 2, 0), 300);

        assertNull(path);
    }

    @Test
    void pathNeverVisitsAnotherDepth() {
        List<Point> path = new PathFinder().findPath(
            creature, new Point(1, 2, 0), new Point(5, 2, 0), 300);

        assertNotNull(path);
        for (Point point : path) {
            assertEquals(0, point.z);
        }
    }

    @Test
    void repeatedSearchesDoNotReusePreviousState() {
        PathFinder finder = new PathFinder();

        List<Point> first = finder.findPath(
            creature, new Point(1, 2, 0), new Point(5, 2, 0), 300);
        tiles[3][2][0] = Tile.WALL;
        tiles[3][3][0] = Tile.WALL;
        List<Point> second = finder.findPath(
            creature, new Point(1, 2, 0), new Point(5, 2, 0), 300);

        assertNotNull(first);
        assertNotNull(second);
        assertEquals(new Point(5, 2, 0), second.get(second.size() - 1));
        assertFalse(second.isEmpty());
    }

    @Test
    void wallsNeverAppearInReturnedPath() {
        for (Point point : new ArrayList<Point>()) {
            assertNotEquals(Tile.WALL, world.tile(point.x, point.y, point.z));
        }
        tiles[2][2][0] = Tile.WALL;

        List<Point> path = new PathFinder().findPath(
            creature, new Point(1, 2, 0), new Point(5, 2, 0), 300);

        assertNotNull(path);
        for (Point point : path) {
            assertNotEquals(Tile.WALL, world.tile(point.x, point.y, point.z));
        }
    }
}
