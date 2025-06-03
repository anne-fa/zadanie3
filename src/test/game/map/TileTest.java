package test.game.map;

import static org.junit.jupiter.api.Assertions.*;

import com.game.city.City;
import com.game.entities.Bot;
import com.game.entities.Hero;
import com.game.map.Map;
import com.game.map.Tile;
import com.game.map.TileType;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
class TileTest {

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
    void testTilePassability() {
        Tile plain = new Tile(TileType.PLAIN);
        Tile mountain = new Tile(TileType.MOUNTAIN);
        assertTrue(plain.isPassable());
        assertFalse(mountain.isPassable());
    }



    @Test
    void testMapInitialization() {

        TileType tileType = map.getTile(0, 0).getType();
        assertTrue(tileType == TileType.PLAIN || tileType == TileType.FOREST || tileType == TileType.MOUNTAIN, "Клетка на (0, 0) должна иметь валидный тип (PLAIN, FOREST, or MOUNTAIN)");


        assertEquals(TileType.PLAIN, map.getTile(3, 3).getType(), "Должна быть PLAIN на (3, 3)");
        assertEquals(TileType.PLAIN, map.getTile(5, 5).getType(), "Должна быть PLAIN на (5, 5)");
        assertEquals(TileType.PLAIN, map.getTile(8, 8).getType(), "Должна быть PLAIN на (8, 8)");

        assertTrue(map.isValid(5, 5), " (5, 5) должно быть валидным");
        assertFalse(map.isValid(10, 10), " (10, 10) не должно быть валидным");
    }

}