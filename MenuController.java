package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    public void onNouvellePartieClick() {
        WindowManager.afficherDifficulte();
    }

    @FXML
    public void onReglesClick() {
        WindowManager.afficherRegles();
    }
}
