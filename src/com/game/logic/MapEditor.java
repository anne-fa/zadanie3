package com.game.logic;

import java.io.*;
import java.nio.file.*;
import java.util.*;

class MapEditor {
    private MapManager mapManager;
    private Scanner scanner;

    public MapEditor() {
        mapManager = new MapManager();
        scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("1. Создать новую карту");
            System.out.println("2. Редактировать существующую карту");
            System.out.println("3. Удалить карту");
            System.out.println("4. Выйти");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createMap();
                    break;
                case 2:
                    editMap();
                    break;
                case 3:
                    deleteMap();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private void createMap() {
        System.out.print("Введите имя карты: ");
        String name = scanner.nextLine();
        System.out.print("Введите количество строк: ");
        int rows = scanner.nextInt();
        System.out.print("Введите количество столбцов: ");
        int cols = scanner.nextInt();
        scanner.nextLine();

        List<String> mapData = new ArrayList<>();
        List<String> customTilesData = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            System.out.print("Введите строку " + (i + 1) + " (используйте ! для кастомных клеток): ");
            String row = scanner.nextLine();
            if (row.length() != cols) {
                System.out.println("Ошибка: строка должна быть длиной " + cols);
                i--;
                continue;
            }

            StringBuilder processedRow = new StringBuilder(row);
            for (int j = 0; j < row.length(); j++) {
                if (row.charAt(j) == '!') {
                    System.out.println("Настройка кастомной клетки в позиции (" + j + ", " + i + "):");
                    System.out.print("Введите бонус/штраф по золоту (целое число): ");
                    int bonusGold = scanner.nextInt();
                    System.out.print("Проходима ли клетка? (yes/no): ");
                    String passableInput = scanner.next();
                    boolean customPassable = passableInput.equalsIgnoreCase("yes");
                    scanner.nextLine();

                    customTilesData.add(j + ":" + i + ":" + bonusGold + ":" + customPassable);
                }
            }
            mapData.add(processedRow.toString());
        }

        try {
            mapManager.createMapWithCustomTiles(name, mapData, customTilesData);
            System.out.println("Карта создана");
        } catch (IOException e) {
            System.out.println("Ошибка при создании карты: " + e.getMessage());
        }
    }

    public boolean isCustomTile(int x, int y, List<String> mapData) {
        if (y < mapData.size() && x < mapData.get(y).length()) {
            return mapData.get(y).charAt(x) == '!';
        }
        return false;
    }

    private void editMap() {
        List<String> maps = mapManager.getAvailableMaps();
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт");
            return;
        }
        System.out.println("Доступные карты:");
        for (String map : maps) {
            System.out.println(map);
        }
        System.out.print("Введите имя карты для редактирования: ");
        String name = scanner.nextLine();
        try {
            MapData mapData = mapManager.loadMap(name);
            System.out.println("Текущая карта:");
            for (String line : mapData.mapLines) {
                System.out.println(line);
            }
            System.out.println("Кастомные клетки: " + mapData.customTiles);


            System.out.print("Введите количество строк: ");
            int rows = scanner.nextInt();
            System.out.print("Введите количество столбцов: ");
            int cols = scanner.nextInt();
            scanner.nextLine();

            List<String> newMapData = new ArrayList<>();
            List<String> newCustomTilesData = new ArrayList<>();

            for (int i = 0; i < rows; i++) {
                System.out.print("Введите строку " + (i + 1) + " (используйте ! для кастомных клеток): ");
                String row = scanner.nextLine();
                if (row.length() != cols) {
                    System.out.println("Ошибка: строка должна быть длиной " + cols);
                    i--;
                    continue;
                }
                newMapData.add(row);
                for (int j = 0; j < row.length(); j++) {
                    if (row.charAt(j) == '!') {
                        System.out.println("Настройка кастомной клетки в позиции (" + j + ", " + i + "):");
                        System.out.print("Введите бонус/штраф по золоту: ");
                        int bonusGold = scanner.nextInt();
                        System.out.print("Проходима ли клетка? (yes/no): ");
                        String passableInput = scanner.next();
                        boolean customPassable = passableInput.equalsIgnoreCase("yes");
                        scanner.nextLine();
                        newCustomTilesData.add(j + ":" + i + ":" + bonusGold + ":" + customPassable);
                    }
                }
            }

            mapManager.createMapWithCustomTiles(name, newMapData, newCustomTilesData);
            System.out.println("Карта отредактирована");
        } catch (IOException e) {
            System.out.println("Ошибка при редактировании карты: " + e.getMessage());
        }
    }

    private void deleteMap() {
        List<String> maps = mapManager.getAvailableMaps();
        if (maps.isEmpty()) {
            System.out.println("Нет доступных карт");
            return;
        }
        System.out.println("Доступные карты:");
        for (String map : maps) {
            System.out.println(map);
        }
        System.out.print("Введите имя карты для удаления: ");
        String name = scanner.nextLine();
        mapManager.deleteMap(name);
        System.out.println("Карта удалена");
    }
}