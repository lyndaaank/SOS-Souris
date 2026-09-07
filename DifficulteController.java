package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import com.projetihm.application.model.Difficulte;
import javafx.fxml.FXML;

public class DifficulteController {

    @FXML
    public void choisirFacile() {
        choisirDifficulte(Difficulte.FACILE);
    }

    @FXML
    public void choisirNormal() {
        choisirDifficulte(Difficulte.NORMAL);
    }

    @FXML
    public void choisirDifficile() {
        choisirDifficulte(Difficulte.DIFFICILE);
    }

    @FXML
    public void retourMenu() {
        WindowManager.afficherMenu();
    }

    private void choisirDifficulte(Difficulte difficulte) {
        WindowManager.setDifficulteActuelle(difficulte);
        WindowManager.afficherCreationPerso();
    }
}
