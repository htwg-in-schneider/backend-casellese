package de.htwg.in.wete.backend.model;

public enum Category {
    BROT("Brot"),
    SALAMI("Salami"),
    KAESE("Käse");

    private final String germanName;

    Category(String germanName) {
        this.germanName = germanName;
    }

    public String getGermanName() {
        return germanName;
    }
}