package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CombatRulesTest {
  
    @ParameterizedTest 
    @CsvSource({
        "99, 100, 1, 100",
        "1, 100, 0, 1",
        "99, 100, 2, 100",
        "1, 100, -1, 0"
    })
    void testCalculateNewHp(int currentHp, int maxHp, int amount, int expectedHp) {
        //arrange
        CombatRules combatRules = new CombatRules();
        //act and assert
        assertEquals(expectedHp, combatRules.calculateNewHp(currentHp, maxHp, amount)); 
    }

    @Test
    void testIsDeadTrue() {
        //Arrange 
        CombatRules combatRules = new CombatRules();
        //act and assert
        assertTrue(combatRules.isDead(0));

    }

    @Test
    void testIsDeadFalse() {
        //Arrange 
        CombatRules combatRules = new CombatRules();
        //act and assert
        assertFalse(combatRules.isDead(1));
        assertFalse(combatRules.isDead(100));

    }
}
