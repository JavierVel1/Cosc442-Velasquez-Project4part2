package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiAndPathfindingTest {
    private World world;
    private Creature player;
    private Creature goblin;

    @BeforeEach
    void setUp() {
        Tile[][][] tiles = new Tile[5][5][1];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                tiles[x][y][0] = Tile.FLOOR;
            }
        }
        world = new World(tiles);

        player = new Creature(world, '@', Color.WHITE, "player", 100, 10, 5);
        player.x = 3;
        player.y = 1;
        player.z = 0;
        new CreatureAi(player);

        goblin = new Creature(world, 'g', Color.GREEN, "goblin", 20, 5, 2);
        goblin.x = 1;
        goblin.y = 1;
        goblin.z = 0;
    }

    @Test
    void goblinPursuesVisiblePlayerAlongShortestPath() {
        World corridor = createCorridorWorld();
        player = new Creature(corridor, '@', Color.WHITE, "player", 100, 10, 5);
        player.x = 3;
        player.y = 1;
        player.z = 0;
        goblin = new Creature(corridor, 'g', Color.GREEN, "goblin", 20, 5, 2);
        goblin.x = 1;
        goblin.y = 1;
        goblin.z = 0;
        new CreatureAi(player);
        new GoblinAi(goblin, player);
        corridor.add(player);
        corridor.add(goblin);

        goblin.update();

        assertEquals(2, goblin.x);
        assertEquals(1, goblin.y);
        assertTrue(corridor.tile(goblin.x, goblin.y, goblin.z).isGround());
    }

    @Test
    void goblinSeesPlayerOnlyWithinVisionAndSameDepth() {
        CreatureAi ai = new CreatureAi(goblin);

        assertTrue(ai.canSee(3, 1, 0));
        assertFalse(ai.canSee(11, 1, 0));
        assertFalse(ai.canSee(3, 1, 1));
    }

    @Test
    void creatureAiDoesNotEnterWalls() {
        World wallWorld = createWorld(2, 1);
        goblin = new Creature(wallWorld, 'g', Color.GREEN, "goblin", 20, 5, 2);
        goblin.x = 1;
        goblin.y = 1;
        goblin.z = 0;
        CreatureAi ai = new CreatureAi(goblin);

        ai.onEnter(2, 1, 0, wallWorld.tile(2, 1, 0));

        assertEquals(1, goblin.x);
        assertEquals(1, goblin.y);
    }

    @Test
    void playerAiRecordsNotificationsAndNoticesItems() {
        List<String> messages = new ArrayList<>();
        FieldOfView fov = new FieldOfView(world);
        PlayerAi ai = new PlayerAi(player, messages, fov);
        Item item = new Item('!', Color.WHITE, "potion", null);
        world.addAtEmptySpace(item, 2, 2, 0);
        fov.update(2, 2, 0, 2);

        ai.onEnter(2, 2, 0, Tile.FLOOR);

        assertEquals(2, player.x);
        assertEquals(2, player.y);
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).contains("potion"));
    }

    @Test
    void pathfinderRoutesAroundAnObstacle() {
        World wallWorld = createWorld(2, 1);
        goblin = new Creature(wallWorld, 'g', Color.GREEN, "goblin", 20, 5, 2);
        goblin.x = 1;
        goblin.y = 1;
        goblin.z = 0;
        new CreatureAi(goblin);
        wallWorld.add(goblin);

        Path path = new Path(goblin, 3, 1);

        assertNotNull(path.points());
        assertFalse(path.points().isEmpty());
        assertEquals(3, path.points().get(path.points().size() - 1).x);
        assertEquals(1, path.points().get(path.points().size() - 1).y);

        for (Point point : path.points()) {
            assertFalse(wallWorld.tile(point.x, point.y, point.z) == Tile.WALL);
        }
    }

    @Test
    void fieldOfViewStopsAtWalls() {
        World wallWorld = createWorld(2, 1);
        FieldOfView fov = new FieldOfView(wallWorld);

        fov.update(1, 1, 0, 3);

        assertTrue(fov.isVisible(2, 1, 0));
        assertFalse(fov.isVisible(3, 1, 0));
    }

    private World createWorld(int wallX, int wallY) {
        Tile[][][] tiles = new Tile[5][5][1];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                tiles[x][y][0] = Tile.FLOOR;
            }
        }
        tiles[wallX][wallY][0] = Tile.WALL;
        return new World(tiles);
    }

    private World createCorridorWorld() {
        Tile[][][] tiles = new Tile[5][5][1];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                tiles[x][y][0] = y == 1 ? Tile.FLOOR : Tile.WALL;
            }
        }
        return new World(tiles);
    }
}
