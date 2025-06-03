package test.game.entities;

import com.game.entities.UnitType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class UnitTypeTest {

    @Test
    public void testDwarfStats() {
        UnitType dwarf = UnitType.DWARF;
        assertEquals(20, dwarf.getHealth());
        assertEquals(7, dwarf.getAttack());
        assertEquals(4, dwarf.getDefense());
        assertEquals(2, dwarf.getSpeed());
        assertEquals(30, dwarf.getCost());
    }

    @Test
    public void testKnightStats() {
        UnitType knight = UnitType.KNIGHT;
        assertEquals(30, knight.getHealth());
        assertEquals(6, knight.getAttack());
        assertEquals(4, knight.getDefense());
        assertEquals(5, knight.getSpeed());
        assertEquals(50, knight.getCost());
    }
}