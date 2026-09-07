package com.projetihm.application.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Joueur {

    private String nom;
    private CouleurBlouse couleurBlouse;
    private boolean lunettes;
    private boolean cheveux;
    private boolean badge;
    private double posX;
    private double posY;
    private final boolean[] cles;
    private final List<Character> lettres;

    public Joueur() {
        this.nom = "Iness";
        this.couleurBlouse = CouleurBlouse.BLANCHE;
        this.lunettes = false;
        this.cheveux = false;
        this.badge = false;
        this.posX = 800;
        this.posY = 570;
        this.cles = new boolean[3];
        this.lettres = new ArrayList<>();
    }

    public Joueur(String nom, CouleurBlouse couleurBlouse, boolean lunettes, boolean cheveux) {
        this(nom, couleurBlouse, lunettes, cheveux, false);
    }

    public Joueur(String nom, CouleurBlouse couleurBlouse, boolean lunettes, boolean cheveux, boolean badge) {
        setNom(nom);
        this.couleurBlouse = couleurBlouse;
        this.lunettes = lunettes;
        this.cheveux = cheveux;
        this.badge = badge;
        this.posX = 800;
        this.posY = 570;
        this.cles = new boolean[3];
        this.lettres = new ArrayList<>();
    }

    public void genererAleatoire() {
        String[] noms = {"Lynda", "Nova", "Lina", "Moha", "Aya", "Sam"};
        CouleurBlouse[] couleurs = CouleurBlouse.values();

        this.nom = noms[(int) (Math.random() * noms.length)];
        this.couleurBlouse = couleurs[(int) (Math.random() * couleurs.length)];
        this.lunettes = Math.random() < 0.5;
        this.cheveux = Math.random() < 0.5;
        this.badge = Math.random() < 0.5;
    }

    public void deplacer(double x, double y) {
        this.posX = x;
        this.posY = y;
    }

    public void ajouterCle(int n) {
        if (n >= 0 && n < cles.length) {
            cles[n] = true;
        }
    }

    public void retirerCle(int n) {
        if (n >= 0 && n < cles.length) {
            cles[n] = false;
        }
    }

    public void ajouterLettres(char[] lettresRecues) {
        if (lettresRecues == null) {
            return;
        }

        for (char lettre : lettresRecues) {
            lettres.add(lettre);
        }
    }

    public boolean aCle(int n) {
        return n >= 0 && n < cles.length && cles[n];
    }

    public boolean[] getCles() {
        return Arrays.copyOf(cles, cles.length);
    }

    public List<Character> getLettres() {
        return Collections.unmodifiableList(lettres);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.isBlank()) {
            this.nom = "Alex";
        } else {
            this.nom = nom;
        }
    }

    public CouleurBlouse getCouleurBlouse() {
        return couleurBlouse;
    }

    public boolean isLunettes() {
        return lunettes;
    }

    public boolean isCheveux() {
        return cheveux;
    }

    public boolean isBadge() {
        return badge;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }
}
