//package com.game.logic;
//
//import java.io.*;
//import java.nio.file.*;
//import java.util.*;
//
//class MapManager {
//    private static final String MAPS_DIR = "maps/";
//
//    public MapManager() {
//        File dir = new File(MAPS_DIR);
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
//    }
//
//
//    public List<String> getAvailableMaps() {
//        File dir = new File(MAPS_DIR);
//
//        FilenameFilter txtFilter = new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                return name.endsWith(".txt");
//            }
//        };
//
//        String[] mapFiles = dir.list(txtFilter);
//
//        if (mapFiles != null) {
//            return Arrays.asList(mapFiles); //массив в список
//        } else {
//            return Collections.emptyList();
//        }
//    }
//
//
//    public void createMap(String mapName, int rows, int cols) throws IOException {
//        String filePath = MAPS_DIR + mapName + ".txt";
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
//            for (int i = 0; i < rows; i++) {
//                String line = new String(new char[cols]).replace('\0', '.');
//                writer.write(line);
//                writer.newLine();
//            }
//        }
//    }
//
//
//    public void editMap(String mapName) throws IOException {
//        String filePath = MAPS_DIR + mapName;
//        List<String> lines = Files.readAllLines(Paths.get(filePath));  //каждый элемент это строка карты
//        for (String line : lines) {
//            System.out.println(line);
//        }
//
//        System.out.println("Введите новую карту (по строкам):");
//        for (int i = 0; i < lines.size(); i++) {
//            String newLine = new Scanner(System.in).nextLine();
//            lines.set(i, newLine);
//        }
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
//            for (String line : lines) {
//                writer.write(line);
//                writer.newLine();
//            }
//        }
//    }
//
//
//    public void deleteMap(String mapName) {
//        String filePath = MAPS_DIR + mapName;
//        File file = new File(filePath);
//        if (file.exists()) {
//            file.delete();
//        }
//    }
//
//
//    public List<String> loadMap(String mapName) throws IOException {
//        String filePath = MAPS_DIR + mapName;
//        return Files.readAllLines(Paths.get(filePath));
//    }
//}

package com.game.logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MapManager {
    private static final String MAPS_DIR = "maps/";
    private Gson gson = new Gson();
    private Scanner scanner = new Scanner(System.in);

    public MapManager() {

        try {
            Files.createDirectories(Paths.get(MAPS_DIR));
        } catch (IOException e) {
            System.out.println("Ошибка при создании директории для карт: " + e.getMessage());
        }
    }


    public List<String> getAvailableMaps() {
        List<String> maps = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(MAPS_DIR), "*.json")) {
            for (Path entry : stream) {
                maps.add(entry.getFileName().toString().replace(".json", ""));
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении списка карт: " + e.getMessage());
        }
        return maps;
    }


    public void createMapWithCustomTiles(String name, List<String> mapData, List<String> customTilesData) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("rows", mapData.size());
        map.put("cols", mapData.get(0).length());
        map.put("mapData", mapData);
        map.put("customTiles", parseCustomTiles(customTilesData));


        String json = gson.toJson(map);
        try (FileWriter writer = new FileWriter(MAPS_DIR + name + ".json")) {
            writer.write(json);
        }
    }

    private List<Map<String, Object>> parseCustomTiles(List<String> customTilesData) {
        List<Map<String, Object>> customTiles = new ArrayList<>();
        for (String data : customTilesData) {
            String[] parts = data.split(":");
            Map<String, Object> tile = new HashMap<>();
            tile.put("x", Integer.parseInt(parts[0]));
            tile.put("y", Integer.parseInt(parts[1]));
            tile.put("bonusGold", Integer.parseInt(parts[2]));
            tile.put("customPassable", Boolean.parseBoolean(parts[3]));
            customTiles.add(tile);
        }
        return customTiles;
    }


    public MapData loadMap(String name) throws IOException {
        try (FileReader reader = new FileReader(MAPS_DIR + name + ".json")) {
            Map<String, Object> map = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType());
            List<String> mapLines = (List<String>) map.get("mapData");
            List<Map<String, Object>> customTiles = (List<Map<String, Object>>) map.get("customTiles");

            List<String> customTilesData = new ArrayList<>();
            if (customTiles != null) {
                for (Map<String, Object> tile : customTiles) {
                    int x = ((Number) tile.get("x")).intValue();
                    int y = ((Number) tile.get("y")).intValue();
                    int bonusGold = ((Number) tile.get("bonusGold")).intValue();
                    boolean customPassable = (Boolean) tile.get("customPassable");
                    customTilesData.add(x + ":" + y + ":" + bonusGold + ":" + customPassable);
                }
            }
            return new MapData(mapLines, customTilesData);
        }
    }

    public void editMap(String name) throws IOException {
        MapData mapData = loadMap(name);
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
            System.out.print("Введите строку " + (i + 1) + ": ");
            String row = scanner.nextLine();
            if (row.length() != cols) {
                System.out.println("Ошибка: строка должна быть длиной " + cols);
                i--;
                continue;
            }
            newMapData.add(row);


            for (int j = 0; j < row.length(); j++) {
                if (row.charAt(j) == '!') {
                    System.out.println("Обнаружена кастомная клетка в позиции (" + j + ", " + i + ")");
                    System.out.print("Введите бонус/штраф по золоту: ");
                    int bonusGold = scanner.nextInt();
                    System.out.print("Проходима ли клетка? (true/false): ");
                    boolean customPassable = scanner.nextBoolean();
                    scanner.nextLine();
                    newCustomTilesData.add(j + ":" + i + ":" + bonusGold + ":" + customPassable);
                }
            }
        }

        createMapWithCustomTiles(name, newMapData, newCustomTilesData);
        System.out.println("Карта успешно отредактирована!");
    }

    public void deleteMap(String name) {
        try {
            Files.deleteIfExists(Paths.get(MAPS_DIR + name + ".json"));
            System.out.println("Карта " + name + " удалена.");
        } catch (IOException e) {
            System.out.println("Ошибка при удалении карты: " + e.getMessage());
        }
    }
}


class MapData {
    List<String> mapLines;
    List<String> customTiles;

    public MapData(List<String> mapLines, List<String> customTiles) {
        this.mapLines = mapLines;
        this.customTiles = customTiles;
    }
}