package com.game.logic;
import com.game.logic.GameSave;
import com.game.battle.BattleSystem;
import com.game.city.BuildingType;
import com.game.city.City;
import com.game.entities.Bot;
import com.game.entities.Hero;
import com.game.entities.Unit;
import com.game.entities.UnitType;
import com.game.map.Map;
import com.game.map.Tile;
import com.game.map.TileType;

import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.game.city.*;
import com.game.entities.*;
import com.game.map.*;

import java.util.*;
import java.util.concurrent.*;

public class GameEngine {
    public Map map;
    public Hero player;
    public Bot bot;
    public boolean isRunning;
    private final Scanner scanner = new Scanner(System.in);
    public Set<String> visitedForests = new HashSet<>();
    private MapManager mapManager = new MapManager();
    private String username;
    private long lastSaveTime = System.currentTimeMillis();
    private List<Bot> bots;
    private Visitor playerVisitor;
    private List<Visitor> npcs = new ArrayList<>();
    private final ScheduledExecutorService npcScheduler = Executors.newScheduledThreadPool(1);


    public GameEngine() {
        map = new Map(10, 10);
        //player = new Hero("Player", 0, 0);
        //bots = new ArrayList<>();

        List<String> mapData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mapData.add("..........");
        }
        List<String> customTiles = new ArrayList<>();
        //initialize(mapData, customTiles);

