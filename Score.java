package com.projetihm.application.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Score {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String nomJoueur;
    private final Difficulte difficulte;
    private final int tempsRestant;
    private final int score;
    private final String date;

    public Score(String nomJoueur, Difficulte difficulte, int tempsRestant, int score, String date) {
        this.nomJoueur = nomJoueur;
        this.difficulte = difficulte;
        this.tempsRestant = tempsRestant;
        this.score = score;
        this.date = date;
    }

    public static Score depuisPartie(Partie partie) {
        int tempsRestant = partie.getTempsRestant();
        return new Score(
                partie.getJoueur().getNom(),
                partie.getDifficulte(),
                tempsRestant,
                tempsRestant * partie.getDifficulte().getCoefficientScore(),
                LocalDateTime.now().format(FORMAT_DATE)
        );
    }

    public String getNomJoueur() {
        return nomJoueur;
    }

    public Difficulte getDifficulte() {
        return difficulte;
    }

    public int getTempsRestant() {
        return tempsRestant;
    }

    public int getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }
}
