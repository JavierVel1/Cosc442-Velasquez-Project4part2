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
        //Arrange 
        creature.modifyHp(-100, "Death By cookie");
        //act and assert
        assertEquals(0, creature.hp());
        assertEquals("Death By cookie", creature.causeOfDeath());
        assertNull(world.creature(1, 1, 0));
        assertNotNull(world.item(1, 1, 0));
    }
}
