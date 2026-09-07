package com.projetihm.application.Controllers;

import com.projetihm.application.model.Partie;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public final class InventaireController {

    private static final String IMAGE_CLE = "/com/projetihm/application/images/key.png";
    private static final String STYLE_INVENTAIRE_OVERLAY = "inventaire-overlay";
    private static final String STYLE_LETTRE_INVENTAIRE = "lettre-inventaire-overlay";
    private static final String CLE_TIMELINE = "inventaire-timeline";

    private InventaireController() {
    }

    public static void ajouterInventaire(AnchorPane root, Partie partie) {
        ajouterInventaire(root, partie, 836, true, null);
    }

    public static void ajouterInventaire(AnchorPane root, Partie partie, Runnable actionDefaite) {
        ajouterInventaire(root, partie, 836, true, actionDefaite);
    }

    public static void ajouterInventaireLettresADroite(AnchorPane root, Partie partie, Runnable actionDefaite) {
        ajouterInventaire(root, partie, 1340, true, actionDefaite);
    }

    public static void ajouterInventaireSansLettres(AnchorPane root, Partie partie, Runnable actionDefaite) {
        ajouterInventaire(root, partie, 836, false, actionDefaite);
    }

    public static void retirerInventaire(AnchorPane root) {
        Object courant = root.getProperties().remove(CLE_TIMELINE);
        if (courant instanceof Timeline timelineCourante) {
            timelineCourante.stop();
        }
        root.getChildren().removeIf(noeud -> noeud.getStyleClass().contains(STYLE_INVENTAIRE_OVERLAY));
    }

    public static void ajouterLettres(AnchorPane root, Partie partie) {
        ajouterLettres(root, partie, 836);
    }

    private static void ajouterInventaire(AnchorPane root, Partie partie, double centreLettres, boolean afficherLettres, Runnable actionDefaite) {
        dessinerInventaire(root, partie, centreLettres, afficherLettres);
        verifierDefaite(root, partie, actionDefaite);
        demarrerRafraichissement(root, partie, centreLettres, afficherLettres, actionDefaite);
    }

    private static void dessinerInventaire(AnchorPane root, Partie partie, double centreLettres, boolean afficherLettres) {
        root.getChildren().removeIf(noeud -> noeud.getStyleClass().contains(STYLE_INVENTAIRE_OVERLAY));
        ajouterChrono(root, partie);
        ajouterCles(root, partie);
        if (afficherLettres) {
            ajouterLettres(root, partie, centreLettres);
        }
    }

    private static void demarrerRafraichissement(AnchorPane root, Partie partie, double centreLettres, boolean afficherLettres, Runnable actionDefaite) {
        Object existant = root.getProperties().get(CLE_TIMELINE);
        if (existant instanceof Timeline) {
            return;
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (root.getScene() == null) {
                retirerInventaire(root);
                return;
            }

            dessinerInventaire(root, partie, centreLettres, afficherLettres);
            verifierDefaite(root, partie, actionDefaite);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        root.getProperties().put(CLE_TIMELINE, timeline);
        timeline.play();
    }

    private static void verifierDefaite(AnchorPane root, Partie partie, Runnable actionDefaite) {
        if (actionDefaite == null || !partie.verifierDefaite()) {
            return;
        }

        retirerInventaire(root);
        actionDefaite.run();
    }

    private static void ajouterChrono(AnchorPane root, Partie partie) {
        Label chrono = new Label(formaterTemps(partie.getTempsRestant()));
        chrono.setLayoutX(274);
        chrono.setLayoutY(846);
        chrono.setPrefWidth(128);
        chrono.setPrefHeight(46);
        chrono.setAlignment(Pos.CENTER);
        chrono.getStyleClass().addAll("chrono-label", STYLE_INVENTAIRE_OVERLAY);
        root.getChildren().add(chrono);
    }

    private static void ajouterCles(AnchorPane root, Partie partie) {
        boolean[] cles = partie.getJoueur().getCles();
        ajouterCle(root, cles, 0, 1270, 835);
        ajouterCle(root, cles, 1, 1340, 835);
        ajouterCle(root, cles, 2, 1410, 835);
    }

    private static void ajouterCle(AnchorPane root, boolean[] cles, int index, double x, double y) {
        if (index >= cles.length || !cles[index]) {
            return;
        }

        ImageView cle = new ImageView(new Image(InventaireController.class.getResource(IMAGE_CLE).toExternalForm()));
        cle.setLayoutX(x);
        cle.setLayoutY(y);
        cle.setFitWidth(36);
        cle.setPreserveRatio(true);
        cle.setSmooth(true);
        cle.setMouseTransparent(true);
        cle.getStyleClass().add(STYLE_INVENTAIRE_OVERLAY);
        root.getChildren().add(cle);
    }

    private static void ajouterLettres(AnchorPane root, Partie partie, double centreBarre) {
        root.getChildren().removeIf(noeud -> noeud.getStyleClass().contains(STYLE_LETTRE_INVENTAIRE));

        List<Character> lettres = melangerLettres(partie.getJoueur().getLettres());
        double espace = 46;
        double largeurLettre = 38;
        double largeurGroupe = lettres.isEmpty() ? 0 : (lettres.size() - 1) * espace + largeurLettre;
        double departX = centreBarre - largeurGroupe / 2;
        double y = 850;

        for (int i = 0; i < lettres.size(); i++) {
            ImageView lettre = new ImageView(new Image(InventaireController.class.getResource(getImageLettre(lettres.get(i), i)).toExternalForm()));
            lettre.setLayoutX(departX + i * espace);
            lettre.setLayoutY(y);
            lettre.setFitWidth(largeurLettre);
            lettre.setPreserveRatio(true);
            lettre.setSmooth(true);
            lettre.setMouseTransparent(true);
            lettre.getStyleClass().addAll(STYLE_INVENTAIRE_OVERLAY, STYLE_LETTRE_INVENTAIRE);
            root.getChildren().add(lettre);
        }
    }

    private static List<Character> melangerLettres(List<Character> lettresOriginales) {
        List<Character> lettres = new ArrayList<>(lettresOriginales);

        if (lettres.size() == 2) {
            return List.of(lettres.get(1), lettres.get(0));
        }

        if (lettres.size() == 4) {
            return List.of(lettres.get(2), lettres.get(0), lettres.get(3), lettres.get(1));
        }

        if (lettres.size() >= 6) {
            return List.of(lettres.get(2), lettres.get(0), lettres.get(4), lettres.get(1), lettres.get(5), lettres.get(3));
        }

        return lettres;
    }

    private static String getImageLettre(char lettre, int index) {
        return switch (Character.toUpperCase(lettre)) {
            case 'S' -> "/com/projetihm/application/images/lettre_s.png";
            case 'E' -> index >= 4
                    ? "/com/projetihm/application/images/lettre_e_2.png"
                    : "/com/projetihm/application/images/lettre_e_1.png";
            case 'C' -> "/com/projetihm/application/images/lettre_c.png";
            case 'R' -> "/com/projetihm/application/images/lettre_r.png";
            case 'T' -> "/com/projetihm/application/images/lettre_t.png";
            default -> "/com/projetihm/application/images/lettre_s.png";
        };
    }

    public static String formaterTemps(int secondesRestantes) {
        int secondes = Math.max(0, secondesRestantes);
        return String.format("%02d:%02d", secondes / 60, secondes % 60);
    }
}
