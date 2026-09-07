package com.projetihm.application.model;

public class DefiDevinette extends Defi {

    private static final String REPONSE = "SECRET";
    private static final int NOMBRE_ESSAIS_MAX = 3;
    private int essaisRestants = NOMBRE_ESSAIS_MAX;

    public DefiDevinette() {
        super(-1, new char[]{});
        deverrouiller();
    }

    public int getNombreLettres() {
        return REPONSE.length();
    }

    public boolean verifierReponse(String reponse) {
        boolean correcte = REPONSE.equalsIgnoreCase(reponse);

        if (correcte) {
            marquerReussi();
        } else {
            ajouterErreur();
            essaisRestants--;
        }

        return correcte;
    }

    public int getEssaisRestants() {
        return essaisRestants;
    }

    public boolean estPerdu() {
        return essaisRestants <= 0 && !estReussi();
    }
}
