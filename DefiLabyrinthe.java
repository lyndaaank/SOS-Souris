package com.projetihm.application.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefiLabyrinthe extends Defi {

    private static final int MUR = 1;
    private static final int CHEMIN = 0;
    private static final int LIGNES = 13;
    private static final int COLONNES = 19;

    private final int[][] grille;
    private final int[] posSouris;
    private final int[] posSortie;

    public DefiLabyrinthe() {
        super(-1, new char[]{'E', 'T'});
        this.grille = new int[LIGNES][COLONNES];
        this.posSouris = new int[]{1, 1};
        this.posSortie = new int[]{LIGNES - 2, COLONNES - 2};
        deverrouiller();
        generer();
    }

    public void generer() {
        for (int ligne = 0; ligne < LIGNES; ligne++) {
            for (int colonne = 0; colonne < COLONNES; colonne++) {
                grille[ligne][colonne] = MUR;
            }
        }

        creuserDepuis(1, 1);
        posSouris[0] = 1;
        posSouris[1] = 1;
        grille[posSortie[0]][posSortie[1]] = CHEMIN;
    }

    private void creuserDepuis(int ligne, int colonne) {
        grille[ligne][colonne] = CHEMIN;

        List<int[]> directions = new ArrayList<>();
        directions.add(new int[]{-2, 0});
        directions.add(new int[]{2, 0});
        directions.add(new int[]{0, -2});
        directions.add(new int[]{0, 2});
        Collections.shuffle(directions);

        for (int[] direction : directions) {
            int nouvelleLigne = ligne + direction[0];
            int nouvelleColonne = colonne + direction[1];

            if (!estDansGrille(nouvelleLigne, nouvelleColonne) || grille[nouvelleLigne][nouvelleColonne] == CHEMIN) {
                continue;
            }

            grille[ligne + direction[0] / 2][colonne + direction[1] / 2] = CHEMIN;
            creuserDepuis(nouvelleLigne, nouvelleColonne);
        }
    }

    public void deplacerSouris(String direction) {
        int nouvelleLigne = posSouris[0];
        int nouvelleColonne = posSouris[1];

        switch (direction) {
            case "HAUT" -> nouvelleLigne--;
            case "BAS" -> nouvelleLigne++;
            case "GAUCHE" -> nouvelleColonne--;
            case "DROITE" -> nouvelleColonne++;
            default -> {
                return;
            }
        }

        if (!estChemin(nouvelleLigne, nouvelleColonne)) {
            return;
        }

        posSouris[0] = nouvelleLigne;
        posSouris[1] = nouvelleColonne;

        if (sourisEstSortie()) {
            marquerReussi();
        }
    }

    public boolean sourisEstSortie() {
        return posSouris[0] == posSortie[0] && posSouris[1] == posSortie[1];
    }

    public boolean estChemin(int ligne, int colonne) {
        return estDansGrille(ligne, colonne) && grille[ligne][colonne] == CHEMIN;
    }

    private boolean estDansGrille(int ligne, int colonne) {
        return ligne > 0 && ligne < LIGNES - 1 && colonne > 0 && colonne < COLONNES - 1;
    }

    public int[][] getGrille() {
        int[][] copie = new int[LIGNES][COLONNES];
        for (int ligne = 0; ligne < LIGNES; ligne++) {
            System.arraycopy(grille[ligne], 0, copie[ligne], 0, COLONNES);
        }
        return copie;
    }

    public int[] getPosSouris() {
        return new int[]{posSouris[0], posSouris[1]};
    }

    public int[] getPosSortie() {
        return new int[]{posSortie[0], posSortie[1]};
    }
}
