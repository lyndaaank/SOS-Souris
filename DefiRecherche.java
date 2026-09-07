package com.projetihm.application.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefiRecherche extends Defi {

    private final List<String> tousObjets = List.of(
            "Loupe",
            "Bloc-note",
            "Carnet",
            "Serviette",
            "Feuille",
            "Boite",
            "Tournevis",
            "Lampe",
            "Lunettes",
            "Gants",
            "Pipette",
            "Boite de Petri",
            "Fiole jaune",
            "Fiole rose",
            "Fiole verte",
            "Fiole bleue",
            "Seringue"
    );

    private final List<String> aTrouver = new ArrayList<>();

    public DefiRecherche() {
        super(1, new char[]{'C', 'R'});
        deverrouiller();
        tirerAleatoire();
    }

    public void tirerAleatoire() {
        aTrouver.clear();

        List<String> objetsMelanges = new ArrayList<>(tousObjets);
        Collections.shuffle(objetsMelanges);

        for (int i = 0; i < 5; i++) {
            aTrouver.add(objetsMelanges.get(i));
        }
    }

    public boolean verifierObjet(String objet) {
        return aTrouver.contains(objet);
    }

    public void retirerObjet(String objet) {
        aTrouver.remove(objet);

        if (aTrouver.isEmpty()) {
            marquerReussi();
        }
    }

    public List<String> getATrouver() {
        return Collections.unmodifiableList(aTrouver);
    }
}
