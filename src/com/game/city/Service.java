package com.game.city;

import com.game.entities.Hero;
import java.util.function.Consumer;

public class Service {
    private final String name;
    private final int duration;
    private final int cost;
    private final Consumer<Hero> effect;

    public Service(String name, int duration, int cost, Consumer<Hero> effect) {
        this.name = name;
        this.duration = duration;
        this.cost = cost;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getCost() {
        return cost;
    }

    public Consumer<Hero> getEffect() {
        return effect;
    }
}