package com.game.city;

public class ServiceRequest {
    public final Visitor visitor;
    public final Service service;

    public ServiceRequest(Visitor visitor, Service service) {
        this.visitor = visitor;
        this.service = service;
    }
}