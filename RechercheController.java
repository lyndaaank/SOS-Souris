package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import com.projetihm.application.model.DefiRecherche;
import com.projetihm.application.model.Partie;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class RechercheController {

    @FXML
    private AnchorPane root;

    private Label titreLabel;
    private final List<Label> objetsLabels = new ArrayList<>();
    private AnchorPane erreurPane;
    private List<String> objetsRestants;
    private DefiRecherche defiRecherche;
    private Partie partie;

    @FXML
    public void initialize() {
        partie = WindowManager.getPartieActuelle();
        afficherAssistant();
    }

    private void afficherAssistant() {
        root.getChildren().clear();
        ImageView fond = creerImage("tiroir_haut.png");
        root.getChildren().add(fond);
        InventaireController.ajouterInventaireLettresADroite(root, partie, WindowManager::afficherDefaiteFinale);
        AssistantController.ajouterMessage(root, partie.expliquerDefi(2), "COMMENCER", e -> afficherEpreuveRecherche());
    }

    private void afficherEpreuveRecherche() {
        root.getChildren().clear();
        defiRecherche = new DefiRecherche();

        ImageView tiroir = creerImage("tiroir_haut.png");
        root.getChildren().add(tiroir);

        objetsRestants = new ArrayList<>(defiRecherche.getATrouver());

        titreLabel = creerLabel("Objets a trouver :", 500, 850, 20);
        titreLabel.setStyle("-fx-background-color: transparent;");
        titreLabel.setTextFill(Color.WHITE);

        erreurPane = AssistantController.creerErreur(partie.annoncerErreur(2, 1));

        List<ImageView> imagesObjets = new ArrayList<>();
        ajouterObjet(imagesObjets, "Loupe", "loupe.png", 470, 200, 110);
        ajouterObjet(imagesObjets, "Fiole rose", "fiole_rose.png", 543, 267, 55);
        ajouterObjet(imagesObjets, "Bloc-note", "bloc_note.png", 615, 200, 130);
        ajouterObjet(imagesObjets, "Lunettes", "lunettes_labo.png", 780, 200, 160);
        ajouterObjet(imagesObjets, "Serviette", "serviette.png", 920, 200, 170);
        ajouterObjet(imagesObjets, "Boite", "boite_lames.png", 1060, 190, 122);
        ajouterObjet(imagesObjets, "Feuille", "feuille_formules.png", 465, 360, 170);
        ajouterObjet(imagesObjets, "Tournevis", "tournevis.png", 630, 360, 96);
        ajouterObjet(imagesObjets, "Lampe", "lampe.png", 735, 335, 120);
        ajouterObjet(imagesObjets, "Gants", "gants.png", 845, 405, 125);
        ajouterObjet(imagesObjets, "Pipette", "pipette.png", 525, 470, 96);
        ajouterObjet(imagesObjets, "Boite de Petri", "boite_petri.png", 845, 290, 92);
        ajouterObjet(imagesObjets, "Fiole jaune", "fiole_jaune.png", 745, 435, 45);
        ajouterObjet(imagesObjets, "Fiole verte", "fiole_verte.png", 970, 280, 55);
        ajouterObjet(imagesObjets, "Fiole bleue", "fiole_bleue.png", 630, 485, 60);
        ajouterObjet(imagesObjets, "Seringue", "seringue.png", 1085, 335, 72);
        ajouterObjet(imagesObjets, "Carnet", "carnet_vert.png", 1065, 410, 158);

        root.getChildren().addAll(imagesObjets);
        root.getChildren().add(titreLabel);
        mettreAJourListeObjets();
        InventaireController.ajouterInventaireLettresADroite(root, partie, WindowManager::afficherDefaiteFinale);
        root.getChildren().add(erreurPane);
    }

    private void ajouterObjet(List<ImageView> imagesObjets, String nom, String image, double x, double y, double largeur) {
        ImageView imageObjet = creerObjet(image, x, y, largeur);
        imageObjet.setOnMouseClicked(e -> gererClicObjet(nom, imageObjet));
        imagesObjets.add(imageObjet);
    }

    private void gererClicObjet(String nom, ImageView imageObjet) {
        if (!defiRecherche.verifierObjet(nom)) {
            defiRecherche.ajouterErreur();
            afficherErreurAssistant();
            return;
        }

        imageObjet.setVisible(false);
        defiRecherche.retirerObjet(nom);
        objetsRestants.remove(nom);
        mettreAJourListeObjets();

        if (defiRecherche.estReussi()) {
            afficherVictoire();
        }
    }

    private void mettreAJourListeObjets() {
        root.getChildren().removeAll(objetsLabels);
        objetsLabels.clear();

        int nombreColonnes = (objetsRestants.size() + 1) / 2;
        double departX = 710;
        double departY = 825;
        double espaceColonnes = 200;
        double espaceLignes = 54;

        for (int ligne = 0; ligne < 2; ligne++) {
            for (int colonne = 0; colonne < nombreColonnes; colonne++) {
                int index = colonne * 2 + ligne;

                if (index < objetsRestants.size()) {
                    Label label = creerLabel(objetsRestants.get(index),
                            departX + colonne * espaceColonnes,
                            departY + ligne * espaceLignes,
                            22);
                    label.setStyle("-fx-background-color: transparent;");
                    label.setTextFill(Color.WHITE);
                    objetsLabels.add(label);
                }
            }
        }

        root.getChildren().addAll(objetsLabels);
    }

    private void afficherErreurAssistant() {
        int malus = defiRecherche.calculerMalus();
        partie.retirerTemps(malus);
        InventaireController.ajouterInventaireLettresADroite(root, partie, WindowManager::afficherDefaiteFinale);

        if (erreurPane != null) {
            root.getChildren().remove(erreurPane);
        }

        erreurPane = AssistantController.creerErreur(partie.annoncerErreur(malus));
        root.getChildren().add(erreurPane);
        erreurPane.setVisible(true);
        erreurPane.toFront();
    }

    private void afficherVictoire() {
        root.getChildren().clear();

        ImageView fond = creerImage("tiroir_haut.png");
        root.getChildren().add(fond);
        AssistantController.ajouterMessage(root, partie.bravo(2), "RECUPERER", e -> {
            WindowManager.getPartieActuelle().marquerDefiReussi(Partie.ZoneDefi.RECHERCHE);
            WindowManager.afficherRoom();
        }, 685, 742, 250, 260, 590);

        ImageView cle = creerObjet("key.png", 540, 670, 48);
        ImageView lettreC = creerObjet("lettre_c.png", 610, 680, 38);
        ImageView lettreR = creerObjet("lettre_r.png", 655, 680, 38);

        root.getChildren().addAll(cle, lettreC, lettreR);
        InventaireController.ajouterInventaireLettresADroite(root, partie, WindowManager::afficherDefaiteFinale);
    }

    private ImageView creerImage(String nomFichier) {
        ImageView imageView = new ImageView(chargerImage(nomFichier));
        imageView.setLayoutX(0);
        imageView.setLayoutY(0);
        imageView.setFitWidth(1672);
        imageView.setFitHeight(941);
        imageView.setPreserveRatio(false);
        return imageView;
    }

    private ImageView creerObjet(String nomFichier, double x, double y, double largeur) {
        ImageView imageView = new ImageView(chargerImage(nomFichier));
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
        imageView.setFitWidth(largeur);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(false);
        return imageView;
    }

    private Image chargerImage(String nomFichier) {
        return new Image(getClass().getResource("/com/projetihm/application/images/" + nomFichier).toExternalForm());
    }

    private Label creerLabel(String texte, double x, double y, int taille) {
        Label label = new Label(texte);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setFont(Font.font("Arial", FontWeight.BOLD, taille));
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-background-color: rgba(0, 0, 0, 0.55); -fx-padding: 6 10 6 10; -fx-background-radius: 6;");
        return label;
    }
}
