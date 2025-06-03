package test.game.entities;

import com.game.city.City;
import com.game.entities.Bot;
import com.game.entities.Hero;
import com.game.map.Map;
import com.game.map.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Scanner;

public class BotTest {

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
    void testBotMove() {
        map.getTile(bot.getX(), bot.getY()).setHero(null);
        bot.move(0, -1);
        map.getTile(bot.getX(), bot.getY()).setHero(bot);
        assertEquals(7, bot.getY());
    }


    @Test
    void testMakeMoveBlockedByMountain() {
        Map map = new Map(3, 3);

        map.getTile(1, 0).setType(TileType.MOUNTAIN);
        Hero player = new Hero("Player", 2, 0);

        Bot bot = new Bot("Bot", 0, 0);
        Scanner scanner = new Scanner(System.in);

        bot.makeMove(player, map, scanner);

        assertEquals(0, bot.getX(), "Бот не должен двигаться через гору");
        assertEquals(0, bot.getY(), "Бот не должен двигаться через гору");
    }


}