package com.game.logic;

import com.game.entities.Bot;
import com.game.entities.Hero;
import com.game.map.Map;

import java.util.Set;

public class GameSave {
    private Map map;
    private Hero player;
    private Bot bot;
    private boolean isRunning;
    private Set<String> visitedForests;

    public GameSave() {

    }

    public GameSave(Map map, Hero player, Bot bot, boolean isRunning, Set<String> visitedForests) {
        this.map = map;
        this.player = player;
        this.bot = bot;
        this.isRunning = isRunning;
        this.visitedForests = visitedForests;
    }


    public Map getMap() { return map; }
    public void setMap(Map map) { this.map = map; }
    public Hero getPlayer() { return player; }
    public void setPlayer(Hero player) { this.player = player; }
    public Bot getBot() { return bot; }
    public void setBot(Bot bot) { this.bot = bot; }
    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean isRunning) { this.isRunning = isRunning; }
    public Set<String> getVisitedForests() { return visitedForests; }
    public void setVisitedForests(Set<String> visitedForests) { this.visitedForests = visitedForests; }
}