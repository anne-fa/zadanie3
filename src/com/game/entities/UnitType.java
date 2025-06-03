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

public enum UnitType {
    PEASANT(8, 2, 1, 3, 10),
    SWORDMAN(15, 4, 1, 2, 20),
    KNIGHT(30, 6, 4, 5, 50),
    DWARF(20, 7, 4, 2, 30);

    private final int health;
    private final int attack;
    private final int defense;
    private final int speed;
    private final int cost;

    UnitType(int health, int attack, int defense, int speed, int cost) {
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.cost = cost;
    }


    public int getHealth(){
        return health;
    }
    public int getAttack() {
        return attack;
    }
    public int getDefense() {
        return defense;
    }
    public int getSpeed(){
        return speed;
    }
    public int getCost() {
        return cost;
    }
}