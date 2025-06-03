package com.game.city;

import com.game.entities.Hero;

public class Visitor {
    private final String name;
    private final Hero hero;
    private final City city;

    public Visitor(String name, Hero hero, City city) {
        this.name = name;
        this.hero = hero;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public Hero getHero() {
        return hero;
    }

    public City getCity() {
        return city;
    }
}