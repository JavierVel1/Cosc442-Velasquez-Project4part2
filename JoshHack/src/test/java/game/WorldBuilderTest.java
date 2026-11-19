package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WorldBuilderTest {

    @Test
    void worldReturnsBoundsTileOutsideAnyDimension() {
        Tile[][][] tiles = new Tile[3][4][2];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 2; z++) {
                    tiles[x][y][z] = Tile.FLOOR;
                }
            }
        }
        World world = new World(tiles);

        assertEquals(Tile.BOUNDS, world.tile(-1, 0, 0));
        assertEquals(Tile.BOUNDS, world.tile(3, 0, 0));
        assertEquals(Tile.BOUNDS, world.tile(0, 4, 0));
        assertEquals(Tile.BOUNDS, world.tile(0, 0, 2));
    }

    @Test
    void makeCavesBuildsACompleteWorldWithAValidExit() {
        int width = 90;
        int height = 32;
        int depth = 5;

        World world = new WorldBuilder(width, height, depth)
                .makeCaves()
                .build();

        assertEquals(width, world.width());
        assertEquals(height, world.height());
        assertEquals(depth, world.depth());

        int exitStairs = 0;
        int groundTiles = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    Tile tile = world.tile(x, y, z);
                    assertNotNull(tile);
                    if (tile.isGround())
                        groundTiles++;
                    if (z == 0 && tile == Tile.STAIRS_UP)
                        exitStairs++;
                    if (tile == Tile.STAIRS_DOWN) {
                        assertEquals(Tile.STAIRS_UP, world.tile(x, y, z + 1));
                    }
                    if (tile == Tile.STAIRS_UP && z > 0) {
                        assertEquals(Tile.STAIRS_DOWN, world.tile(x, y, z - 1));
                    }
                }
            }
        }

        assertEquals(1, exitStairs);
        assertTrue(groundTiles > 0);
    }
}
