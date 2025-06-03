package com.game.city;

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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class City {
    private final String name;
    private final List<BuildingType> buildings = new ArrayList<>();
    private final List<Unit> garrison = new ArrayList<>();
    private Hero owner;

    private Building hotel;
    private Building cafe;
    private Building barbershop;

    public City(String name, Hero owner) {
        this.name = name;
        this.owner = owner;
        initializeServiceBuildings();
    }

    private void initializeServiceBuildings() {
        hotel = new Building("Отель", Arrays.asList(
                new Service("Короткий отдых", 20, 50, hero -> { // 1 день
                    for (Unit unit : hero.getArmy()) {
                        unit.setCurrentHealth(Math.min(unit.getCurrentHealth() + 2, unit.getType().getHealth()));
                    }
                }),
                new Service("Длинный отдых", 4320, 100, hero -> { // 3 дней
                    for (Unit unit : hero.getArmy()) {
                        unit.setCurrentHealth(Math.min(unit.getCurrentHealth() + 3, unit.getType().getHealth()));
                    }
                })
        ), 5);

        cafe = new Building("Кафе", Arrays.asList(
                new Service("Просто перекус", 15, 20, hero -> {
                    for (Unit unit : hero.getArmy()) {
                        unit.setAttackBonus(unit.getAttackBonus() + 2);
                    }
                }),
                new Service("Плотный обед", 30, 40, hero -> {
                    for (Unit unit : hero.getArmy()) {
                        unit.setAttackBonus(unit.getAttackBonus() + 3);
                    }
                })
        ), 12);

        barbershop = new Building("Парикмахерская", Arrays.asList(
                new Service("Просто стрижка", 10, 10, hero -> {}),
                new Service("Модная стрижка", 30, 30, hero -> hero.setHasCaptureBonus(true))
        ), 1);
    }

    public void recruitToGarrison(UnitType unitType, Hero hero) {
        if (hero.canAfford(unitType.getCost()) && canRecruitUnit(unitType)) {
            Unit newUnit = new Unit(unitType);
            garrison.add(newUnit);
            hero.spendGold(unitType.getCost());
            System.out.println("Нанят " + unitType.name() + " в гарнизон");
        } else {
            System.out.println("Нельзя нанять этого юнита");
        }
    }


    public int getGarrisonTotalAttack() {
        int total = 0;
        for (Unit unit : garrison) {
            total += unit.getAttack();
        }
        return total;
    }

    public Hero getOwner(){
        return owner;
    }
    public void setOwner(Hero owner){
        this.owner = owner;
    }
    public List<Unit> getGarrison() {
        return garrison;
    }

    public boolean build(BuildingType building, Hero player) {
        if (player.getGold() >= building.getCost()) {
            System.out.println("Герой строительства: " + System.identityHashCode(player));
            player.spendGold(building.getCost());
            buildings.add(building);
            return true;
        }
        return false;
    }

    public List<UnitType> getAvailableUnits() {
        List<UnitType> units = new ArrayList<>();
        for (BuildingType b : buildings) {
            if (b.getTrainsUnit() != null) {
                units.add(b.getTrainsUnit());
            }
        }
        return units;
    }

    public boolean canRecruitUnit(UnitType unitType) {
        for (BuildingType b : buildings) {
            if (b.getTrainsUnit() == unitType) {
                return true;
            }
        }
        return false;
    }


    public Building getHotel() {
        return hotel;
    }

    public Building getCafe() {
        return cafe;
    }

    public Building getBarbershop() {
        return barbershop;
    }

    public String getName() {
        return name;
    }
    public List<BuildingType> getBuildings() {
        return buildings;
    }
}