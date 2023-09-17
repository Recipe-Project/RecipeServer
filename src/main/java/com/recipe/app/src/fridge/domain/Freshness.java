package com.recipe.app.src.fridge.domain;

public enum Freshness {
    FRESH("신선"),
    DANGER("위험"),
    DISPOSAL("폐기");

    private String name;

    Freshness(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
