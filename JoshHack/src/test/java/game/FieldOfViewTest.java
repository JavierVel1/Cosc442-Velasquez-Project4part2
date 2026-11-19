package game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FieldOfViewTest {
    private World world;

    @BeforeEach
    void setUp() {
        Tile[][][] tiles = new Tile[7][7][2];
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                for (int z = 0; z < 2; z++) {
                    tiles[x][y][z] = Tile.FLOOR;
                }
            }
        }
        world = new World(tiles);
    }

    @Test
    void updateMarksOnlyRadiusTilesVisible() {
        FieldOfView fov = new FieldOfView(world);

        fov.update(3, 3, 0, 2);

        assertTrue(fov.isVisible(3, 3, 0));
        assertTrue(fov.isVisible(2, 3, 0));
        assertFalse(fov.isVisible(0, 0, 0));
        assertFalse(fov.isVisible(-1, 3, 0));
        assertFalse(fov.isVisible(3, 3, 1));
    }

    @Test
    void wallsStopSightAndAreRemembered() {
        world.dig(2, 3, 0);
        Tile[][][] inaccessibleTiles = new Tile[7][7][2];
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                for (int z = 0; z < 2; z++) {
                    inaccessibleTiles[x][y][z] = Tile.FLOOR;
                }
            }
        }
        inaccessibleTiles[2][3][0] = Tile.WALL;
        World wallWorld = new World(inaccessibleTiles);
        FieldOfView fov = new FieldOfView(wallWorld);

        fov.update(1, 3, 0, 3);

        assertTrue(fov.isVisible(2, 3, 0));
        assertEquals(Tile.WALL, fov.tile(2, 3, 0));
        assertFalse(fov.isVisible(3, 3, 0));
    }

    @Test
    void updateAnotherDepthChangesActiveDepth() {
        FieldOfView fov = new FieldOfView(world);

        fov.update(3, 3, 0, 2);
        assertTrue(fov.isVisible(3, 3, 0));

        fov.update(3, 3, 1, 2);
        assertTrue(fov.isVisible(3, 3, 1));
        assertFalse(fov.isVisible(3, 3, 0));
    }

    @Test
    void outOfBoundsCoordinatesNeverBecomeVisible() {
        FieldOfView fov = new FieldOfView(world);
        fov.update(3, 3, 0, 3);

        assertFalse(fov.isVisible(-1, 3, 0));
        assertFalse(fov.isVisible(7, 3, 0));
        assertFalse(fov.isVisible(3, -1, 0));
        assertFalse(fov.isVisible(3, 7, 0));
    }
}
