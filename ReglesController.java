package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import javafx.fxml.FXML;

public class ReglesController {

    @FXML
    public void onRetourClick() {
        WindowManager.afficherMenu();
    }
}
