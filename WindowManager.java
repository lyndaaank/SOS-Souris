package com.projetihm.application;

import com.projetihm.application.model.Joueur;
import com.projetihm.application.model.Partie;
import com.projetihm.application.model.Difficulte;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

public final class WindowManager {

    private static Stage stage;
    private static Scene scene;

    // Joueur créé dans l'écran de création personnage.
    private static Joueur joueurActuel = new Joueur();
    private static Difficulte difficulteActuelle = Difficulte.NORMAL;

    // Partie en cours : elle garde le joueur et la progression globale.
    private static Partie partieActuelle = new Partie(joueurActuel);

    // Taille de référence de toutes nos images principales.
    public static final double LARGEUR_REFERENCE = 1672;
    public static final double HAUTEUR_REFERENCE = 941;

    public WindowManager() {
        stage = new Stage();
        stage.setTitle("SOS Labo - Sauve la souris !");
        stage.setResizable(false);

        afficherIntroVideo();
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.show();
    }

    private static void changerVue(String fichierFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(GameApplication.class.getResource(fichierFXML));
            Parent vue = loader.load();

            Group groupeVue = new Group(vue);
            StackPane conteneur = new StackPane(groupeVue);
            conteneur.setStyle("-fx-background-color: black;");

            if (scene == null) {
                scene = new Scene(conteneur, LARGEUR_REFERENCE, HAUTEUR_REFERENCE);
                scene.setFill(Color.BLACK);
                stage.setScene(scene);
            } else {
                scene.setRoot(conteneur);
            }

            groupeVue.scaleXProperty().bind(
                    Bindings.min(
                            scene.widthProperty().divide(LARGEUR_REFERENCE),
                            scene.heightProperty().divide(HAUTEUR_REFERENCE)
                    )
            );
            groupeVue.scaleYProperty().bind(groupeVue.scaleXProperty());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void afficherMenu() {
        changerVue("menu-view.fxml");
    }

    public static void afficherRegles() {
        changerVue("regles-view.fxml");
    }

    public static void afficherCables() {
        changerVue("cables-view.fxml");
    }

    public static void afficherLabyrinthe() {
        changerVue("labyrinthe-view.fxml");
    }

    public static void afficherDevinette() {
        changerVue("devinette-view.fxml");
    }

    public static void afficherDefaiteFinale() {
        partieActuelle.arreterChrono();
        afficherEcranFinal("defaite.png");
    }

    public static void afficherCreationPerso() {
        changerVue("creation-personnage-view.fxml");
    }

    public static void afficherIntroVideo() {
        changerVue("intro-video-view.fxml");
    }

    public static void afficherDifficulte() {
        changerVue("difficulte-view.fxml");
    }

    public static void afficherRecherche() {
        changerVue("recherche-view.fxml");
    }

    public static void afficherRoom() {
        changerVue("room-view.fxml");
    }

    private static void afficherEcranFinal(String image) {
        AnchorPane vue = new AnchorPane();
        vue.setPrefSize(LARGEUR_REFERENCE, HAUTEUR_REFERENCE);

        ImageView ecran = new ImageView(new Image(GameApplication.class.getResource("/com/projetihm/application/images/" + image).toExternalForm()));
        ecran.setFitWidth(LARGEUR_REFERENCE);
        ecran.setFitHeight(HAUTEUR_REFERENCE);
        ecran.setPreserveRatio(false);
        vue.getChildren().add(ecran);

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
        fermer.setOnAction(e -> quitterApplication());
        vue.getChildren().add(fermer);

        afficherVue(vue);
    }

    private static void afficherVue(Parent vue) {
        Group groupeVue = new Group(vue);
        StackPane conteneur = new StackPane(groupeVue);
        conteneur.setStyle("-fx-background-color: black;");

        if (scene == null) {
            scene = new Scene(conteneur, LARGEUR_REFERENCE, HAUTEUR_REFERENCE);
            scene.setFill(Color.BLACK);
            stage.setScene(scene);
        } else {
            scene.setRoot(conteneur);
        }

        groupeVue.scaleXProperty().bind(
                Bindings.min(
                        scene.widthProperty().divide(LARGEUR_REFERENCE),
                        scene.heightProperty().divide(HAUTEUR_REFERENCE)
                )
        );
        groupeVue.scaleYProperty().bind(groupeVue.scaleXProperty());
    }

    public static Joueur getJoueurActuel() {
        return joueurActuel;
    }

    public static void setJoueurActuel(Joueur joueur) {
        joueurActuel = joueur;
        partieActuelle = new Partie(joueurActuel, difficulteActuelle);
    }

    public static void setDifficulteActuelle(Difficulte difficulte) {
        difficulteActuelle = difficulte == null ? Difficulte.NORMAL : difficulte;
        partieActuelle = new Partie(joueurActuel, difficulteActuelle);
    }

    public static Partie getPartieActuelle() {
        return partieActuelle;
    }

    public static void quitterApplication() {
        Platform.exit();
    }
}
