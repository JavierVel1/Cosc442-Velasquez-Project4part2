package game;

import org.junit.jupiter.api.BeforeEach;
import java.awt.Color;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource; 

public class CreatureTest {

    private World world;
    private Creature creature;
    @BeforeEach
    void setUp() {
        Tile[][][] tiles = new Tile[3][3][1];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                tiles[x][y][0] = Tile.FLOOR;
            }
        }
        world = new World(tiles);
        creature = new Creature(world, '@', Color.GRAY, "josh", 100, 10, 5);
        creature.x = 1;
        creature.y = 1;
        creature.z = 0;
        new CreatureAi(creature);
    }

    @ParameterizedTest 
    @CsvSource({
        "50, 100","51, 100", "-99, 1"
    })
    void testmodifyHpValidHP(int amount, int results) {
        //Arrange 
        creature.modifyHp(amount, "Death By cookie");
        //act and assert
        assertEquals(results, creature.hp());
    }

    @Test
    void testmodifyHpInvalidHP() {
        creature.modifyHp(-100, "Death By cookie");

        assertEquals(0, creature.hp());
        assertEquals("Death By cookie", creature.causeOfDeath());
        assertNull(world.creature(1, 1, 0));
        assertNotNull(world.item(1, 1, 0));
    }

    @Test
    void constructorSetsCoreDefaults() {
        assertEquals(100, creature.maxHp());
        assertEquals(10, creature.attackValue());
        assertEquals(5, creature.defenseValue());
        assertEquals(9, creature.visionRadius());
        assertEquals(1000, creature.maxFood());
        assertEquals(666, creature.food());
        assertEquals(5, creature.maxMana());
        assertEquals(5, creature.mana());
        assertEquals(1, creature.level());
    }

    @Test
    void modifyManaClampsToAvailableRange() {
        creature.modifyMana(-100);
        assertEquals(0, creature.mana());

        creature.modifyMana(100);
        assertEquals(creature.maxMana(), creature.mana());
    }

    @Test
    void equipmentContributesToCombatValues() {
        Item sword = new Item('/', Color.WHITE, "sword", null);
        sword.modifyAttackValue(7);
        Item armor = new Item('[', Color.WHITE, "armor", null);
        armor.modifyDefenseValue(4);

        creature.equip(sword);
        creature.equip(armor);

        assertEquals(sword, creature.weapon());
        assertEquals(armor, creature.armor());
        assertEquals(17, creature.attackValue());
        assertEquals(9, creature.defenseValue());
    }

    @Test
    void canEnterRequiresGroundAndAnUnoccupiedLocation() {
        Creature blocker = new Creature(world, 'g', Color.GREEN, "goblin", 10, 1, 1);
        blocker.x = 2;
        blocker.y = 1;
        blocker.z = 0;
        new CreatureAi(blocker);
        world.add(blocker);

        assertTrue(creature.canEnter(1, 2, 0));
        assertFalse(creature.canEnter(2, 1, 0));
        assertFalse(creature.canEnter(-1, 1, 0));
    }

    @Test
    void starvationKillsPlayersButNotOtherCreatures() {
        world.add(creature);
        creature.modifyFood(-1000);

        assertTrue(creature.hp() < 1);
        assertNull(world.creature(1, 1, 0));

        Creature monster = new Creature(world, 'g', Color.GREEN, "goblin", 10, 1, 1);
        monster.x = 2;
        monster.y = 2;
        monster.z = 0;
        new CreatureAi(monster);
        world.add(monster);
        monster.modifyFood(-1000);

        assertEquals(10, monster.hp());
        assertEquals(monster, world.creature(2, 2, 0));
    }

    @Test
    void healingCannotExceedMaximumHp() {
        creature.modifyHp(-25, "test damage");
        creature.modifyHp(100, "test healing");

        assertEquals(creature.maxHp(), creature.hp());
    }

    @Test
    void overeatingIncreasesFoodCapacityAndCostsHealth() {
        creature.modifyFood(400);

        assertEquals(1033, creature.maxFood());
        assertEquals(1033, creature.food());
        assertEquals(99, creature.hp());
    }

    @Test
    void updateRegeneratesHealthAndMana() {
        creature.modifyHp(-10, "test damage");
        creature.modifyMana(-2);
        int foodBeforeUpdate = creature.food();

        creature.update();

        assertEquals(91, creature.hp());
        assertEquals(4, creature.mana());
        assertEquals(foodBeforeUpdate - 3, creature.food());
    }

    @Test
    void gainingExperienceAwardsXpAndCrossingThresholdLevelsUp() {
        creature.modifyXp(10);
        assertEquals(10, creature.xp());
        assertEquals(1, creature.level());

        creature.modifyXp(16);

        assertEquals(26, creature.xp());
        assertEquals(2, creature.level());
    }

    @Test
    void defeatingCreatureAwardsExperienceBasedOnItsStats() {
        Creature enemy = new Creature(world, 'g', Color.GREEN, "goblin", 10, 4, 3);

        creature.gainXp(enemy);

        assertEquals(16, creature.xp());
    }

    @Test
    void deathDropsInventoryItemsAlongsideCorpse() {
        Item potion = new Item('!', Color.WHITE, "potion", null);
        creature.inventory().add(potion);
        world.add(creature);

        creature.modifyHp(-100, "test death");

        assertFalse(creature.inventory().contains(potion));
        assertNotNull(findItem(potion));
        assertNotNull(world.item(1, 1, 0));
        assertNull(world.creature(1, 1, 0));
    }

    @Test
    void replacingEquipmentSelectsNewItemAndUnequipRemovesBonus() {
        Item firstSword = new Item('/', Color.WHITE, "first sword", null);
        firstSword.modifyAttackValue(3);
        Item secondSword = new Item('/', Color.WHITE, "second sword", null);
        secondSword.modifyAttackValue(8);

        creature.equip(firstSword);
        creature.equip(secondSword);
        assertEquals(secondSword, creature.weapon());
        assertEquals(18, creature.attackValue());

        creature.unequip(secondSword);
        assertNull(creature.weapon());
        assertEquals(10, creature.attackValue());
        assertTrue(creature.inventory().contains(firstSword));
        assertTrue(creature.inventory().contains(secondSword));
    }

    @Test
    void effectStartsUpdatesEndsAndIsRemovedWhenComplete() {
        TrackingEffect effect = new TrackingEffect(2);
        Item potion = new Item('!', Color.WHITE, "potion", null);
        potion.setQuaffEffect(effect);
        creature.inventory().add(potion);

        creature.quaff(potion);
        assertEquals(1, effect.starts);
        assertEquals(1, creature.effects().size());

        creature.update();
        assertEquals(1, effect.updates);
        assertEquals(1, creature.effects().size());

        creature.update();
        assertEquals(2, effect.updates);
        assertEquals(1, effect.ends);
        assertTrue(creature.effects().isEmpty());
    }

    @Test
    void pickupDropAndConsumeMoveItemsBetweenInventoryAndWorld() {
        Item food = new Item('%', Color.WHITE, "food", null);
        food.modifyFoodValue(20);
        world.addAtEmptySpace(food, creature.x, creature.y, creature.z);
        creature.pickup();
        assertTrue(creature.inventory().contains(food));
        assertNull(world.item(creature.x, creature.y, creature.z));

        int foodBeforeEating = creature.food();
        creature.eat(food);
        assertEquals(foodBeforeEating + 20, creature.food());
        assertFalse(creature.inventory().contains(food));

        Item dropped = new Item('?', Color.WHITE, "dropped", null);
        creature.inventory().add(dropped);
        creature.drop(dropped);
        assertFalse(creature.inventory().contains(dropped));
        assertNotNull(findItem(dropped));
    }

    @Test
    void throwingItemRemovesItFromInventoryAndPlacesItInWorld() {
        Item rock = new Item('*', Color.WHITE, "rock", null);
        creature.inventory().add(rock);

        creature.throwItem(rock, 2, 2, 0);

        assertFalse(creature.inventory().contains(rock));
        assertNotNull(findItem(rock));
    }

    private Item findItem(Item target) {
        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                for (int z = 0; z < world.depth(); z++) {
                    if (world.item(x, y, z) == target)
                        return target;
                }
            }
        }
        return null;
    }

    private static class TrackingEffect extends Effect {
        private int starts;
        private int updates;
        private int ends;

        TrackingEffect(int duration) {
            super(duration);
        }

        @Override
        public void start(Creature creature) {
            starts++;
        }

        @Override
        public void update(Creature creature) {
            updates++;
            super.update(creature);
        }

        @Override
        public void end(Creature creature) {
            ends++;
        }
    }
}
