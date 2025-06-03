package test.game.logic;

import com.game.battle.BattleSystem;
import com.game.city.City;
import com.game.entities.Bot;
import com.game.entities.Hero;
import com.game.entities.Unit;
import com.game.entities.UnitType;
import com.game.logic.GameEngine;
import com.game.map.Map;
import com.game.map.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameEnginePlayerVictoryTest {

    private GameEngine gameEngine;
    private Hero player;
    private Bot bot;
    private Map map;

    @BeforeEach
    public void setUp() {
        map = new Map(10, 10);
        player = new Hero("Игрок", 5, 5);
        bot = new Bot("Бот", 8, 8);
        gameEngine = new GameEngine();
        gameEngine.map = map;
        gameEngine.player = player;
        gameEngine.bot = bot;
        gameEngine.isRunning = true;
    }

    @Test
    public void testPlayerWinsByDefeatingBotArmy() {

        player.getArmy().clear();
        player.getArmy().add(new Unit(UnitType.KNIGHT)); // атака = 6, хп = 30
        bot.getArmy().clear();
        bot.getArmy().add(new Unit(UnitType.PEASANT)); // атака = 2, хп = 10


        Unit botUnit = bot.getArmy().get(0);
        botUnit.takeDamage(10);
        BattleSystem.removeDeadUnits(bot);

        gameEngine.update();

        assertTrue(bot.isDefeated(), "Армия бота должна быть уничтожена");
        assertFalse(gameEngine.isRunning, "Игра должна была завершиться победой игрока");
    }

    @Test
    public void testPlayerWinsByCapturingBotCity() {

        City botCity = new City("Крепость Бота", bot);
        map.getTile(8, 8).setCity(botCity);
        map.getTile(8, 8).setType(TileType.PLAIN);
        map.getTile(5, 5).setHero(player);
        bot.setCity(botCity);

        player.getArmy().clear();
        player.getArmy().add(new Unit(UnitType.KNIGHT)); // атака = 6
        botCity.getGarrison().clear();
        botCity.getGarrison().add(new Unit(UnitType.PEASANT)); // атака = 2


        gameEngine.movePlayer(3, 3);

        assertEquals(player, botCity.getOwner(), "Игрок должен стать владельцем города бота");
        assertFalse(gameEngine.isRunning, "Игра должна завершиться победой игрока");
    }
}