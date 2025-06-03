package com.game.battle;

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

import java.util.*;

public class BattleSystem {

    private static transient Random random = new Random();

    public static void fight(Hero player, Hero bot, Scanner scanner , boolean playerIsAttacker ) {
        System.out.println("\n=== НАЧАЛО БОЯ ===");
        printArmyStatus(player, "Ваша армия");
        printArmyStatus(bot, "Армия противника");

        List<Unit> initiativeOrder = getInitiativeOrder(player, bot);

        boolean playerTurnProcessed = false;

        while (!player.getArmy().isEmpty() && !bot.getArmy().isEmpty()) {
            for (Unit unit : new ArrayList<>(initiativeOrder)) {
                if (unit.isDead() || !isUnitInBattle(unit, player, bot)) continue;

                Hero currentOwner = getOwner(unit, player, bot);
                Hero enemy = (currentOwner == player) ? bot : player;


                if (currentOwner == player && !playerTurnProcessed) {
                    activateDwarfAbility(player);
                    playerTurnProcessed = true;
                }

                if (currentOwner == player) {
                    playerTurn(unit, enemy, scanner);
                } else {
                    botTurn(unit, enemy);
                    playerTurnProcessed = false;
                }

                if (isBattleEnded(player, bot)) break;
            }

            resetModifiers(player.getArmy());
        }

        determineWinner(player, bot);

    }
///////////////////////
public static void activateDwarfAbility(Hero player) {
        boolean hasDwarf = false;
        for (Unit unit : player.getArmy()) {
            if (unit.getType() == UnitType.DWARF) {
                hasDwarf = true;
                break;
            }
        }
        if (!hasDwarf) return;

        int effect = random.nextInt(2);
        String effectName = effect == 0 ? "снижение урона" : "увеличение атаки";

        List<Unit> aliveUnits = new ArrayList<>();
        for (Unit unit : player.getArmy()) {
            if (!unit.isDead()) {
                aliveUnits.add(unit);
            }
        }

        int count = Math.min(3, aliveUnits.size());
        Collections.shuffle(aliveUnits);
        List<Unit> selectedUnits = aliveUnits.subList(0, count);

        for (Unit unit : selectedUnits) {
            if (effect == 0) {
                unit.setDamageReduction(0.5);
            } else {
                unit.setAttackBonus((int) (unit.getType().getAttack() * 0.9)); // было 0.2
            }
        }

        System.out.print("Гном применил " + effectName + " к юнитам: ");
        for (int i = 0; i < selectedUnits.size(); i++) {
            System.out.print(selectedUnits.get(i).getType().name());
            if (i < selectedUnits.size() - 1) System.out.print(", ");
        }
        System.out.println();
    }


     static void resetModifiers(List<Unit> army) {
        for (Unit unit : army) {
            unit.resetModifiers();
        }
    }


//    private static boolean isFirstUnitInTurn(List<com.game.entities.Unit> initiativeOrder, com.game.entities.Unit unit) {
//        for (com.game.entities.Unit u : initiativeOrder) {
//            if (!u.isDead()) return u == unit;
//        }
//        return false;
//    }


     public static List<Unit> getInitiativeOrder(Hero player, Hero bot) {
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(player.getArmy());
        allUnits.addAll(bot.getArmy());

        allUnits.sort(new Comparator<Unit>() {
            @Override
            public int compare(Unit u1, Unit u2) {
                int speedCompare = Integer.compare(u2.getSpeed(), u1.getSpeed());
                if (speedCompare != 0) return speedCompare;
                boolean u1IsPlayer = player.getArmy().contains(u1);
                boolean u2IsPlayer = player.getArmy().contains(u2);
                return Boolean.compare(u2IsPlayer, u1IsPlayer);
            }
        });

        return allUnits;
    }


