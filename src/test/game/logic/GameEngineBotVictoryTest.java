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

public class GameEngineBotVictoryTest {

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
    public void testBotWinsByDefeatingPlayerArmy() {

        player.getArmy().clear();
        player.getArmy().add(new Unit(UnitType.PEASANT)); // атака = 2, хп = 10
        bot.getArmy().clear();
        bot.getArmy().add(new Unit(UnitType.KNIGHT)); // атака = 6, хп = 30

        Unit playerUnit = player.getArmy().get(0);
        playerUnit.takeDamage(10);
        BattleSystem.removeDeadUnits(player);

        gameEngine.update();

        assertTrue(player.isDefeated(), "Армия игрока должна быть уничтожена");
        assertFalse(gameEngine.isRunning, "Игра должна завершиться победой бота");
    }

    @Test
    public void testBotWinsByCapturingPlayerCity() {
        City playerCity = new City("Главный Замок", player);
        map.getTile(5, 5).setCity(playerCity);
        map.getTile(5, 5).setType(TileType.PLAIN);
        map.getTile(8, 8).setHero(bot);
        bot.setCity(new City("Крепость Бота", bot));

        bot.getArmy().clear();
        bot.getArmy().add(new Unit(UnitType.KNIGHT)); // атака 6
        playerCity.getGarrison().clear();
        playerCity.getGarrison().add(new Unit(UnitType.PEASANT)); // атака 2


        int newX = 5, newY = 5;
        map.getTile(bot.getX(), bot.getY()).setHero(null);
        bot.move(newX - bot.getX(), newY - bot.getY());
        map.getTile(newX, newY).setHero(bot);

        if (map.getTile(newX, newY).hasCity()) {
            City city = map.getTile(newX, newY).getCity();
            if (city.getOwner() != bot) {
                int garrisonAttack = city.getGarrisonTotalAttack();
                int attackerAttack = 0;
                for (Unit unit : bot.getArmy()) {
                    attackerAttack += unit.getAttack();
                }
                if (attackerAttack > garrisonAttack) {
                    city.setOwner(bot);
                    gameEngine.isRunning = false;
                }
            }
        }


        assertEquals(bot, playerCity.getOwner(), "Бот должен стать владельцем города игрока");
        assertFalse(gameEngine.isRunning, "Игра должна завершиться победой бота");
    }
}