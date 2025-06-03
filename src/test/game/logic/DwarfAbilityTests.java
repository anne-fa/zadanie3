package test.game.logic;

import static org.junit.jupiter.api.Assertions.*;

import com.game.battle.BattleSystem;
import com.game.city.City;
import com.game.entities.Bot;
import com.game.entities.Hero;
import com.game.entities.Unit;
import com.game.entities.UnitType;
import com.game.map.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class DwarfAbilityTests {

    private Hero player;
    private Bot bot;
    private City city;
    private Map map;

    @BeforeEach
    void setUp() {
        player = new Hero("Player", 5, 5);
        bot = new Bot("Bot", 8, 8);
        city = new City("TestCity", player);
        map = new Map(10, 10);
    }

    @Test
    void testDwarfAbilityActivation() {

        player.getArmy().clear();
        player.getArmy().add(new Unit(UnitType.DWARF));
        player.getArmy().add(new Unit(UnitType.PEASANT));
        player.getArmy().add(new Unit(UnitType.SWORDMAN));

        BattleSystem.activateDwarfAbility(player);

        int modifiedUnits = 0;
        for (Unit unit : player.getArmy()) {
            if (unit.getDamageReduction() == 0.5 || unit.getAttackBonus() > 0) {
                modifiedUnits++;
            }
        }
        assertTrue(modifiedUnits > 0 && modifiedUnits <= 3);
    }

    @Test
    void testDwarfAbilityNoDwarf() {
        player.getArmy().clear();
        player.getArmy().add(new Unit(UnitType.PEASANT));
        BattleSystem.activateDwarfAbility(player);
        for (Unit unit : player.getArmy()) {
            assertEquals(0.0, unit.getDamageReduction());
            assertEquals(0, unit.getAttackBonus());
        }
    }

    @Test
    void testDwarfAbilityDamageReduction() {
        player.getArmy().clear();
        Unit dwarf = new Unit(UnitType.DWARF);
        Unit peasant = new Unit(UnitType.PEASANT);
        player.getArmy().add(dwarf);
        player.getArmy().add(peasant);

        peasant.setDamageReduction(0.5);
        peasant.takeDamage(10);
        assertEquals(5, peasant.getCurrentHealth());
    }




}
