package com.projetihm.application.Controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.InputStream;

public class AssistantController {

    private static final String IMAGE_ASSISTANT = "/com/projetihm/application/images/assistant/scientifique_room.png";
    private static final String IMAGE_BULLE = "/com/projetihm/application/images/assistant/bulle_dialogue.png";
    private static final String IMAGE_ASSISTANT_DEFI = "assistant/scientifique_room.png";
    private static final String IMAGE_BULLE_DEFI = "assistant/bulle_defi.png";
    private static final double LARGEUR_SCENE = 1672;
    private static final double HAUTEUR_SCENE = 941;

    private final AnchorPane assistantPane;
    private final ImageView assistantImage;
    private final ImageView assistantBubbleImage;
    private final Label assistantMessage;

    //attendre 4 secondes avant de cacher l'assistant
    private PauseTransition pauseAssistant;

    public AssistantController(AnchorPane assistantPane,
                               ImageView assistantImage,
                               ImageView assistantBubbleImage,
                               Label assistantMessage) {
        this.assistantPane = assistantPane;
        this.assistantImage = assistantImage;
        this.assistantBubbleImage = assistantBubbleImage;
        this.assistantMessage = assistantMessage;
    }

    public void chargerImages() {
        Image assistant = chargerImage(IMAGE_ASSISTANT);
        if (assistant != null) {
            assistantImage.setImage(assistant);
        }

        Image bulle = chargerImage(IMAGE_BULLE);
        if (bulle != null) {
            assistantBubbleImage.setImage(bulle);
        }
    }

    public void afficherMessage(String message, boolean resterVisible) {
        if (pauseAssistant != null) {
            pauseAssistant.stop();
        }

        assistantMessage.setText(message);
        assistantPane.setVisible(true);
        assistantPane.setOpacity(0);

        FadeTransition apparition = new FadeTransition(Duration.millis(180), assistantPane);
        apparition.setFromValue(0);
        apparition.setToValue(1);
        apparition.play();

        if (!resterVisible) {
            pauseAssistant = new PauseTransition(Duration.seconds(4));
            pauseAssistant.setOnFinished(event -> cacherMessage());
            pauseAssistant.play();
        }
    }

    public void cacherMessage() {
        FadeTransition disparition = new FadeTransition(Duration.millis(250), assistantPane);
        disparition.setFromValue(assistantPane.getOpacity());
        disparition.setToValue(0);
        disparition.setOnFinished(event -> assistantPane.setVisible(false));
        disparition.play();
    }

    private Image chargerImage(String chemin) {
        InputStream flux = getClass().getResourceAsStream(chemin);
        if (flux == null) {
            System.err.println("Image assistant introuvable : " + chemin);
            return null;
        }
        return new Image(flux);
    }

    public static void ajouterMessage(AnchorPane root, String texte, String boutonTexte, EventHandler<ActionEvent> action) {
        ajouterMessage(root, texte, boutonTexte, action, 675, 700, 240, 355, 610);
    }


    public static void ajouterMessage(AnchorPane root,
                                      String texte,
                                      String boutonTexte,
                                      EventHandler<ActionEvent> action,
                                      double texteY,
                                      double boutonY,
                                      double texteX,
                                      double largeurTexte,
                                      double boutonX) {
        ImageView assistant = creerImageDefi(IMAGE_ASSISTANT_DEFI, 45, 630, 120);
        ImageView bulle = creerImageDefi(IMAGE_BULLE_DEFI, 150, 635, 620);
        Label message = creerMessageDefi(texte, texteX, texteY, largeurTexte);
        Button bouton = creerBoutonDefi(boutonTexte, boutonX, boutonY);
        bouton.setOnAction(action);

        root.getChildren().addAll(assistant, bulle, message, bouton);
    }

    public static AnchorPane creerErreur(String texte) {
        AnchorPane pane = new AnchorPane();
        pane.setVisible(false);

        Rectangle fondErreur = new Rectangle(0, 0, LARGEUR_SCENE, HAUTEUR_SCENE);
        fondErreur.setFill(Color.rgb(0, 0, 0, 0.35));

        pane.getChildren().add(fondErreur);
        ajouterMessage(pane, texte, "REPRENDRE", e -> pane.setVisible(false));
        return pane;
    }

    private static Button creerBoutonDefi(String texte, double x, double y) {
        Button bouton = new Button(texte);
        bouton.setLayoutX(x);
        bouton.setLayoutY(y);
        bouton.setPrefWidth(118);
        bouton.setPrefHeight(34);
        bouton.setStyle("-fx-background-color: #f1c232; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: #7b4f2f; -fx-border-width: 2; -fx-border-radius: 8;");
        bouton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        return bouton;
    }

    private static Label creerMessageDefi(String texte, double x, double y, double largeur) {
        Label label = new Label(texte);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        label.setTextFill(Color.web("#12395c"));
        label.setStyle("-fx-background-color: transparent;");
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setMaxWidth(largeur);
        label.setPrefWidth(largeur);
        label.setPrefHeight(78);
        label.setLineSpacing(4);
        return label;
    }

    private static ImageView creerImageDefi(String nomFichier, double x, double y, double largeur) {
        ImageView imageView = new ImageView(chargerImageDefi(nomFichier));
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setFitWidth(largeur);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(false);
        return imageView;
    }

    private static Image chargerImageDefi(String nomFichier) {
        return new Image(AssistantController.class.getResource("/com/projetihm/application/images/" + nomFichier).toExternalForm());
    }
}


