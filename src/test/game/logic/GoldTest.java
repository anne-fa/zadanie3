package test.game.logic;

import com.game.entities.Hero;
import com.game.logic.GameEngine;
import com.game.map.Map;
import com.game.map.Tile;
import com.game.map.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

public class GoldTest {

    private Hero player;
    private Map map;
    private Set<String> visitedForests;
    private GameEngine gameEngine;

    @BeforeEach
    public void setUp() {
        map = new Map(10, 10);
        player = new Hero("Игрок", 5, 5);
        visitedForests = new HashSet<>();
        gameEngine = new GameEngine();
        gameEngine.map = map;
        gameEngine.player = player;
        gameEngine.visitedForests = visitedForests;
    }

    @Test
    public void testGoldAddition() {

        map.getTile(5, 6).setType(TileType.FOREST);
        int initialGold = player.getGold();

        Tile targetTile = map.getTile(5, 6);
        int newX = 5, newY = 6;
        if (targetTile.getType() == TileType.FOREST) {
            String key = newX + ":" + newY;
            if (!visitedForests.contains(key)) {
                player.addGold(20);
                visitedForests.add(key);
            }
        }

        assertEquals(initialGold + 20, player.getGold(), "Золото должно увеличиться на 20 при первом посещении леса");
        assertTrue(visitedForests.contains("5:6"), "Лес должен быть добавлен в список посещенных");
    }

    @Test
    public void testNoGoldAddition() {

        map.getTile(5, 6).setType(TileType.FOREST);
        visitedForests.add("5:6");
        int initialGold = player.getGold();

        Tile targetTile = map.getTile(5, 6);
        int newX = 5, newY = 6;
        if (targetTile.getType() == TileType.FOREST) {
            String key = newX + ":" + newY;
            if (!visitedForests.contains(key)) {
                player.addGold(20);
                visitedForests.add(key);
            }
        }
        assertEquals(initialGold, player.getGold(), "Золото не должно увеличиваться при повторном посещении леса");
    }

}