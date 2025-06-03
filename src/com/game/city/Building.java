package com.game.city;

import com.game.entities.Hero;
import com.game.entities.Unit;

import java.util.*;
import java.util.concurrent.*;

public class Building {
    private final String type;
    private final List<Service> services;
    private final int maxVisitors;
    private final ConcurrentLinkedQueue<Visitor> currentVisitors = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ServiceRequest> waitingQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Visitor, Service> visitorServices = new ConcurrentHashMap<>();
    private final Map<Visitor, Long> completionTimes = new ConcurrentHashMap<>();

    public Building(String type, List<Service> services, int maxVisitors) {
        this.type = type;
        this.services = services;
        this.maxVisitors = maxVisitors;
    }

    public boolean requestService(Visitor visitor, Service service) {
        if (visitor.getHero() != null && !visitor.getHero().canAfford(service.getCost())) {
            System.out.println("Недостаточно золота");
            return false;
        }

        if (currentVisitors.size() < maxVisitors) {
            if (visitor.getHero() != null) {
                System.out.println("Герой услуги: " + System.identityHashCode(visitor.getHero()));
                visitor.getHero().spendGold(service.getCost());
            }
            if (visitor.getHero() != null) {
                visitor.getHero().spendGold(service.getCost());
            }
            startService(visitor, service);
            return true;
        } else {
            waitingQueue.add(new ServiceRequest(visitor, service));
            return false;
        }
    }

    private void startService(Visitor visitor, Service service) {
        currentVisitors.add(visitor);
        visitorServices.put(visitor, service);
        long completionTime = System.currentTimeMillis() + (long) (service.getDuration() * 100); // 1 мин = 100 мс
        completionTimes.put(visitor, completionTime);

        scheduler.schedule(() -> completeService(visitor), (long) (service.getDuration() * 100), TimeUnit.MILLISECONDS);
    }

    private void completeService(Visitor visitor) {
        Service service = visitorServices.remove(visitor);
        completionTimes.remove(visitor);
        currentVisitors.remove(visitor);

        if (visitor.getHero() != null) {
            service.getEffect().accept(visitor.getHero());
            System.out.println("Услуга для " + visitor.getName() + " завершена");
        }
        service.getEffect().accept(visitor.getHero());
        System.out.println("Услуга для " + visitor.getName() + " завершена");
        for (Unit unit : visitor.getHero().getArmy()) {
            System.out.println("Юнит: здоровье = " + unit.getCurrentHealth() + ", бонус атаки = " + unit.getAttackBonus());
        }
        if (!waitingQueue.isEmpty()) {
            ServiceRequest next = waitingQueue.poll();
            if (next.visitor.getHero() != null && next.visitor.getHero().canAfford(next.service.getCost())) {
                next.visitor.getHero().spendGold(next.service.getCost());
                startService(next.visitor, next.service);
            } else if (next.visitor.getHero() == null) {
                startService(next.visitor, next.service);
            }
        }
    }

    public void printStatistics() {
        System.out.println("Здание: " + type);
        if (currentVisitors.isEmpty()) {
            System.out.println("Нет посетителей");
        } else {
            System.out.println("Текущие посетители:");
            for (Visitor visitor : currentVisitors) {
                Service service = visitorServices.get(visitor);
                long remaining = (completionTimes.get(visitor) - System.currentTimeMillis()) / 100;
                System.out.println(visitor.getName() + " - услуга: " + service.getName() + ", осталось: " + remaining + " мин");
            }
        }
    }

    public List<Service> getServices() {
        return services;
    }
}