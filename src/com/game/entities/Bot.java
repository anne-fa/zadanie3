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

import com.game.battle.BattleSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Bot extends Hero {
    private City botCity;
    private transient Random random = new Random();
    private int MAX_ARMY_SIZE = 10;

    public Bot(String name, int x, int y) {
        super(name, x, y);
    }

    public void displayArmy() {
        System.out.println("\n=== АРМИЯ БОТА ===");
        System.out.println("Размер армии: " + getArmy().size());
        for (Unit unit : getArmy()) {
            System.out.println("- " + unit.getType().name() + " [HP: " + unit.getCurrentHealth() + "/" + unit.getType().getHealth() + "]");
        }
    }

    public void setCity(City city) {
        this.botCity = city;
    }


    public void manageCity(Map map) {
        if (botCity == null)
            return;

        Tile currentTile = map.getTile(getX(), getY());


        if (currentTile.getCity() != botCity) {
            //System.out.println("Бот не в своём замке, управление невозможно");
            return;
        }

        addGold(20);


        BuildingType[] availableBuildings = BuildingType.values();
        List<BuildingType> unbuiltBuildings = new ArrayList<>();
        for (BuildingType b : availableBuildings) {
            if (!botCity.getBuildings().contains(b)) {
                unbuiltBuildings.add(b);
            }
        }
        if (!unbuiltBuildings.isEmpty() && getGold() >= 50) {
            BuildingType toBuild = unbuiltBuildings.get(random.nextInt(unbuiltBuildings.size()));
            if (botCity.build(toBuild, this)) {
                System.out.println("Бот построил " + toBuild.getName() + " в своём замке");
            }
        }


        List<UnitType> availableUnits = botCity.getAvailableUnits();
        if (!availableUnits.isEmpty() && getArmy().size() < MAX_ARMY_SIZE && getGold() >= 10) {
            UnitType toRecruit = availableUnits.get(random.nextInt(availableUnits.size()));
            if (canAfford(toRecruit.getCost())) {
                Unit newUnit = new Unit(toRecruit);
                getArmy().add(newUnit);
                spendGold(toRecruit.getCost());
                System.out.println("Бот нанял " + toRecruit.name() + " для своей армии. Размер армии бота: " + getArmy().size());
            }
        }


        if (!availableUnits.isEmpty() && botCity.getGarrison().size() < 5 && getGold() >= 10) {
            UnitType toRecruit = availableUnits.get(random.nextInt(availableUnits.size()));
            if (canAfford(toRecruit.getCost())) {
                botCity.recruitToGarrison(toRecruit, this);
                System.out.println("Бот нанял " + toRecruit.name() + " для гарнизона. Размер гарнизона: " + botCity.getGarrison().size());
            }
        }
    }

    public void makeMove(Hero player, Map map, Scanner scanner) {

        int dx = player.getX() - getX();
        int dy = player.getY() - getY();
        if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1 && !getArmy().isEmpty()) {
            BattleSystem.fight(player, this, scanner, false);
            return;
        }


        int moveX = 0, moveY = 0;
        if (dx != 0) moveX = dx > 0 ? 1 : -1;
        else if (dy != 0) moveY = dy > 0 ? 1 : -1;

        int newX = getX() + moveX;
        int newY = getY() + moveY;

        if (map.isValid(newX, newY) && map.getTile(newX, newY).isPassable()) {
            map.getTile(getX(), getY()).setHero(null);
            move(moveX, moveY);
            map.getTile(newX, newY).setHero(this); // cтавим бота на новую позицию
            System.out.println("Бот движется к X = " + newX + " Y = " + newY);
        } else {
            System.out.println("Бот не может двигаться в эту сторону");
        }
    }
}