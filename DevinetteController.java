package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import com.projetihm.application.model.DefiDevinette;
import com.projetihm.application.model.Partie;
import com.projetihm.application.model.Score;
import com.projetihm.application.model.ScoreManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class DevinetteController {

    private static final double TAILLE_LETTRE = 62;
    private static final double DECALAGE_CASE_X = 0;
    private static final double DECALAGE_CASE_Y = -3;

    @FXML
    private AnchorPane root;

    private final List<Rectangle> casesReponse = new ArrayList<>();
    private final List<ImageView> lettres = new ArrayList<>();
    private DefiDevinette defiDevinette;
    private Partie partie;
    private ImageView[] reponse;
    private AnchorPane erreurPane;
    private boolean scoreEnregistre = false;

    @FXML
    public void initialize() {
        afficherAssistant();
    }

    private void afficherAssistant() {
        root.getChildren().clear();
        afficherFond();
        partie = WindowManager.getPartieActuelle();
        InventaireController.ajouterInventaireSansLettres(root, partie, WindowManager::afficherDefaiteFinale);
        AssistantController.ajouterMessage(
                root,
                partie.expliquerDefi(4),
                "COMMENCER",
                e -> afficherEpreuveDevinette()
        );
    }

    private void afficherEpreuveDevinette() {
        root.getChildren().clear();
        casesReponse.clear();
        lettres.clear();
        defiDevinette = new DefiDevinette();
        reponse = new ImageView[defiDevinette.getNombreLettres()];
        afficherFond();
        creerCasesReponse();
        creerLettresMelangees();
        creerBoutonValider();
        InventaireController.ajouterInventaireSansLettres(root, partie, WindowManager::afficherDefaiteFinale);
    }

    private void afficherFond() {
        ImageView fond = new ImageView(chargerImage("digicode.png"));
        fond.setFitWidth(WindowManager.LARGEUR_REFERENCE);
        fond.setFitHeight(WindowManager.HAUTEUR_REFERENCE);
        fond.setPreserveRatio(false);
        root.getChildren().add(fond);
    }

    private void creerCasesReponse() {
        double[] xCases = {608, 681, 754, 828, 901, 975};
        double y = 326;

        for (double x : xCases) {
            Rectangle rectangle = new Rectangle(x, y, 62, 56);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.TRANSPARENT);
            casesReponse.add(rectangle);
            root.getChildren().add(rectangle);
        }
    }

    private void creerLettresMelangees() {
        ajouterLettre("T", "lettre_t.png", 624, 416);
        ajouterLettre("E", "lettre_e_1.png", 729, 486);
        ajouterLettre("C", "lettre_c.png", 834, 416);
        ajouterLettre("S", "lettre_s.png", 939, 486);
        ajouterLettre("R", "lettre_r.png", 624, 556);
        ajouterLettre("E", "lettre_e_2.png", 834, 556);
    }

    private void ajouterLettre(String valeur, String image, double x, double y) {
        ImageView lettre = new ImageView(chargerImage(image));
        lettre.setUserData(valeur);
        lettre.setLayoutX(x);
        lettre.setLayoutY(y);
        lettre.setFitWidth(TAILLE_LETTRE);
        lettre.setPreserveRatio(true);
        lettre.setCursor(Cursor.HAND);
        lettre.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.45)));

        final double[] decalage = new double[2];

        lettre.setOnMousePressed(e -> {
            decalage[0] = e.getSceneX() - lettre.getLayoutX();
            decalage[1] = e.getSceneY() - lettre.getLayoutY();
            retirerLettreDeLaReponse(lettre);
            lettre.toFront();
            e.consume();
        });

        lettre.setOnMouseDragged(e -> {
            lettre.setLayoutX(e.getSceneX() - decalage[0]);
            lettre.setLayoutY(e.getSceneY() - decalage[1]);
            e.consume();
        });

        lettre.setOnMouseReleased(e -> {
            deposerLettre(lettre);
            e.consume();
        });

        lettres.add(lettre);
        root.getChildren().add(lettre);
    }

    private void deposerLettre(ImageView lettre) {
        int indexCase = trouverCaseSousLettre(lettre);

        if (indexCase == -1) {
            return;
        }

        ImageView ancienneLettre = reponse[indexCase];
        if (ancienneLettre != null && ancienneLettre != lettre) {
            ancienneLettre.setLayoutX(lettre.getLayoutX());
            ancienneLettre.setLayoutY(lettre.getLayoutY());
            retirerLettreDeLaReponse(ancienneLettre);
        }

        reponse[indexCase] = lettre;
        Rectangle cible = casesReponse.get(indexCase);
        lettre.setLayoutX(cible.getX() + (cible.getWidth() - TAILLE_LETTRE) / 2 + DECALAGE_CASE_X);
        lettre.setLayoutY(cible.getY() + DECALAGE_CASE_Y);
    }

    private int trouverCaseSousLettre(ImageView lettre) {
        Bounds boundsLettre = lettre.getBoundsInParent();
        double centreX = boundsLettre.getMinX() + boundsLettre.getWidth() / 2;
        double centreY = boundsLettre.getMinY() + boundsLettre.getHeight() / 2;

        for (int i = 0; i < casesReponse.size(); i++) {
            if (casesReponse.get(i).contains(centreX, centreY)) {
                return i;
            }
        }

        return -1;
    }

    private void retirerLettreDeLaReponse(ImageView lettre) {
        for (int i = 0; i < reponse.length; i++) {
            if (reponse[i] == lettre) {
                reponse[i] = null;
            }
        }
    }

    private void creerBoutonValider() {
        Button bouton = new Button("VALIDER");
        bouton.setLayoutX(700);
        bouton.setLayoutY(648);
        bouton.setPrefWidth(250);
        bouton.setPrefHeight(48);
        bouton.getStyleClass().add("devinette-valider-button");
        bouton.setOnAction(e -> validerReponse());
        root.getChildren().add(bouton);
    }

    private void validerReponse() {
        StringBuilder saisie = new StringBuilder();

        for (ImageView lettre : reponse) {
            if (lettre == null) {
                afficherErreurAssistant(partie.annoncerLettresManquantes());
                return;
            }

            saisie.append(lettre.getUserData());
        }

        if (defiDevinette.verifierReponse(saisie.toString())) {
            WindowManager.getPartieActuelle().marquerDefiReussi(Partie.ZoneDefi.SORTIE);
            afficherVictoire();
        } else if (defiDevinette.estPerdu()) {
            afficherDefaite();
        } else {
            afficherErreurAssistant(partie.annoncerErreur(4, defiDevinette.getEssaisRestants()));
        }
    }

    private void afficherVictoire() {
        InventaireController.retirerInventaire(root);
        afficherEcranFinal("victoire.png", true);
    }

    private void afficherDefaite() {
        InventaireController.retirerInventaire(root);
        afficherEcranFinal("defaite.png", false);
    }

    private void afficherEcranFinal(String nomImage, boolean victoire) {
        InventaireController.retirerInventaire(root);
        root.getChildren().clear();

        ImageView ecran = new ImageView(chargerImage(nomImage));
        ecran.setFitWidth(WindowManager.LARGEUR_REFERENCE);
        ecran.setFitHeight(WindowManager.HAUTEUR_REFERENCE);
        ecran.setPreserveRatio(false);
        root.getChildren().add(ecran);

        if (victoire) {
            enregistrerScoreSiNecessaire();
            root.getChildren().add(creerPanneauScore());
        }

        Button fermer = new Button("X");
        fermer.setLayoutX(1610);
        fermer.setLayoutY(25);
        fermer.setPrefWidth(38);
        fermer.setPrefHeight(38);
        fermer.setFont(Font.font("Cooper Black", FontWeight.BOLD, 18));
        fermer.setTextFill(Color.WHITE);
        fermer.setStyle("-fx-background-color: #c91f1f;"
                + "-fx-border-color: #7a0f0f;"
                + "-fx-border-width: 2;"
                + "-fx-background-radius: 4;"
                + "-fx-border-radius: 4;");
        fermer.setOnAction(e -> Platform.exit());
        root.getChildren().add(fermer);
    }

    private void enregistrerScoreSiNecessaire() {
        if (scoreEnregistre) {
            return;
        }

        ScoreManager.enregistrerScore(Score.depuisPartie(partie));
        scoreEnregistre = true;
    }

    private VBox creerPanneauScore() {
        VBox panneau = new VBox(7);
        panneau.setLayoutX(1048);
        panneau.setLayoutY(318);
        panneau.setPrefWidth(465);
        panneau.setAlignment(Pos.CENTER_LEFT);
        panneau.setStyle("-fx-background-color: rgba(255, 247, 223, 0.90);"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: #0b5ea8;"
                + "-fx-border-width: 4;"
                + "-fx-border-radius: 18;"
                + "-fx-padding: 18 24 18 24;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.36), 14, 0.22, 0, 5);");

        panneau.getChildren().add(creerLabelScore("Score final", 26, "#0b5ea8"));
        panneau.getChildren().add(creerLabelScore("Joueur : " + partie.getJoueur().getNom(), 18, "#12395c"));
        panneau.getChildren().add(creerLabelScore("Difficulte : " + partie.getDifficulte().getLibelle(), 18, "#12395c"));
        panneau.getChildren().add(creerLabelScore("Temps restant : " + InventaireController.formaterTemps(partie.getTempsRestant()), 18, "#12395c"));
        panneau.getChildren().add(creerLabelScore("Score : " + partie.calculerScore(), 23, "#b47a00"));
        panneau.getChildren().add(creerLabelScore("Meilleurs scores", 21, "#0b5ea8"));

        int rang = 1;
        for (Score score : ScoreManager.lireMeilleursScores(5)) {
            String ligne = rang + ". " + score.getNomJoueur()
                    + " - " + score.getScore()
                    + " pts (" + score.getDifficulte().getLibelle() + ")";
            panneau.getChildren().add(creerLabelScore(ligne, 15, "#12395c"));
            rang++;
        }

        return panneau;
    }

    private Label creerLabelScore(String texte, int taille, String couleur) {
        Label label = new Label(texte);
        label.setFont(Font.font("Arial", FontWeight.BOLD, taille));
        label.setTextFill(Color.web(couleur));
        label.setWrapText(true);
        return label;
    }

    private void afficherErreurAssistant(String texte) {
        if (erreurPane != null) {
            root.getChildren().remove(erreurPane);
        }

        erreurPane = AssistantController.creerErreur(texte);
        root.getChildren().add(erreurPane);
        erreurPane.setVisible(true);
        erreurPane.toFront();
    }

    private Image chargerImage(String nomFichier) {
        return new Image(getClass().getResource("/com/projetihm/application/images/" + nomFichier).toExternalForm());
    }
}
