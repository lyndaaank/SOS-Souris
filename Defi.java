package com.projetihm.application.model;

import java.util.Arrays;

public abstract class Defi {

    protected EtatDefi etat;
    protected int cleRecompense;
    protected char[] lettresRecompense;
    protected int nbErreurs;

    protected Defi(int cleRecompense, char[] lettresRecompense) {
        this.etat = EtatDefi.VERROUILLE;
        this.cleRecompense = cleRecompense;
        this.lettresRecompense = lettresRecompense == null ? new char[0] : Arrays.copyOf(lettresRecompense, lettresRecompense.length);
        this.nbErreurs = 0;
    }

    public boolean estReussi() {
        return etat == EtatDefi.REUSSI;
    }

    public void deverrouiller() {
        if (etat == EtatDefi.VERROUILLE) {
            etat = EtatDefi.EN_COURS;
        }
    }

    public void marquerReussi() {
        etat = EtatDefi.REUSSI;
    }

    public void ajouterErreur() {
        nbErreurs++;
    }

    public int calculerMalus() {
        if (nbErreurs <= 0) {
            return 0;
        }

        return (int) Math.pow(2, nbErreurs - 1);
    }

    public int getCleRecompense() {
        return cleRecompense;
    }

    public char[] getLettresRecompense() {
        return Arrays.copyOf(lettresRecompense, lettresRecompense.length);
    }
}
