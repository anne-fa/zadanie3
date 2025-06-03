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

public enum BuildingType {
    BARRACKS("Казармы", 100, UnitType.KNIGHT),
    SWORDMAN_RANGE("Оружейная", 80, UnitType.SWORDMAN),
    FARM("Ферма", 50, UnitType.PEASANT);

    private final String name;
    private final int cost;
    private final UnitType trainsUnit;

    BuildingType(String name, int cost, UnitType trainsUnit) {
        this.name = name;
        this.cost = cost;
        this.trainsUnit = trainsUnit;
    }


    public String getName() {
        return name;
    }
    public int getCost(){
        return cost;
    }
    public UnitType getTrainsUnit() {
        return trainsUnit;
    }

}