    private static void playerTurn(Unit attacker, Hero enemy, Scanner scanner) {
        System.out.println("\nХод вашего " + attacker.getType().name() + " [HP: " + attacker.getCurrentHealth() + "/" + attacker.getType().getHealth() + "]");
        List<Unit> targets = enemy.getArmy();
        int targetIndex = getValidTargetIndex(targets, scanner);
        Unit defender = targets.get(targetIndex);
        performAttack(attacker, defender);
        removeDeadUnits(enemy);
    }


    private static void botTurn(Unit attacker, Hero enemy) {
        System.out.println("\nХод противника: " + attacker.getType().name() + " [HP: " + attacker.getCurrentHealth() + "/" + attacker.getType().getHealth() + "]");
        List<Unit> targets = enemy.getArmy();
        Unit defender = targets.get(random.nextInt(targets.size()));
        performAttack(attacker, defender);
        removeDeadUnits(enemy);
    }


    private static int getValidTargetIndex(List<Unit> targets, Scanner scanner) {
        while (true) {
            try {
                System.out.println("Выберите цель:");
                for (int i = 0; i < targets.size(); i++) {
                    Unit target = targets.get(i);
                    System.out.println((i + 1) + ". " + target.getType().name() + " [HP: " + target.getCurrentHealth() + "/" + target.getType().getHealth() + "]");
                }
                System.out.print("Ваш выбор: ");
                int choice = scanner.nextInt();
                if (choice < 1 || choice > targets.size()) {
                    System.out.println("Неверный номер. Попробуйте снова.");
                    continue;
                }
                return choice - 1;
            } catch (InputMismatchException e) {
                System.out.println("Введите число");
                scanner.nextLine();
            }
        }
    }


    private static void performAttack(Unit attacker, Unit defender) {
        int damage = calculateDamage(attacker, defender);
        defender.takeDamage(damage);
        System.out.println(attacker.getType().name() + " наносит " + damage + " урона " + defender.getType().name() + "!");
    }


    public static int calculateDamage(Unit attacker, Unit defender) {
        int baseDamage = Math.max(1, attacker.getAttack() - defender.getDefense());

        return baseDamage;
    }


     public static void removeDeadUnits(Hero hero) {
        List<Unit> army = hero.getArmy();
        for (int i = army.size() - 1; i >= 0; i--) {
            if (army.get(i).isDead()) {
                army.remove(i);
            }
        }
    }


    private static boolean isUnitInBattle(Unit unit, Hero player, Hero bot) {
        return player.getArmy().contains(unit) || bot.getArmy().contains(unit);
    }


    private static boolean isBattleEnded(Hero player, Hero bot) {
        return player.getArmy().isEmpty() || bot.getArmy().isEmpty();
    }


    public static Hero getOwner(Unit unit, Hero player, Hero bot) {
        if (player.getArmy().contains(unit)) {
            return player;
        } else {
            return bot;
        }
    }


    private static void printArmyStatus(Hero hero, String title) {
        System.out.println("\n" + title + ":");
        List<Unit> army = hero.getArmy();
        for (int i = 0; i < army.size(); i++) {
            Unit unit = army.get(i);
            System.out.println((i + 1) + ". " + unit.getType().name() + " [HP: " + unit.getCurrentHealth() + "/" + unit.getType().getHealth() + "]");
        }
    }


    private static void determineWinner(Hero player, Hero bot) {
        if (bot.getArmy().isEmpty()) {
            System.out.println("\n=== ВЫ ПОБЕДИЛИ! ===");
            int goldReward = calculateGoldReward(bot);
            player.addGold(goldReward);
           // System.out.println("Получено золота: " + goldReward);
        } else {
            System.out.println("\n=== ВЫ ПРОИГРАЛИ( ===");
        }
    }


    private static int calculateGoldReward(Hero bot) {
        int reward = 0;
        for (Unit unit : bot.getArmy()) {
            reward += unit.getType().getCost() / 2;
        }
        return reward;
    }
}