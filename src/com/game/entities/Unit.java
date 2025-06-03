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

public class Unit {
    private final UnitType type;
    private volatile int currentHealth;      // Изменено на volatile
    private volatile int attackBonus = 0;    // Изменено на volatile
    private volatile double damageReduction = 0;


    public Unit(UnitType type) {
        this.type = type;
        this.currentHealth = type.getHealth();
    }


    public void takeDamage(int damage) {
        int reducedDamage = (int) (damage * (1 - damageReduction));
        currentHealth = Math.max(0, currentHealth - reducedDamage);
    }
    public void setCurrentHealth(int health) {
        this.currentHealth = Math.min(health, type.getHealth());
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public void setAttackBonus(int bonus) {
        this.attackBonus = bonus;
    }

    public void setDamageReduction(double reduction) {
        this.damageReduction = reduction;
    }

    public double getDamageReduction() {
        return damageReduction;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public void resetModifiers() {
        attackBonus = 0;
        damageReduction = 0;
    }



    public UnitType getType(){
        return type;
    }
    public int getCurrentHealth() {
        return currentHealth;
    }
    public int getAttack() {
        return type.getAttack() + attackBonus;
    }
    public int getDefense(){
        return type.getDefense();
    }
    public int getSpeed() {
        return type.getSpeed();
    }
}