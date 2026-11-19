package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreatureAiTest {
    private Tile[][][] tiles;
    private World world;
    private Creature player;
    private Creature zombie;

    @BeforeEach
    void setUp() {
        tiles = new Tile[7][7][1];
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                tiles[x][y][0] = Tile.FLOOR;
            }
        }
        world = new World(tiles);
        player = new Creature(world, '@', Color.WHITE, "Player", 100, 10, 5);
        zombie = new Creature(world, 'Z', Color.GREEN, "Zombie", 30, 5, 2);
        place(player, 1, 1);
        place(zombie, 5, 5);
        world.add(player);
        world.add(zombie);
        player.setCreatureAi(new CreatureAi(player));
            zombie.setCreatureAi(new CreatureAi(zombie));
    }

    @Test
    void seesTargetWithinRadiusWithoutWalls() {
        new CreatureAi(zombie);
        assertTrue(zombie.canSee(player.x, player.y, player.z));
    }

    @Test
    void cannotSeeBeyondVisionRadius() {
        new CreatureAi(zombie);
        place(zombie, 6, 6);
        zombie.modifyVisionRadius(-9);
        assertFalse(zombie.canSee(player.x, player.y, player.z));
    }

    @Test
    void wallBlocksLineOfSight() {
        CreatureAi ai = new CreatureAi(zombie);
        tiles[3][3][0] = Tile.WALL;

        assertFalse(ai.canSee(player.x, player.y, player.z));
    }

    @Test
    void targetOnAnotherDepthIsNotVisible() {
        CreatureAi ai = new CreatureAi(zombie);
        assertFalse(ai.canSee(player.x, player.y, 1));
    }

    @Test
    void wanderingUsesInjectedMovementAndStaysInBounds() {
        SequenceRandom random = new SequenceRandom(new int[]{2, 1}, new double[]{0.9});
        CreatureAi ai = new CreatureAi(zombie, random);

        ai.wander();

        assertEquals(6, zombie.x);
        assertEquals(5, zombie.y);
        assertEquals(0, zombie.z);
    }

    @Test
    void wanderingIntoWallLeavesCreatureAtOriginalPosition() {
        tiles[6][5][0] = Tile.WALL;
        SequenceRandom random = new SequenceRandom(new int[]{2, 1}, new double[]{0.9});
        CreatureAi ai = new CreatureAi(zombie, random);

        ai.wander();

        assertEquals(5, zombie.x);
        assertEquals(5, zombie.y);
    }

    @Test
    void zombieSkipBranchAndVisiblePursuitAreControlledByRandom() {
        SequenceRandom skipping = new SequenceRandom(new int[]{}, new double[]{0.1});
        ZombieAi zombieAi = new ZombieAi(zombie, player, skipping);
        place(zombie, 5, 5);
        zombieAi.onUpdate();
        assertEquals(5, zombie.x);
        assertEquals(5, zombie.y);

        SequenceRandom pursuing = new SequenceRandom(new int[]{}, new double[]{0.9});
        place(zombie, 3, 1);
        ZombieAi pursuitAi = new ZombieAi(zombie, player, pursuing);
        pursuitAi.onUpdate();
        assertTrue(Math.abs(zombie.x - player.x) + Math.abs(zombie.y - player.y) < 3);
    }

    @Test
    void batUpdateCallsWanderTwiceWithInjectedRandom() {
        SequenceRandom random = new SequenceRandom(
            new int[]{2, 2, 2, 2},
            new double[]{});
        BatAi ai = new BatAi(zombie, random);

        ai.onUpdate();

        assertEquals(6, zombie.x);
        assertEquals(6, zombie.y);
    }

    @Test
    void goblinEquipsBetterWeaponBeforeOtherActions() {
        GoblinAi ai = new GoblinAi(zombie, player);
        Item strongSword = new Item('/', Color.WHITE, "strong sword", null);
        strongSword.modifyAttackValue(20);
        zombie.inventory().add(strongSword);

        ai.onUpdate();

        assertEquals(strongSword, zombie.weapon());
    }

    private void place(Creature creature, int x, int y) {
        creature.x = x;
        creature.y = y;
        creature.z = 0;
    }

    private static class SequenceRandom extends Random {
        private final int[] ints;
        private final double[] doubles;
        private int intIndex;
        private int doubleIndex;

        SequenceRandom(int[] ints, double[] doubles) {
            this.ints = ints;
            this.doubles = doubles;
        }

        @Override
        public int nextInt(int bound) {
            return ints[intIndex++] % bound;
        }

        @Override
        public double nextDouble() {
            return doubles[doubleIndex++];
        }
    }
}
