package test.game.entities;

import com.game.city.BuildingType;
import com.game.city.City;
import com.game.entities.Bot;
import com.game.entities.Hero;
import com.game.entities.UnitType;
import com.game.map.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeroTest {

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
    void testHeroInitialization() {
        assertEquals("Player", player.getName());
        assertEquals(5, player.getX());
        assertEquals(5, player.getY());
        assertEquals(100, player.getGold());
        assertEquals(2, player.getArmy().size());
        assertEquals(UnitType.PEASANT, player.getArmy().get(0).getType());
    }

    @Test
    void testHeroMove() {
        player.move(1, -1);
        assertEquals(6, player.getX());
        assertEquals(4, player.getY());
    }

    @Test
    void testHeroRecruitUnit() {
        city.build(BuildingType.SWORDMAN_RANGE, player);
        player.recruitUnit(UnitType.SWORDMAN, city);
        assertEquals(3, player.getArmy().size());
        assertEquals(UnitType.SWORDMAN, player.getArmy().get(2).getType());
        assertEquals(0, player.getGold()); //мечник 20 + дом мечника 80
    }

    @Test
    void testHeroRecruitUnitNoGold() {
        player.spendGold(100);
        city.build(BuildingType.SWORDMAN_RANGE, player);
        player.recruitUnit(UnitType.SWORDMAN, city);
        assertEquals(2, player.getArmy().size());
    }


    @Test
    void testHeroIsDefeated() {
        player.getArmy().clear();
        assertTrue(player.isDefeated());
        player.recruitUnit(UnitType.PEASANT, null);
        assertFalse(player.isDefeated());
    }
}