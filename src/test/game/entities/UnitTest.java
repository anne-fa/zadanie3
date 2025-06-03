package test.game.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.game.entities.Unit;
import com.game.entities.UnitType;
import org.junit.jupiter.api.Test;
public class UnitTest {

    @Test
    void testUnitTakeDamage() {
        Unit unit = new Unit(UnitType.PEASANT);
        unit.takeDamage(5);
        assertEquals(5, unit.getCurrentHealth());
        unit.takeDamage(10);
        assertEquals(0, unit.getCurrentHealth());
        assertTrue(unit.isDead());
    }

    @Test
    void testUnitModifiers() {
        Unit unit = new Unit(UnitType.PEASANT);
        unit.setAttackBonus(5);
        unit.setDamageReduction(0.5);

        assertEquals(7, unit.getAttack());
        assertEquals(0.5, unit.getDamageReduction());
        unit.resetModifiers();
        assertEquals(2, unit.getAttack());
        assertEquals(0.0, unit.getDamageReduction());
    }

}