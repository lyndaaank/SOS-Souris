package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import com.projetihm.application.model.CouleurBlouse;
import com.projetihm.application.model.Joueur;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class CreationPersonnageController {

    private static final String IMAGE_DIR = "/com/projetihm/application/images/personnages/";
    private static final String IMAGE_LUNETTES = IMAGE_DIR + "lunettes.png";
    private static final String IMAGE_CHEVEUX = IMAGE_DIR + "cheveux.png";
    private static final String IMAGE_BADGE = IMAGE_DIR + "badge_personnage.png";

    @FXML
    private TextField nomField;

    @FXML
    private ImageView personnageImage;

    @FXML
    private ImageView lunettesImage;

    @FXML
    private ImageView cheveuxImage;

    @FXML
    private ImageView badgeImage;

    private CouleurBlouse couleurChoisie = CouleurBlouse.BLANCHE;
    private boolean lunettes = false;
    private boolean cheveux = false;
    private boolean badge = false;

    @FXML
    public void initialize() {
        nomField.setText("Yasmine");
        mettreAJourApercu();
    }

    @FXML
    public void choisirBlanc() {
        choisirCouleur(CouleurBlouse.BLANCHE);
    }

    @FXML
    public void choisirBleu() {
        choisirCouleur(CouleurBlouse.BLEUE);
    }

    @FXML
    public void choisirVert() {
        choisirCouleur(CouleurBlouse.VERTE);
    }

    @FXML
    public void choisirJaune() {
        choisirCouleur(CouleurBlouse.JAUNE);
    }

    @FXML
    public void toggleLunettes() {
        lunettes = !lunettes;
        mettreAJourApercu();
    }

    @FXML
    public void toggleCheveux() {
        cheveux = !cheveux;
        mettreAJourApercu();
    }

    @FXML
    public void toggleBadge() {
        badge = !badge;
        mettreAJourApercu();
    }

    @FXML
    public void onAleatoireClick() {
        Joueur joueur = new Joueur();
        joueur.genererAleatoire();

        nomField.setText(joueur.getNom());
        couleurChoisie = joueur.getCouleurBlouse();
        lunettes = joueur.isLunettes();
        cheveux = joueur.isCheveux();
        badge = joueur.isBadge();

        mettreAJourApercu();
    }

    @FXML
    public void onValiderClick() {
        Joueur joueur = new Joueur(
                nomField.getText(),
                couleurChoisie,
                lunettes,
                cheveux,
                badge
        );

        WindowManager.setJoueurActuel(joueur);
        WindowManager.afficherRoom();
    }

    @FXML
    public void onRetourClick() {
        WindowManager.afficherDifficulte();
    }

    private void mettreAJourApercu() {
        personnageImage.setImage(chargerImage(getImagePersonnage()));

        lunettesImage.setImage(chargerImage(IMAGE_LUNETTES));
        lunettesImage.setVisible(lunettes);

        cheveuxImage.setImage(chargerImage(IMAGE_CHEVEUX));
        cheveuxImage.setVisible(cheveux);

        badgeImage.setImage(chargerImage(IMAGE_BADGE));
        badgeImage.setVisible(badge);
    }

    private void choisirCouleur(CouleurBlouse couleur) {
        couleurChoisie = couleur;
        mettreAJourApercu();
    }

    private String getImagePersonnage() {
        return switch (couleurChoisie) {
            case BLANCHE -> IMAGE_DIR + "personnage_blanc.png";
            case BLEUE -> IMAGE_DIR + "personnage_bleu.png";
            case VERTE -> IMAGE_DIR + "personnage_vert.png";
            case JAUNE -> IMAGE_DIR + "personnage_jaune.png";
        };
    }

    private Image chargerImage(String chemin) {
        InputStream flux = getClass().getResourceAsStream(chemin);

        if (flux == null) {
            System.err.println("Image introuvable : " + chemin);
            return null;
        }

        return new Image(flux);
    }
}
