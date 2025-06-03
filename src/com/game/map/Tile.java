package com.game.map;

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

import com.game.city.City;

public class Tile {
    private TileType type;
    private Hero hero;
    private City city;
    private boolean isCave;

    private boolean isCustom = false; // Признак кастомной клетки
    private int bonusGold = 0;       // Бонус или штраф по золоту
    private boolean customPassable = true; // Проходимость кастомной клетки

    public Tile(TileType type) {
        this.type = type;
        this.hero = null;
        this.city = null;
        this.isCave = false;
    }


//    public boolean isPassable() {
//        return type != TileType.MOUNTAIN;
//    }

    public boolean isPassable() {
        if (isCustom) {
            return customPassable;
        }
        return type != TileType.MOUNTAIN;
    }

    public TileType getType() {
        return type;
    }
    public void setType(TileType type) {
        this.type = type;
    }
    public Hero getHero() {
        return hero;
    }
    public void setHero(Hero hero) {
        this.hero = hero;
    }
    public boolean hasCity() {
        return city != null;
    }
    public City getCity(){
        return city;
    }
    public void setCity(City city){
        this.city = city;
    }
    public boolean isCave(){
        return isCave;
    }
    public void setCave(boolean isCave) {
        this.isCave = isCave;
    }


    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public int getBonusGold() {
        return bonusGold;
    }

    public void setBonusGold(int bonusGold) {
        this.bonusGold = bonusGold;
    }

    public boolean isCustomPassable() {
        return customPassable;
    }

    public void setCustomPassable(boolean customPassable) {
        this.customPassable = customPassable;
    }
}