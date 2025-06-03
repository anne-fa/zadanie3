package test.game.battle;

import com.game.battle.BattleSystem;
import com.game.city.City;
import com.game.entities.Bot;
import com.game.entities.Hero;
import com.game.entities.Unit;
import com.game.entities.UnitType;
import com.game.map.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BattleSystemTest {

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
    void testBattleSystemCalculateDamage() {
        Unit attacker = new Unit(UnitType.SWORDMAN);
        Unit defender = new Unit(UnitType.PEASANT);
        int damage = BattleSystem.calculateDamage(attacker, defender);
        assertEquals(3, damage); // 4-1= 3
    }

    @Test
    void testCalculateDamageMin() {
        Unit attacker = new Unit(UnitType.PEASANT); // attack = 5
        Unit defender = new Unit(UnitType.KNIGHT); // defense = 10
        int damage = BattleSystem.calculateDamage(attacker, defender);
        assertEquals(1, damage, "Минимальный урон должен быть 1");
    }

    @Test
    void testGetInitiativeOrder() {
        Hero player = new Hero("Player", 0, 0);
        Hero bot = new Hero("Bot", 1, 1);         //com.game.entities.Bot
        player.getArmy().add(new Unit(UnitType.KNIGHT)); // sp=10
        bot.getArmy().add(new Unit(UnitType.PEASANT)); // sp=5
        List<Unit> order = BattleSystem.getInitiativeOrder(player, bot);
        assertEquals(UnitType.KNIGHT, order.get(0).getType(), "Юнит с большей скоростью ходит первым");
        assertEquals(UnitType.PEASANT, order.get(1).getType(), "Юнит с меньшей скоростью ходит вторым");
    }


    @Test
    void testBattleSystemRemoveDeadUnits() {
        player.getArmy().clear();
        Unit unit = new Unit(UnitType.PEASANT);
        player.getArmy().add(unit);
        unit.takeDamage(10);
        BattleSystem.removeDeadUnits(player);
        assertTrue(player.getArmy().isEmpty());
    }


    @Test
    void testGetOwner() {
        Hero player = new Hero("Player", 0, 0);
       Hero bot = new Hero("Bot", 1, 1);
        Unit unit = new Unit(UnitType.PEASANT);
        player.getArmy().add(unit);
        assertEquals(player, BattleSystem.getOwner(unit, player, bot), "Владелец должен быть player");
    }

}








