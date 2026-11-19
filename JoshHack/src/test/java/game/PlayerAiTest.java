package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerAiTest {
    private World world;
    private Tile[][][] tiles;
    private FieldOfView fov;
    private List<String> messages;
    private Creature player;
    private PlayerAi ai;

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
        fov = new FieldOfView(world);
        messages = new ArrayList<String>();
        player = new Creature(world, '@', Color.WHITE, "Player", 100, 10, 5);
        player.x = 1;
        player.y = 1;
        player.z = 0;
        ai = new PlayerAi(player, messages, fov);
        world.add(player);
        fov.update(1, 1, 0, 5);
    }

    @Test
    void constructorConnectsAiAndRetainsInitialPositionState() {
        assertEquals(1, player.x);
        assertEquals(1, player.y);
        assertEquals(0, player.z);
        assertEquals(player, findPlayer());
    }

    @Test
    void onEnterMovesPlayerOntoGround() {
        ai.onEnter(2, 3, 0, Tile.FLOOR);

        assertEquals(2, player.x);
        assertEquals(3, player.y);
        assertEquals(0, player.z);
        assertTrue(messages.isEmpty());
    }

    @Test
    void onEnterReportsVisibleItemAtDestination() {
        Item potion = new Item('!', Color.WHITE, "potion", null);
        world.addAtEmptySpace(potion, 2, 3, 0);
        fov.update(2, 3, 0, 5);
        messages.clear();

        ai.onEnter(2, 3, 0, Tile.FLOOR);

        assertEquals(1, messages.size());
        assertEquals("There's a potion here.", messages.get(0));
    }

    @Test
    void onEnterDoesNotReportItemOutsideFov() {
        Item potion = new Item('!', Color.WHITE, "potion", null);
        world.addAtEmptySpace(potion, 6, 6, 0);
        messages.clear();

        ai.onEnter(6, 6, 0, Tile.FLOOR);

        assertEquals(6, player.x);
        assertEquals(6, player.y);
        assertTrue(messages.isEmpty());
    }

    @Test
    void onEnterDiggableTileDiggsAndDoesNotMovePlayer() {
        tiles[2][2][0] = Tile.WALL;
        world = new World(tiles);
        fov = new FieldOfView(world);
        messages = new ArrayList<String>();
        player = new Creature(world, '@', Color.WHITE, "Player", 100, 10, 5);
        player.x = 1;
        player.y = 1;
        player.z = 0;
        ai = new PlayerAi(player, messages, fov);
        world.add(player);
        messages.clear();
        int foodBefore = player.food();

        ai.onEnter(2, 2, 0, Tile.WALL);

        assertEquals(1, player.x);
        assertEquals(1, player.y);
        assertEquals(Tile.FLOOR, world.tile(2, 2, 0));
        assertEquals(foodBefore - 10, player.food());
        assertTrue(messages.contains("You dig."));
    }

    @Test
    void onEnterNonGroundNonDiggableTileDoesNotMoveOrDig() {
        ai.onEnter(-1, 1, 0, Tile.BOUNDS);

        assertEquals(1, player.x);
        assertEquals(1, player.y);
        assertEquals(0, player.z);
        assertEquals(666, player.food());
    }

    @Test
    void onNotifyAddsExactMessageToSharedList() {
        ai.onNotify("first");
        ai.onNotify("second");

        assertEquals(List.of("first", "second"), messages);
    }

    @Test
    void canSeeUsesFieldOfViewVisibilityAndDepth() {
        assertTrue(ai.canSee(1, 1, 0));
        assertFalse(ai.canSee(6, 6, 0));
        assertFalse(ai.canSee(1, 1, 1));
    }

    @Test
    void onGainLevelDoesNotThrow() {
        ai.onGainLevel();
        ai.onGainLevel();
        assertEquals(1, player.level());
    }

    @Test
    void rememberedTileReturnsFieldOfViewMemory() {
        assertEquals(Tile.FLOOR, ai.rememberedTile(1, 1, 0));
        assertEquals(Tile.UNKNOWN, ai.rememberedTile(1, 1, 1));
    }

    private Creature findPlayer() {
        return world.creature(1, 1, 0);
    }
}
