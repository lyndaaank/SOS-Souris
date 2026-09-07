package com.projetihm.application.model;

public enum Difficulte {
    FACILE("Facile", 20, 1),
    NORMAL("Normal", 15, 2),
    DIFFICILE("Difficile", 10, 3);

    private final String libelle;
    private final int tempsInitialMinutes;
    private final int coefficientScore;

    Difficulte(String libelle, int tempsInitialMinutes, int coefficientScore) {
        this.libelle = libelle;
        this.tempsInitialMinutes = tempsInitialMinutes;
        this.coefficientScore = coefficientScore;
    }

    public String getLibelle() {
        return libelle;
    }

    public int getTempsInitialMinutes() {
        return tempsInitialMinutes;
    }

    public int getCoefficientScore() {
        return coefficientScore;
    }
}
