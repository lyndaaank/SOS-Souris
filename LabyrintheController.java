package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import com.projetihm.application.model.DefiLabyrinthe;
import com.projetihm.application.model.Partie;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LabyrintheController {

    private static final double LARGEUR_SCENE = 1672;
    private static final double HAUTEUR_SCENE = 941;
    private static final double X_LABYRINTHE = 528;
    private static final double Y_LABYRINTHE = 189;
    private static final double TAILLE_CASE = 32;
    private static final double TAILLE_SOURIS = 44;
    private static final double TAILLE_FROMAGE = 34;
    private static final int CASE_MUR = 1;
    private static final String IMAGE_FOND = "labyrinthe.png";
    private static final String IMAGE_SOURIS = "souris_tete.png";
    private static final String IMAGE_FROMAGE = "fromage.png";
    private static final String IMAGE_LETTRE_E = "lettre_e_2.png";
    private static final String IMAGE_LETTRE_T = "lettre_t.png";

    @FXML
    private AnchorPane root;

    private final Partie partie = WindowManager.getPartieActuelle();
    private DefiLabyrinthe defiLabyrinthe;
    private ImageView sourisImage;

    @FXML
    public void initialize() {
        afficherAssistant();
    }

    private void afficherAssistant() {
        root.getChildren().clear();
        root.getChildren().add(creerFond());
        InventaireController.ajouterInventaire(root, partie, WindowManager::afficherDefaiteFinale);
        AssistantController.ajouterMessage(root, partie.expliquerDefi(3), "COMMENCER", e -> afficherEpreuveLabyrinthe());
    }

    private void afficherEpreuveLabyrinthe() {
        root.getChildren().clear();
        defiLabyrinthe = new DefiLabyrinthe();

        root.getChildren().add(creerFond());
        dessinerLabyrinthe();
        ajouterFromage();
        ajouterSouris();
        InventaireController.ajouterInventaire(root, partie, WindowManager::afficherDefaiteFinale);

        root.setFocusTraversable(true);
        root.setOnKeyPressed(this::gererTouche);
        root.requestFocus();
    }

    private void dessinerLabyrinthe() {
        int[][] grille = defiLabyrinthe.getGrille();

        for (int ligne = 0; ligne < grille.length; ligne++) {
            for (int colonne = 0; colonne < grille[ligne].length; colonne++) {
                if (grille[ligne][colonne] == CASE_MUR) {
                    root.getChildren().add(creerMur(ligne, colonne));
                }
            }
        }
    }

    private Rectangle creerMur(int ligne, int colonne) {
        Rectangle mur = new Rectangle(
                X_LABYRINTHE + colonne * TAILLE_CASE,
                Y_LABYRINTHE + ligne * TAILLE_CASE,
                TAILLE_CASE,
                TAILLE_CASE
        );
        mur.setArcWidth(7);
        mur.setArcHeight(7);
        mur.setFill(Color.web("#4d7f25"));
        mur.setStroke(Color.web("#2b4d18"));
        mur.setStrokeWidth(2);
        mur.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.28)));
        return mur;
    }

    private void ajouterSouris() {
        sourisImage = creerObjet(IMAGE_SOURIS, TAILLE_SOURIS);
        placerImageSurCase(sourisImage, defiLabyrinthe.getPosSouris(), TAILLE_SOURIS);
        root.getChildren().add(sourisImage);
    }

    private void ajouterFromage() {
        ImageView fromage = creerObjet(IMAGE_FROMAGE, TAILLE_FROMAGE);
        placerImageSurCase(fromage, defiLabyrinthe.getPosSortie(), TAILLE_FROMAGE);
        root.getChildren().add(fromage);
    }

    private ImageView creerObjet(String image, double taille) {
        ImageView objet = new ImageView(chargerImage(image));
        objet.setFitWidth(taille);
        objet.setPreserveRatio(true);
        objet.setSmooth(true);
        objet.setMouseTransparent(true);
        return objet;
    }

    private void placerImageSurCase(ImageView image, int[] position, double taille) {
        image.setLayoutX(X_LABYRINTHE + position[1] * TAILLE_CASE + (TAILLE_CASE - taille) / 2);
        image.setLayoutY(Y_LABYRINTHE + position[0] * TAILLE_CASE + (TAILLE_CASE - taille) / 2);
    }

    private void gererTouche(KeyEvent event) {
        String direction = directionDepuisTouche(event.getCode());
        if (direction == null || defiLabyrinthe.estReussi()) {
            return;
        }

        int[] anciennePosition = defiLabyrinthe.getPosSouris();
        defiLabyrinthe.deplacerSouris(direction);
        int[] nouvellePosition = defiLabyrinthe.getPosSouris();

        if (anciennePosition[0] == nouvellePosition[0] && anciennePosition[1] == nouvellePosition[1]) {
            event.consume();
            return;
        }

        placerImageSurCase(sourisImage, nouvellePosition, TAILLE_SOURIS);
        event.consume();

        if (defiLabyrinthe.estReussi()) {
            afficherVictoire();
        }
    }

    private String directionDepuisTouche(KeyCode code) {
        return switch (code) {
            case UP -> "HAUT";
            case DOWN -> "BAS";
            case LEFT -> "GAUCHE";
            case RIGHT -> "DROITE";
            default -> null;
        };
    }

    private void afficherVictoire() {
        root.setOnKeyPressed(null);
        AssistantController.ajouterMessage(root, partie.bravoLabyrinthe(), "RECUPERER", e -> {
            partie.terminerDefi(defiLabyrinthe);
            WindowManager.afficherRoom();
        }, 685, 728, 250, 260, 592);

        root.getChildren().addAll(
                creerLettreRecompense(IMAGE_LETTRE_E, 610, 680),
                creerLettreRecompense(IMAGE_LETTRE_T, 658, 680)
        );
    }

    private ImageView creerFond() {
        ImageView fond = new ImageView(chargerImage(IMAGE_FOND));
        fond.setFitWidth(LARGEUR_SCENE);
        fond.setFitHeight(HAUTEUR_SCENE);
        fond.setPreserveRatio(false);
        return fond;
    }

    private ImageView creerLettreRecompense(String image, double x, double y) {
        ImageView lettre = creerObjet(image, 38);
        lettre.setLayoutX(x);
        lettre.setLayoutY(y);
        return lettre;
    }

    private Image chargerImage(String nomFichier) {
        return new Image(getClass().getResource("/com/projetihm/application/images/" + nomFichier).toExternalForm());
    }
}
