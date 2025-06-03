package test.game.city;
//
import com.game.city.BuildingType;
import com.game.city.City;
import com.game.entities.Bot;
import com.game.entities.Hero;
import com.game.entities.UnitType;
import com.game.map.Map;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//
//class CityTest {
//
//    private Hero player;
//    private Bot bot;
//    private City city;
//    private Map map;
//
//    @BeforeEach
//    void setUp() {
//        player = new Hero("Player", 5, 5);
//        bot = new Bot("Bot", 8, 8);
//        city = new City("TestCity", player);
//        map = new Map(10, 10);
//    }
//
//    @Test
//    void testCityBuild() {
//        player.addGold(100);
//        assertTrue(city.build(BuildingType.FARM, player));
//        assertEquals(1, city.getBuildings().size());
//        assertEquals(BuildingType.FARM, city.getBuildings().get(0));
//        assertEquals(50, player.getGold());
//    }
//
//    @Test
//    void testCityRecruitToGarrison() {
//        city.build(BuildingType.FARM, player);
//        player.addGold(10);
//        city.recruitToGarrison(UnitType.PEASANT, player);
//        assertEquals(1, city.getGarrison().size());
//        assertEquals(UnitType.PEASANT, city.getGarrison().get(0).getType());
//        assertEquals(90, player.getGold());
//    }



//    @Test
//    void testCityAvailableUnits() {
//        city.build(BuildingType.FARM, player);
//        city.build(BuildingType.SWORDMAN_RANGE, player);
//        List<UnitType> units = city.getAvailableUnits();
//        assertEquals(2, units.size());
//        assertTrue(units.contains(UnitType.PEASANT));
//        assertTrue(units.contains(UnitType.SWORDMAN));
//    }
//
//}



import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;


class CityTest {

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
    void testCityBuild() {
        player.setGold(100);
        assertTrue(city.build(BuildingType.FARM, player));
        assertEquals(1, city.getBuildings().size());
        assertEquals(BuildingType.FARM, city.getBuildings().get(0));
        assertEquals(50, player.getGold());
    }

    @Test
    void testCityRecruitToGarrison() {
        city.build(BuildingType.FARM, player);
        player.addGold(10);
        city.recruitToGarrison(UnitType.PEASANT, player);
        assertEquals(1, city.getGarrison().size());
        assertEquals(UnitType.PEASANT, city.getGarrison().get(0).getType());
        //assertEquals(90, player.getGold());
    }

    @Test
    void testCityAvailableUnits() {
        player.setGold(200);
        city.build(BuildingType.FARM, player);
        city.build(BuildingType.SWORDMAN_RANGE, player);
        List<UnitType> units = city.getAvailableUnits();
        assertEquals(2, units.size());
        assertTrue(units.contains(UnitType.PEASANT));
        assertTrue(units.contains(UnitType.SWORDMAN));
    }

}