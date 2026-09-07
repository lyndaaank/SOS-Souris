package com.projetihm.application.model;

import java.util.HashMap;
import java.util.Map;

public class DefiCables extends Defi {

    private static final int NOMBRE_CABLES = 5;
    private final Map<String, String> branchements = new HashMap<>();

    public DefiCables() {
        super(0, new char[]{'S', 'E'});
        deverrouiller();
    }

    public boolean connecter(String cable, String prise) {
        branchements.put(cable, prise);
        boolean bonBranchement = cable.equals(prise);

        if (!bonBranchement) {
            ajouterErreur();
        }

        verifierReussite();
        return bonBranchement;
    }

    public void deconnecter(String cable) {
        branchements.remove(cable);
        verifierReussite();
    }

    private void verifierReussite() {
        if (branchements.size() != NOMBRE_CABLES) {
            return;
        }

        for (Map.Entry<String, String> branchement : branchements.entrySet()) {
            if (!branchement.getKey().equals(branchement.getValue())) {
                return;
            }
        }

        marquerReussi();
    }
}
