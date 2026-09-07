package com.projetihm.application.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partie {

    public enum ZoneDefi {
        AUCUNE,
        CABLES,
        RECHERCHE,
        LABYRINTHE,
        SORTIE
    }

    private static final int SECONDES_PAR_MINUTE = 60;

    private Joueur joueur;
    private Difficulte difficulte;
    private final List<Defi> defis;
    private EtatPartie etat;
    private int etapeDefi;
    private int tempsRestant;
    private long dernierInstantChrono;
    private boolean enCours;
    private String messageActuel;

    public Partie() {
        this(new Joueur());
    }

    public Partie(Joueur joueur) {
        this(joueur, Difficulte.NORMAL);
    }

    public Partie(Joueur joueur, Difficulte difficulte) {
        this.joueur = joueur;
        this.difficulte = difficulte == null ? Difficulte.NORMAL : difficulte;
        this.defis = new ArrayList<>();
        lancerPartie();
    }

    public void lancerPartie() {
        this.etat = EtatPartie.EN_COURS;
        this.etapeDefi = 1;
        this.tempsRestant = difficulte.getTempsInitialMinutes() * SECONDES_PAR_MINUTE;
        this.enCours = true;
        this.dernierInstantChrono = System.currentTimeMillis();
        this.messageActuel = getMessagePremierDefi();
    }



    public void terminerDefi(Defi defi) {
        if (defi == null || !defi.estReussi() || etat != EtatPartie.EN_COURS) {
            return;
        }

        donnerRecompense(defi);
        avancerEtape();
    }

    private void donnerRecompense(Defi defi) {
        if (defi.getCleRecompense() >= 0) {
            joueur.ajouterCle(defi.getCleRecompense());
        }
        joueur.ajouterLettres(defi.getLettresRecompense());
    }

    public void marquerDefiReussi(ZoneDefi zone) {
        if (!estZoneAccessible(zone) || etat != EtatPartie.EN_COURS) {
            return;
        }

        donnerRecompense(zone);
        avancerEtape();
    }

    public void utiliserClePourEntrer(ZoneDefi zone) {
        switch (zone) {
            case RECHERCHE -> joueur.retirerCle(0);
            case LABYRINTHE -> joueur.retirerCle(1);
            default -> {
            }
        }
    }

    private void avancerEtape() {
        if (getZoneProchainDefi() == ZoneDefi.SORTIE) {
            etat = EtatPartie.GAGNE;
            enCours = false;
            messageActuel = bravo(4);
            return;
        }

        etapeDefi++;
        messageActuel = getMessagePremierDefi();
    }



    public boolean verifierDefaite() {
        if (tempsRestant <= 0 && etat == EtatPartie.EN_COURS) {
            etat = EtatPartie.PERDU;
            enCours = false;
        }

        return etat == EtatPartie.PERDU;
    }

    public void demarrerChrono() {
        if (etat != EtatPartie.EN_COURS) {
            return;
        }

        enCours = true;
        dernierInstantChrono = System.currentTimeMillis();
    }

    public void arreterChrono() {
        actualiserChrono();
        enCours = false;
    }

    public int getTempsRestant() {
        actualiserChrono();
        return tempsRestant;
    }

    public int calculerScore() {
        return getTempsRestant() * difficulte.getCoefficientScore();
    }

    public void retirerTemps(int malus) {
        if (malus <= 0) {
            return;
        }

        actualiserChrono();
        tempsRestant = Math.max(0, tempsRestant - malus * SECONDES_PAR_MINUTE);
        dernierInstantChrono = System.currentTimeMillis();
        verifierDefaite();
    }

    private void actualiserChrono() {
        if (!enCours || etat != EtatPartie.EN_COURS) {
            return;
        }

        long maintenant = System.currentTimeMillis();
        if (dernierInstantChrono == 0) {
            dernierInstantChrono = maintenant;
            return;
        }

        int secondesEcoulees = (int) ((maintenant - dernierInstantChrono) / 1000);
        if (secondesEcoulees <= 0) {
            return;
        }

        tempsRestant = Math.max(0, tempsRestant - secondesEcoulees);
        dernierInstantChrono += secondesEcoulees * 1000L;
        verifierDefaite();
    }

    public String expliquerDefi(int n) {
        return switch (n) {
            case 1 -> "Defi cables : relie chaque cable a la prise de meme couleur.";
            case 2 -> "Defi recherche : retrouve les objets demandes dans le tiroir.";
            case 3 -> "Defi labyrinthe : aide la souris a rejoindre le fromage.";
            case 4 -> "Defi devinette : replace les lettres dans le bon ordre grace a l'indice du digicode.";
            default -> "Explore le laboratoire.";
        };
    }

    public String bravo() {
        return "Bravo ! Une cle et deux lettres pour toi.";
    }

    public String bravo(int n) {
        return switch (n) {
            case 1, 2 -> "Bravo !\nVous avez reussi.\nVoici une cle et deux lettres.";
            case 4 -> "Bravo !\nTu as trouve le bon code.";
            default -> "Bravo !\nDefi reussi.";
        };
    }

    public String bravoLabyrinthe() {
        return "Bravo ! Maintenant, devinette pour sortir avec la souris.";
    }

    public String annoncerErreur(int malus) {
        String unite = malus > 1 ? "minutes" : "minute";
        return "Oups, mauvais choix ! -" + malus + " " + unite + " au chrono.";
    }

    public String annoncerErreur(int n, int malus) {
        return switch (n) {
            case 1 -> "Mauvais branchement !\nTu perds " + malus + " minute.";
            case 2 -> "Mauvais objet !\nTu perds " + malus + " minute.";
            case 4 -> "Tu t'es trompe !\nIl te reste " + malus + " essais.";
            default -> "Erreur !\nTu perds " + malus + " minute.";
        };
    }

    public String annoncerLettresManquantes() {
        return "Il manque des lettres !\nPlace les 6 lettres dans les cases.";
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public Difficulte getDifficulte() {
        return difficulte;
    }

    public int getEtapeDefi() {
        return etapeDefi;
    }

    public ZoneDefi getZoneProchainDefi() {
        return switch (etapeDefi) {
            case 1 -> ZoneDefi.CABLES;
            case 2 -> ZoneDefi.RECHERCHE;
            case 3 -> ZoneDefi.LABYRINTHE;
            case 4 -> ZoneDefi.SORTIE;
            default -> ZoneDefi.AUCUNE;
        };
    }

    public boolean estZoneAccessible(ZoneDefi zone) {
        return zone != ZoneDefi.AUCUNE && zone == getZoneProchainDefi() && etat == EtatPartie.EN_COURS;
    }

    public boolean estZoneDejaReussie(ZoneDefi zone) {
        return switch (zone) {
            case CABLES -> etapeDefi > 1;
            case RECHERCHE -> etapeDefi > 2;
            case LABYRINTHE -> etapeDefi > 3;
            case SORTIE -> etat == EtatPartie.GAGNE;
            default -> false;
        };
    }

    private void donnerRecompense(ZoneDefi zone) {
        switch (zone) {
            case CABLES -> {
                joueur.ajouterCle(0);
                joueur.ajouterLettres(new char[]{'S', 'E'});
            }
            case RECHERCHE -> {
                joueur.ajouterCle(1);
                joueur.ajouterLettres(new char[]{'C', 'R'});
            }
            case LABYRINTHE -> {
                joueur.ajouterCle(2);
                joueur.ajouterLettres(new char[]{'E', 'T'});
            }
            default -> {
            }
        }
    }

    public String getMessagePremierDefi() {
        return expliquerDefi(etapeDefi);
    }

    public String getMessageBienvenue() {
        return switch (etapeDefi) {
            case 1 -> "Bienvenue au labo ! Premiere mission : repare le boitier electrique.";
            case 2 -> "Bien joue ! Nouvelle mission : fouille la commode.";
            case 3 -> "Super ! Maintenant, aide la souris a sortir du labyrinthe.";
            case 4 -> "Derniere etape : utilise tes lettres pour ouvrir la porte.";
            default -> "Explore le laboratoire.";
        };
    }

}