        initializeNPCs();
    }

    private void initializeNPCs() {
        //playerVisitor = new Visitor("Игрок", player, null);
        npcs = new ArrayList<>();
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Tile tile = map.getTile(x, y);
                if (tile.hasCity()) {
                    City city = tile.getCity();
                    for (int i = 1; i <= 10; i++) {
                        npcs.add(new Visitor("NPC" + i, null, city));
                    }
                }
            }
        }
    }
    public void start() {
        System.out.println("Введите имя:");
        username = scanner.nextLine();
        System.out.println("Хотите загрузить сохранение? (yes/no)");
        String choice = scanner.nextLine();
        if (choice.equals("yes")) {
            System.out.println("Введите имя файла для загрузки:");
            String filename = scanner.nextLine();


            //////////////////
            if (!filename.startsWith(username + "_")) {
                System.out.println("Такого файла не существует");
            } else {
                loadGame(filename);
                if (isRunning) {
                    while (isRunning) {
                        render();
                        handleInput();
                        update();
                    }
                    return;
                }
            }
        }
//////////////////
        while (true) {
            System.out.println("1. Начать игру");
            System.out.println("2. Редактор карт");
            System.out.println("3. Выход");
            System.out.print("Выбор: ");
            int menuChoice;

            while (true) {
                if (scanner.hasNextInt()) {
                    menuChoice = scanner.nextInt();
                    scanner.nextLine();
                    break;
                } else {
                    scanner.nextLine();
                    System.out.println("Ошибка");

                }
            }

            if (menuChoice == 1) {
                List<String> maps = mapManager.getAvailableMaps();

                if (maps.isEmpty()) {
                    System.out.println("Нет доступных карт. Сначала создайте карту в редакторе");
                    continue;
                }

                System.out.println("Выберите карту:");
                for (int i = 0; i < maps.size(); i++) {
                    System.out.println((i + 1) + ". " + maps.get(i));
                }

                int mapChoice;
                while (true) {
                    System.out.print("Выберите карту: ");
                    if (scanner.hasNextInt()) {
                        mapChoice = scanner.nextInt();
                        scanner.nextLine();
                        break;
                    } else {
                        scanner.nextLine();
                        System.out.println("Ошибка: требуется число");
                    }
                }

                if (mapChoice < 1 || mapChoice > maps.size()) {
                    System.out.println("Неверный выбор карты");
                    continue;
                }

                String selectedMap = maps.get(mapChoice - 1);
                try {
                    MapData mapData = mapManager.loadMap(selectedMap);
                    initialize(mapData.mapLines, mapData.customTiles);
                    System.out.println("Игра началась на карте: " + selectedMap);
                    while (isRunning) {
                        render();
                        handleInput();
                        update();
                    }
                    break;
                } catch (IOException e) {
                    System.out.println("Ошибка при загрузке карты: " + e.getMessage());
                    continue;
                }

            } else if (menuChoice == 2) {
                MapEditor editor = new MapEditor();
                editor.start();
            } else if (menuChoice == 3) {
                System.out.println("Выход из игры");
                break;
            } else {
                System.out.println("Неверный выбор");
            }
        }
        scanner.close();
    }


    private void initialize(List<String> mapData, List<String> customTiles) {
        int rows = mapData.size();
        int cols = mapData.get(0).length();
        map = new Map(rows, cols);
        boolean playerFound = false;
        boolean botFound = false;
       // playerVisitor = new Visitor("Игрок", player, null);

        for (int y = 0; y < rows; y++) {
            String row = mapData.get(y);
            for (int x = 0; x < cols; x++) {
                char symbol = row.charAt(x);
                if (symbol == 'H') {
                    //player = new Hero("Игрок", x, y);
                    playerFound = true;
                } else if (symbol == 'E') {
                    bot = new Bot("Бот", x, y);
                    botFound = true;
                }
            }
        }

        playerVisitor = new Visitor("Игрок", player, null);
        // Отладка: проверяем ID объектов
        System.out.println("ID player: " + System.identityHashCode(player));
        System.out.println("ID playerVisitor.getHero(): " + System.identityHashCode(playerVisitor.getHero()));

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Tile tile = map.getTile(x, y);
                if (tile.hasCity()) {
                    City city = tile.getCity();
                    for (int i = 1; i <= 10; i++) {
                        npcs.add(new Visitor("NPC" + i, null, city));
                    }
                }
            }
        }


        npcScheduler.scheduleAtFixedRate(() -> {
            for (Visitor npc : npcs) {
                if (Math.random() < 0.1) {
                    City city = npc.getCity();
                    if (city != null) {
                        List<Building> serviceBuildings = Arrays.asList(city.getHotel(), city.getCafe(), city.getBarbershop());
                        Building selectedBuilding = serviceBuildings.get(new Random().nextInt(serviceBuildings.size()));
                        Service service = selectedBuilding.getServices().get(new Random().nextInt(selectedBuilding.getServices().size()));
                        selectedBuilding.requestService(npc, service);
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);


        for (int y = 0; y < rows; y++) {
            String row = mapData.get(y);
            for (int x = 0; x < cols; x++) {
                char symbol = row.charAt(x);
                if (symbol == 'H') {
                    player = new Hero("Игрок", x, y);
                    playerFound = true;
                } else if (symbol == 'E') {
                    bot = new Bot("Бот", x, y);
                    botFound = true;
                }
            }
        }


        if (!botFound) {
            //System.out.println("Предупреждение: символ 'E' не найден на карте. Размещаем бота в (" + (cols - 1) + "," + (rows - 1) + ").");
            bot = new Bot("Бот", cols - 1, rows - 1);
            map.getTile(cols - 1, rows - 1).setHero(bot);
            map.getTile(cols - 1, rows - 1).setType(TileType.PLAIN);
        }


//        if (!playerFound) {
//            System.out.println("Предупреждение: символ 'H' не найден на карте. Размещаем игрока в (0,0).");
//            //player = new Hero("Игрок", 0, 0);
//            //map.getTile(0, 0).setHero(player);
//            map.getTile(0, 0).setType(TileType.PLAIN);
//        }


        for (int y = 0; y < rows; y++) {
            String row = mapData.get(y);
            for (int x = 0; x < cols; x++) {
                char symbol = row.charAt(x);
                Tile tile = map.getTile(x, y);
                switch (symbol) {
                    case '.':
                        tile.setType(TileType.PLAIN);
                        break;
                    case '#':
                        tile.setType(TileType.FOREST);
                        break;
                    case '^':
                        tile.setType(TileType.MOUNTAIN);
                        break;
                    case '@':
                        tile.setType(TileType.PLAIN);
                        tile.setCave(true);
                        break;
                    case 'A':
                        tile.setType(TileType.PLAIN);
                        String cityNameA = "Замок игрока";
                        City cityA = new City(cityNameA, player);
                        tile.setCity(cityA);
                        player.setCity(cityA);
                        break;
                    case 'C':
                        tile.setType(TileType.PLAIN);
                        String cityNameC = (player != null && x == player.getX() && y == player.getY()) ? "Главный Замок" :
                                (bot != null && x == bot.getX() && y == bot.getY()) ? "Крепость Бота" : "Город";
                        Hero cityOwnerC = (player != null && x == player.getX() && y == player.getY()) ? player :
                                (bot != null && x == bot.getX() && y == bot.getY()) ? bot : null;
                        City cityC = new City(cityNameC, cityOwnerC);
                        tile.setCity(cityC);
                        if (cityOwnerC == player) {
                            player.setCity(cityC);
                        } else if (cityOwnerC == bot) {
                            bot.setCity(cityC);
                        }
                        break;
                    case 'H':
                        tile.setType(TileType.PLAIN);
                        tile.setHero(player);
                        break;
                    case 'E':
                        tile.setType(TileType.PLAIN);
                        tile.setHero(bot);
                        break;
                    case '!':
                        tile.setType(TileType.PLAIN);
                        tile.setCustom(true);
                        break;
                    default:
                        tile.setType(TileType.PLAIN);
                        break;
                }
            }
        }


        if (customTiles != null) {
            for (String customTile : customTiles) {
                String[] parts = customTile.split(":");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int bonusGold = Integer.parseInt(parts[2]);
                boolean customPassable = Boolean.parseBoolean(parts[3]);
                Tile tile = map.getTile(x, y);
                tile.setCustom(true);
                tile.setBonusGold(bonusGold);
                tile.setCustomPassable(customPassable);
            }
        }

        isRunning = true;
    }


    private void render() {
        System.out.println("\n=== КАРТА ===");
        System.out.println("Золото: " + player.getGold() + " | Армия: " + player.getArmy().size());

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Tile tile = map.getTile(x, y);
                String symbol = ".";

                if (tile.getHero() != null) {
                    symbol = (tile.getHero() == player) ? "H" : "E";
                } else if (tile.hasCity()) {
                    City city = tile.getCity();
                    symbol = (city.getOwner() == player) ? "A" : "C";
                } else if (tile.isCave()) {
                    symbol = "@";
                } else {
                    switch (tile.getType()) {
                        case FOREST: symbol = "#"; break;
                        case MOUNTAIN: symbol = "^"; break;
                    }
                }
                System.out.print(symbol + " ");
            }
            System.out.println();
        }

        System.out.println("\nПодсказки: H - Вы, E - Враг, C - Город бота, # - Лес, ^ - Горы, @ - Пещера");
    }

    private void recruitToGarrisonMenu(City city) {
        List<UnitType> availableUnits = city.getAvailableUnits();
        if (availableUnits.isEmpty()) {
            System.out.println("Нет доступных юнитов");
            return;
        }

        System.out.println("\nДоступные юниты для гарнизона:");
        for (int i = 0; i < availableUnits.size(); i++) {
            UnitType ut = availableUnits.get(i);
            System.out.println((i + 1) + ". " + ut.name() + " (" + ut.getCost() + " золота)");
        }


        System.out.print("Выберите юнита (0 - отмена): ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (choice < 0 || choice >= availableUnits.size()) return;

        UnitType selected = availableUnits.get(choice);
        if (player.canAfford(selected.getCost())) {
            city.recruitToGarrison(selected, player);
        } else {
            System.out.println("Недостаточно золота(");
        }
    }

    private void visitBuilding(Building building) {
        System.out.println("Доступные услуги:");
        for (int i = 0; i < building.getServices().size(); i++) {
            Service service = building.getServices().get(i);
            System.out.println((i + 1) + ". " + service.getName() + " - " + service.getCost() + " золота");
        }
        System.out.print("Выберите услугу (0 - отмена): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 0) return;

        Service selectedService = building.getServices().get(choice - 1);
        if (building.requestService(playerVisitor, selectedService)) {
            System.out.println("Услуга начата");
        } else {
            building.printStatistics();
            System.out.print("Здание занято. Хотите подождать? (да/нет): ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("да")) {
                building.requestService(playerVisitor, selectedService);
                System.out.println("Вы встали в очередь");
            } else {
                System.out.println("Вы решили не ждать");
            }
        }
    }

    private void fightForCastle(Hero attacker, City city) {
        int attackerAttack = 0;
        for (Unit unit : attacker.getArmy()) {
            attackerAttack += unit.getAttack();
        }
        if (attacker.hasCaptureBonus()) {
            attackerAttack *= 2;
            attacker.setHasCaptureBonus(false);
        }

        int defenderAttack = city.getGarrisonTotalAttack();

        System.out.println("Сражение за город " + city.getName());
        System.out.println("Атака армии: " + attackerAttack + " против гарнизона: " + defenderAttack);

        if (attackerAttack > defenderAttack) {
            System.out.println("Город захвачен");
            city.setOwner(attacker);
            city.getGarrison().clear();
        } else {
            System.out.println("Гарнизон отбился");
            attacker.getArmy().removeIf(Unit::isDead);
        }
    }

    public void update() {
        if (player.isDefeated()) {
            System.out.println("Вы проиграли(");
            isRunning = false;
        } else if (bot.isDefeated()) {
            System.out.println("Вы победили !!!");
            isRunning = false;
        }
        bot.manageCity(map);
        bot.displayArmy();
        bot.makeMove(player, map, scanner);


        if (System.currentTimeMillis() - lastSaveTime >= 90_000) {
            saveGame(username);
            lastSaveTime = System.currentTimeMillis();
        }
    }

    private void handleCityInteraction(City city) {
        while (true) {
            System.out.println("\nВы в городе " + city.getName());
            System.out.println("1. Построить здание");
            System.out.println("2. Нанять юнитов для армии");
            System.out.println("3. Нанять юнитов для гарнизона");
            System.out.println("4. Посетить отель");
            System.out.println("5. Посетить кафе");
            System.out.println("6. Посетить парикмахерскую");
            System.out.println("7. Выйти");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> buildBuildingMenu(city);
                case 2 -> recruitUnitsMenu(city);
                case 3 -> recruitToGarrisonMenu(city);
                case 4 -> visitBuilding(city.getHotel());
                case 5 -> visitBuilding(city.getCafe());
                case 6 -> visitBuilding(city.getBarbershop());
                case 7 -> { return; }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private void buildBuildingMenu(City city) {
        System.out.println("\nДоступные здания:");
        BuildingType[] allBuildings = BuildingType.values();

        for (int i = 0; i < allBuildings.length; i++) {
            BuildingType b = allBuildings[i];
            String status = city.getBuildings().contains(b) ? "[Построено]" : "";
            System.out.println((i + 1) + ". " + b.getName() + " - " + b.getCost() + " золота " + status);
        }

        System.out.print("Выберите здание (0 - отмена): ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (choice < 0 || choice >= allBuildings.length) return;

        BuildingType selected = allBuildings[choice];
        if (city.build(selected, player)) {
            System.out.println(selected.getName() + " построено");
        } else {
            System.out.println("Недостаточно золота (");
        }
    }

    private void recruitUnitsMenu(City city) {
        List<UnitType> availableUnits = city.getAvailableUnits();
        if (availableUnits.isEmpty()) {
            System.out.println("Нет доступных юнитов");
            return;
        }

        System.out.println("\nДоступные юниты:");
        for (int i = 0; i < availableUnits.size(); i++) {
            UnitType ut = availableUnits.get(i);
            System.out.println((i + 1) + ". " + ut.name() + " (" + ut.getCost() + " золота)");
        }

        System.out.print("Выберите юнита (0 - отмена): ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (choice < 0 || choice >= availableUnits.size()) return;

        UnitType selected = availableUnits.get(choice);
        if (player.canAfford(selected.getCost())) {
            player.recruitUnit(selected, city);
            System.out.println(selected.name() + " нанят");
        } else {
            System.out.println("Недостаточно золота");
        }
    }

    private void handleInput() {
        System.out.print("\nДействие [W/A/S/D/save]: ");
        String input = scanner.nextLine().trim().toLowerCase();
        switch (input) {
            case "w" -> movePlayer(0, -1);
            case "s" -> movePlayer(0, 1);
            case "a" -> movePlayer(-1, 0);
            case "d" -> movePlayer(1, 0);
            case "save" -> saveGame(username);
            case "q" -> isRunning = false;
            default -> System.out.println("Неизвестная команда");
        }
    }

    public void movePlayer(int dx, int dy) {
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        if (!map.isValid(newX, newY)) {
            System.out.println("Выход за границы карты");
            return;
        }

        Tile targetTile = map.getTile(newX, newY);

        if (targetTile.getHero() == bot) {
            BattleSystem.fight(player, bot, scanner, true);
            if (bot.isDefeated()) {
                targetTile.setHero(null);
            }
            return;
        }

        if (isTilePassable(targetTile)) {
            map.getTile(player.getX(), player.getY()).setHero(null);
            player.move(dx, dy);
            targetTile.setHero(player);


            if (targetTile.isCustom()) {
                int bonus = targetTile.getBonusGold();
                player.addGold(bonus);
                System.out.println("Вы получили " + bonus + " золота на кастомной клетке");
            }

            if (targetTile.getType() == TileType.FOREST) {
                String key = newX + ":" + newY;
                if (!visitedForests.contains(key)) {
                    player.addGold(20);
                    visitedForests.add(key);
                    System.out.println("Вы нашли 20 золота в лесу");
                }
            }

            if (targetTile.isCave()) {
                hireDwarfFromCave();
            }

            if (targetTile.hasCity()) {
                City city = targetTile.getCity();
                if (city.getOwner() == player) {
                    handleCityInteraction(city);
                } else {
                    fightForCastle(player, city);
                }
            }
        } else {
            System.out.println("Нельзя пройти через " + targetTile.getType());
        }
    }



    private boolean isTilePassable(Tile tile) {
        if (tile.getType() == TileType.MOUNTAIN) {
            for (Unit unit : player.getArmy()) {
                if (unit.getType() == UnitType.DWARF) {
                    return true;
                }
            }
            return false;
        }
        return tile.isPassable();
    }


    private void hireDwarfFromCave() {
        System.out.println("\nВы нашли пещеру. Хотите нанять Гнома за 30 золота?");
        System.out.println("1. Да");
        System.out.println("2. Нет");
        System.out.print("Выбор: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            if (player.canAfford(UnitType.DWARF.getCost())) {
                if (player.getArmy().size() < 10) {
                    player.recruitUnit(UnitType.DWARF, null); // null, так как не из города
                    System.out.println("Гном нанят");
                } else {
                    System.out.println("Армия переполнена");
                }
            } else {
                System.out.println("Недостаточно золота");
            }
        }
    }
    ///////////////////////////
    public void saveGame(String username) {
        GameSave save = new GameSave(map, player, bot, isRunning, visitedForests);
        Gson gson = new Gson();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = username + "_" + timestamp + ".json";

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(save, writer);
            System.out.println("Игра сохранена в " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    public void loadGame(String filename) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filename)) {
            GameSave save = gson.fromJson(reader, GameSave.class);
            this.map = save.getMap();
            this.player = save.getPlayer();
            this.bot = save.getBot();
            this.isRunning = save.isRunning();
            this.visitedForests = save.getVisitedForests();
            playerVisitor = new Visitor("Игрок", player, null);
            map.getTile(player.getX(), player.getY()).setHero(player);
            map.getTile(bot.getX(), bot.getY()).setHero(bot);

            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    Tile tile = map.getTile(x, y);
                    if (tile.hasCity()) {
                        City city = tile.getCity();
                        if (city.getOwner() == null) {
                            if (x == player.getX() && y == player.getY()) {
                                city.setOwner(player);
                            } else if (x == bot.getX() && y == bot.getY()) {
                                city.setOwner(bot);
                                bot.setCity(city);
                            }
                        }
                    }
                }
            }
            System.out.println("Игра загружена из " + filename);

        } catch (IOException e) {
            System.out.println("Ошибка при загрузке: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        new GameEngine().start();
    }
}