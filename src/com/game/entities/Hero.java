//недостаточно денег на здание и переполнение армии

package com.game.entities;

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
import com.game.logic.GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hero {
    private static final Logger logger = Logger.getLogger(Hero.class.getName());
    private final String name;
    private int x, y;
    private final List<Unit> army = new ArrayList<>();
    private volatile int gold;
    private static final int MAX_ARMY_SIZE = 4;  //раньше было 10
    private transient City city;
    private boolean hasCaptureBonus = false;

    public Hero(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.gold = 1000;

        try {
            this.army.add(new Unit(UnitType.PEASANT));
            this.army.add(new Unit(UnitType.PEASANT));
            logger.info("Создан герой " + name + " с начальной армией из двух крестьян");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при создании начальной армии для героя " + name, e);
        }
    }

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        logger.info("Герой " + name + " переместился на координаты (" + x + ", " + y + ")");
        System.out.println("Новые координаты: X=" + x + " Y=" + y);
    }

    public void setHasCaptureBonus(boolean b) {
        hasCaptureBonus = b;
    }

    public boolean hasCaptureBonus() {
        return hasCaptureBonus;
    }


//    public void recruitUnit(UnitType unitType, City city) {
//        if (army.size() >= MAX_ARMY_SIZE) {
//            System.out.println("Армия переполнена");
//            return;
//        }
//
//        if ((city == null || city.canRecruitUnit(unitType))     && gold >= unitType.getCost()) {
//            army.add(new Unit(unitType));
//            gold -= unitType.getCost();
//        }
//    }

//    public void recruitUnit(UnitType unitType, City city) {
//        if (army.size() >= MAX_ARMY_SIZE) {
//            logger.warning("Попытка найма юнита для героя " + name + ", но армия переполнена (размер: " + army.size() + ")");
//            System.out.println("Армия переполнена");
//            return;
//        }
//
//        if ((city == null || city.canRecruitUnit(unitType))) {
//            if (gold >= unitType.getCost()) {
//                army.add(new Unit(unitType));
//                gold -= unitType.getCost();
//                logger.info("Герой " + name + " нанял юнита " + unitType.name());
//            } else {
//                logger.warning("Попытка найма юнита " + unitType.name() + " героем " + name + " не удалась: недостаточно золота (требуется: " + unitType.getCost() + ", доступно: " + gold + ")");
//                System.out.println("Недостаточно золота");
//            }
//        }
//    }

    public void recruitUnit(UnitType unitType, City city) {
        if (army.size() >= MAX_ARMY_SIZE) {
            logger.warning("Попытка найма юнита для героя " + name + ", но армия переполнена (размер: " + army.size() + ")");
            System.out.println("Армия переполнена");
            return;
        }

        if (city == null || city.canRecruitUnit(unitType)) {
            if (gold >= unitType.getCost()) {
                try {
                    army.add(new Unit(unitType));
                    gold -= unitType.getCost();
                    logger.info("Герой " + name + " нанял юнита " + unitType.name());
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Критическая ошибка при создании юнита " + unitType.name() + " для героя " + name, e);
                    System.out.println("Ошибка при найме юнита");
                }
            } else {
                logger.warning("Попытка найма юнита " + unitType.name() + " героем " + name + " не удалась: недостаточно золота (требуется: " + unitType.getCost() + ", доступно: " + gold + ")");
                System.out.println("Недостаточно золота");
            }
        } else {
            logger.warning("Герой " + name + " не может нанять юнита " + unitType.name() + ": город не поддерживает этот тип юнита");
            System.out.println("Город не может нанять этот тип юнита");
        }
    }









    public boolean buildBuilding(BuildingType buildingType, City city) {
        if (city.getBuildings().contains(buildingType)) {
            System.out.println(buildingType.getName() + " уже построено");
            return false;
        }

        if (gold >= buildingType.getCost()) {
            city.getBuildings().add(buildingType);
            gold -= buildingType.getCost();
            logger.info("Герой " + name + " построил здание " + buildingType.getName() + " в городе " + city.getName());
            return true;
        } else {
            logger.warning("Попытка строительства здания " + buildingType.getName() + " героем " + name + " не удалась: недостаточно золота (требуется: " + buildingType.getCost() + ", доступно: " + gold + ")");
            System.out.println("Недостаточно золота");
            return false;
        }
    }

    public void displayArmy() {
        System.out.println("\n=== АРМИЯ ИГРОКА ===");
        System.out.println("Размер армии: " + army.size());
        for (Unit unit : army) {
            System.out.println("- " + unit.getType().name() + " [HP: " + unit.getCurrentHealth() + "/" + unit.getType().getHealth() + "]");
        }
    }

    public boolean canAfford(int amount) {
        return gold >= amount;
    }

    public boolean isDefeated() {
        return army.isEmpty();
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Unit> getArmy() {
        return army;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void spendGold(int amount) {
        System.out.println("Списание " + amount + " для " + this.getName() +
                ", ID: " + System.identityHashCode(this) +
                ", до: " + gold);
        gold -= amount;
        System.out.println("После: " + gold);
    }

    public void setGold(int rubles) {
        gold = rubles;
    }
}