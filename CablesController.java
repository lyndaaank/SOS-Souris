package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import com.projetihm.application.model.DefiCables;
import com.projetihm.application.model.Partie;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CablesController {

    private static final double LARGEUR_SCENE = 1672;
    private static final double HAUTEUR_SCENE = 941;
    private static final double LARGEUR_EMBOUT = 34;
    private static final double HAUTEUR_EMBOUT = 34;
    private static final double HAUTEUR_CABLE = 24;
    private static final double X_DEPART_CABLE = 692;
    private static final double X_CIBLE_CABLE = 1165;
    private static final double[] Y_CABLES = {230, 317, 405, 493, 582};

    @FXML
    private AnchorPane root;

    private final Partie partie = WindowManager.getPartieActuelle();
    private DefiCables defiCables;
    private AnchorPane erreurPane;
    private List<CibleCable> ciblesActuelles;

    @FXML
    public void initialize() {
        afficherAssistant();
    }

    private void afficherAssistant() {
        root.getChildren().clear();
        root.getChildren().add(creerFondCables());
        InventaireController.ajouterInventaire(root, partie, WindowManager::afficherDefaiteFinale);
        AssistantController.ajouterMessage(root, partie.expliquerDefi(1), "COMMENCER", e -> afficherEpreuveCables());
    }

    private void afficherEpreuveCables() {
        root.getChildren().clear();
        defiCables = new DefiCables();

        root.getChildren().add(creerFondCables());
        InventaireController.ajouterInventaire(root, partie, WindowManager::afficherDefaiteFinale);

        List<CableGraphique> cables = new ArrayList<>();
        cables.add(creerCable("Rouge", Color.web("#d94141"), X_DEPART_CABLE, Y_CABLES[0]));
        cables.add(creerCable("Bleu", Color.web("#2f79d6"), X_DEPART_CABLE, Y_CABLES[1]));
        cables.add(creerCable("Jaune", Color.web("#f0c03a"), X_DEPART_CABLE, Y_CABLES[2]));
        cables.add(creerCable("Vert", Color.web("#55b95b"), X_DEPART_CABLE, Y_CABLES[3]));
        cables.add(creerCable("Violet", Color.web("#a76be8"), X_DEPART_CABLE, Y_CABLES[4]));

        ciblesActuelles = creerCiblesMelangees();
        for (CibleCable cible : ciblesActuelles) {
            root.getChildren().add(creerEmbout(cible.x, cible.y, cible.peinture, false));
        }

        for (CableGraphique cable : cables) {
            root.getChildren().addAll(cable.ombre, cable.courbe, cable.depart, cable.arrivee);
        }

        for (CableGraphique cable : cables) {
            cable.arrivee.toFront();
        }
    }

    private CableGraphique creerCable(String couleur, Color peinture, double departX, double departY) {
        Group depart = creerEmbout(departX, departY, peinture, true);
        Group arrivee = creerEmbout(departX + 70, departY, peinture, true);
        arrivee.setCursor(Cursor.HAND);

        CubicCurve ombre = creerCourbe(departX, departY, centreX(arrivee), centreY(arrivee), Color.rgb(0, 0, 0, 0.45), HAUTEUR_CABLE + 10);
        CubicCurve courbe = creerCourbe(departX, departY, centreX(arrivee), centreY(arrivee), peinture, HAUTEUR_CABLE);
        courbe.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.45)));

        CableGraphique cable = new CableGraphique(couleur, ombre, courbe, depart, arrivee);

        arrivee.setOnMousePressed(e -> {
            defiCables.deconnecter(cable.couleur);
            arrivee.toFront();
            e.consume();
        });

        arrivee.setOnMouseDragged(e -> {
            Point2D position = root.sceneToLocal(e.getSceneX(), e.getSceneY());
            deplacerArrivee(cable, position.getX(), position.getY());
            e.consume();
        });

        arrivee.setOnMouseReleased(e -> {
            Point2D position = root.sceneToLocal(e.getSceneX(), e.getSceneY());
            gererDepotCable(cable, cibleSousPoint(position.getX(), position.getY()));
            e.consume();
        });

        return cable;
    }

    private void gererDepotCable(CableGraphique cable, CibleCable cible) {
        if (cible == null) {
            return;
        }

        deplacerArrivee(cable, cible.x, cible.y);

        boolean bonBranchement = defiCables.connecter(cable.couleur, cible.couleur);
        if (!bonBranchement) {
            int malus = defiCables.calculerMalus();
            partie.retirerTemps(malus);
            InventaireController.ajouterInventaire(root, partie, WindowManager::afficherDefaiteFinale);
            afficherErreurAssistant(malus);
            return;
        }

        if (defiCables.estReussi()) {
            afficherVictoire();
        }
    }

    private CibleCable cibleSousPoint(double x, double y) {
        for (CibleCable cible : ciblesActuelles) {
            boolean dansLaCible = Math.abs(x - cible.x) <= LARGEUR_EMBOUT / 2
                    && Math.abs(y - cible.y) <= HAUTEUR_EMBOUT / 2;
            if (dansLaCible) {
                return cible;
            }
        }
        return null;
    }

    private List<CibleCable> creerCiblesMelangees() {
        List<Double> positionsY = new ArrayList<>();
        for (double y : Y_CABLES) {
            positionsY.add(y);
        }
        Collections.shuffle(positionsY);

        List<CibleCable> cibles = new ArrayList<>();
        cibles.add(new CibleCable("Rouge", Color.web("#d94141"), X_CIBLE_CABLE, positionsY.get(0)));
        cibles.add(new CibleCable("Bleu", Color.web("#2f79d6"), X_CIBLE_CABLE, positionsY.get(1)));
        cibles.add(new CibleCable("Jaune", Color.web("#f0c03a"), X_CIBLE_CABLE, positionsY.get(2)));
        cibles.add(new CibleCable("Vert", Color.web("#55b95b"), X_CIBLE_CABLE, positionsY.get(3)));
        cibles.add(new CibleCable("Violet", Color.web("#a76be8"), X_CIBLE_CABLE, positionsY.get(4)));
        return cibles;
    }

    private void deplacerArrivee(CableGraphique cable, double x, double y) {
        cable.arrivee.setLayoutX(x - LARGEUR_EMBOUT / 2);
        cable.arrivee.setLayoutY(y - HAUTEUR_EMBOUT / 2);
        mettreAJourCourbe(cable.courbe, x, y);
        mettreAJourCourbe(cable.ombre, x, y);
    }

    private void mettreAJourCourbe(CubicCurve courbe, double x, double y) {
        courbe.setEndX(x);
        courbe.setEndY(y);
        courbe.setControlX2(x - 115);
        courbe.setControlY2(y);
    }

    private void afficherErreurAssistant(int malus) {
        if (erreurPane != null) {
            root.getChildren().remove(erreurPane);
        }

        erreurPane = AssistantController.creerErreur(partie.annoncerErreur(malus));
        root.getChildren().add(erreurPane);
        erreurPane.setVisible(true);
        erreurPane.toFront();
    }

    private void afficherVictoire() {
        InventaireController.ajouterInventaire(root, partie, WindowManager::afficherDefaiteFinale);
        AssistantController.ajouterMessage(root, partie.bravo(), "RECUPERER", e -> {
            partie.terminerDefi(defiCables);
            WindowManager.afficherRoom();
        }, 685, 742, 250, 260, 590);

        ImageView cle = creerObjet("key.png", 540, 670, 48);
        ImageView lettreS = creerObjet("lettre_s.png", 610, 680, 38);
        ImageView lettreE = creerObjet("lettre_e_1.png", 655, 680, 38);

        root.getChildren().addAll(cle, lettreS, lettreE);
    }

    private AnchorPane creerFondCables() {
        AnchorPane fond = new AnchorPane();
        fond.setPrefSize(LARGEUR_SCENE, HAUTEUR_SCENE);

        ImageView image = new ImageView(chargerImage("cables.png"));
        image.setFitWidth(LARGEUR_SCENE);
        image.setFitHeight(HAUTEUR_SCENE);
        image.setPreserveRatio(false);

        fond.getChildren().add(image);
        return fond;
    }

    private CubicCurve creerCourbe(double departX, double departY, double arriveeX, double arriveeY, Color couleur, double largeur) {
        CubicCurve courbe = new CubicCurve();
        courbe.setStartX(departX);
        courbe.setStartY(departY);
        courbe.setEndX(arriveeX);
        courbe.setEndY(arriveeY);
        courbe.setControlX1(departX + 115);
        courbe.setControlY1(departY);
        courbe.setControlX2(arriveeX - 115);
        courbe.setControlY2(arriveeY);
        courbe.setStroke(couleur);
        courbe.setStrokeWidth(largeur);
        courbe.setFill(Color.TRANSPARENT);
        courbe.setMouseTransparent(true);
        return courbe;
    }

    private Group creerEmbout(double centreX, double centreY, Color couleur, boolean metalADroite) {
        Group embout = new Group();
        embout.setLayoutX(centreX - LARGEUR_EMBOUT / 2);
        embout.setLayoutY(centreY - HAUTEUR_EMBOUT / 2);

        Rectangle gaine = new Rectangle(0, 0, LARGEUR_EMBOUT, HAUTEUR_EMBOUT);
        gaine.setArcWidth(6);
        gaine.setArcHeight(6);
        gaine.setFill(couleur);
        gaine.setStroke(Color.rgb(0, 0, 0, 0.35));
        gaine.setStrokeWidth(2);

        Rectangle reflet = new Rectangle(6, 4, LARGEUR_EMBOUT - 20, 4);
        reflet.setArcWidth(3);
        reflet.setArcHeight(3);
        reflet.setFill(Color.rgb(255, 255, 255, 0.24));

        embout.getChildren().addAll(gaine, reflet);
        embout.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.55)));
        return embout;
    }

    private double centreX(Group embout) {
        return embout.getLayoutX() + LARGEUR_EMBOUT / 2;
    }

    private double centreY(Group embout) {
        return embout.getLayoutY() + HAUTEUR_EMBOUT / 2;
    }

    private Image chargerImage(String nomFichier) {
        return new Image(getClass().getResource("/com/projetihm/application/images/" + nomFichier).toExternalForm());
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

    private static class CableGraphique {
        private final String couleur;
        private final CubicCurve ombre;
        private final CubicCurve courbe;
        private final Group depart;
        private final Group arrivee;

        private CableGraphique(String couleur, CubicCurve ombre, CubicCurve courbe, Group depart, Group arrivee) {
            this.couleur = couleur;
            this.ombre = ombre;
            this.courbe = courbe;
            this.depart = depart;
            this.arrivee = arrivee;
        }
    }

    private static class CibleCable {
        private final String couleur;
        private final Color peinture;
        private final double x;
        private final double y;

        private CibleCable(String couleur, Color peinture, double x, double y) {
            this.couleur = couleur;
            this.peinture = peinture;
            this.x = x;
            this.y = y;
        }
    }
}
