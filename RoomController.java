package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import com.projetihm.application.model.CouleurBlouse;
import com.projetihm.application.model.Joueur;
import com.projetihm.application.model.Partie;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class RoomController {

    private static final String IMAGE_ROOM = "/com/projetihm/application/images/room_fond_VF.png";
    private static final String IMAGE_ROOM_APRES_CABLES = "/com/projetihm/application/images/room_fond_VF1.png";
    private static final String IMAGE_ROOM_APRES_RECHERCHE = "/com/projetihm/application/images/room_fond_VF2.png";
    private static final String IMAGE_ROOM_APRES_LABYRINTHE = "/com/projetihm/application/images/room_fond_VF3.png";
    private static final String IMAGE_PERSONNAGE_DIR = "/com/projetihm/application/images/personnages/";
    private static final String IMAGE_CLE = "/com/projetihm/application/images/key.png";

    //constantes de déplacements (taille joueur, vitesse, collision au niveau des pieds)
    private static final double VITESSE = 225.0;
    private static final double PERSO_FIT_WIDTH = 145.0;
    private static final double PIED_OFFSET_X = 72.5;
    private static final double PIED_OFFSET_Y = 223.0;
    private static final double COLLISION_HALF_WIDTH = 30.0;
    private static final double COLLISION_HEIGHT = 16.0;
    private static final double MIN_X = 45.0;
    private static final double MAX_X = 1485.0;
    private static final double MIN_Y = 315.0;
    private static final double MAX_Y = 542.0;
    private static final double JOUEUR_DEPART_X = 760.0;
    private static final double JOUEUR_DEPART_Y = 510.0;
    private static final double DELTA_MAX = 0.05;
    private static final double VITESSE_ANIMATION_MARCHE = 11.0;

    @FXML
    private AnchorPane roomRoot;
    @FXML
    private ImageView fondRoomImage;
    @FXML
    private ImageView personnageDosImage;
    @FXML
    private Ellipse ombrePersonnage;
    @FXML
    private Rectangle zoneCables;
    @FXML
    private Rectangle zoneRecherche;
    @FXML
    private Rectangle zoneLabyrinthe;
    @FXML
    private Rectangle zoneSortie;
    @FXML
    private Rectangle obstacleTableLabyrinthe;
    @FXML
    private Rectangle obstaclePaillasse;
    @FXML
    private Rectangle obstacleCommode;
    @FXML
    private Rectangle obstaclePorte;
    @FXML
    private Rectangle obstacleInventaire;
    @FXML
    private Rectangle highlightCables;
    @FXML
    private Rectangle highlightRecherche;
    @FXML
    private Rectangle highlightLabyrinthe;
    @FXML
    private Rectangle highlightSortie;
    @FXML
    private Label cadenasCables;
    @FXML
    private Label cadenasRecherche;
    @FXML
    private Label cadenasLabyrinthe;
    @FXML
    private Label cadenasSortie;
    @FXML
    private AnchorPane assistantPane;
    @FXML
    private ImageView assistantImage;
    @FXML
    private ImageView assistantBubbleImage;
    @FXML
    private Label assistantMessage;
    @FXML
    private StackPane questionBubble;
    @FXML
    private ImageView cleInventaire1;
    @FXML
    private ImageView cleInventaire2;
    @FXML
    private ImageView cleInventaire3;
    @FXML
    private Label chronoLabel;

    //variable qui garde en mémoire les touches appuyées
    private final Set<KeyCode> touchesAppuyees = new HashSet<>();

    //on récupére ces données potion, son apparence de dos, son invnentaire
    private Joueur joueur;

    //le chrono, les defi reussi pour la zone accessibles, et la difficulté choisit
    private Partie partie;

    //dans quelle zone on est
    private Partie.ZoneDefi zoneActive = Partie.ZoneDefi.AUCUNE;

    //etat interne de la room pedant le jeu

    //si on garde une fleche appuyée en continue le joueur doit continuer a avancer
    private AnimationTimer animationTimer;

    private AssistantController assistantController;

    //technique pour régler la vitesse de deplacement du joueur d'une facon homogéne
    private long dernierTemps = 0;

    //animer le déplacement du personnage
    private double tempsMarche = 0;

    private String dernierMessageAssistant = "";

    @FXML
    //on démarre tous on récupére le tous joueur, etat partie , images .., et on démarre le chrono
    public void initialize() {
        joueur = WindowManager.getJoueurActuel();
        partie = WindowManager.getPartieActuelle();
        partie.demarrerChrono();

        //on prépare ici l'assistant qu'on va envoyer a son controller
        chargerImages();
        assistantController = new AssistantController(assistantPane, assistantImage, assistantBubbleImage, assistantMessage);
        assistantController.chargerImages();
        placerJoueurDepuisModele();
        masquerToutesLesSurbrillances();
        mettreAJourInventaire();

        roomRoot.setFocusTraversable(true);

        //active les touches clavier
        roomRoot.setOnKeyPressed(this::gererTouchePressee);
        roomRoot.setOnKeyReleased(this::gererToucheRelachee);

        Platform.runLater(() -> {
            roomRoot.requestFocus();
            afficherMessageAssistant(partie.getMessageBienvenue(), false);
        });

        //on lance le jeu ici
        lancerBoucleDeJeu();
    }

    private void chargerImages() {
        //on choisit le fond de l'image en fonction de l'avancement (si defi cables reussi ..)
        Image fond = chargerPremiereImageDisponible(getImageRoomActuelle());
        if (fond != null) {
            fondRoomImage.setImage(fond);
        }

        //on prépare plusieurs chemins possibles d'images de dos du joueur en fonction du choix sinonimage de secours
        Image personnage = chargerPremiereImageDisponible(getImagesPersonnageDos());
        if (personnage != null) {
            personnageDosImage.setImage(personnage);
            personnageDosImage.setFitWidth(PERSO_FIT_WIDTH);
        }

        chargerImageCle(cleInventaire1);
        chargerImageCle(cleInventaire2);
    }


    @FXML
    public void rappelerAssistant() {
        String message = dernierMessageAssistant == null || dernierMessageAssistant.isBlank() ? partie.getMessageBienvenue() : dernierMessageAssistant;
        afficherMessageAssistant(message, true);
        roomRoot.requestFocus();
    }

    private void afficherMessageAssistant(String message, boolean resterVisible) {
        dernierMessageAssistant = message;
        assistantController.afficherMessage(message, resterVisible);
    }

    //ici la methode qui affiche la bonne image du defi en fonction du numero defi
    private String getImageRoomActuelle() {
        if (partie.getEtapeDefi() >= 4) {
            return IMAGE_ROOM_APRES_LABYRINTHE;
        }

        if (partie.getEtapeDefi() >= 3) {
            return IMAGE_ROOM_APRES_RECHERCHE;
        }

        if (partie.getEtapeDefi() >= 2) {
            return IMAGE_ROOM_APRES_CABLES;
        }

        return IMAGE_ROOM;
    }

    private void chargerImageCle(ImageView imageView) {
        Image cle = chargerPremiereImageDisponible(IMAGE_CLE);
        if (cle != null) {
            imageView.setImage(cle);
        }
    }

    private String[] getImagesPersonnageDos() {
        String couleur = getNomCouleurFichier(joueur.getCouleurBlouse());
        String coiffure = joueur.isCheveux() ? "cheveux" : "chauve";

        return new String[]{
                IMAGE_PERSONNAGE_DIR + "personnage_" + couleur + "_dos_" + coiffure + ".png",
                IMAGE_PERSONNAGE_DIR + "personnage_" + couleur + "_dos_chauve.png",
                IMAGE_PERSONNAGE_DIR + "personnage_blanc_dos_chauve.png"
        };
    }

    private String getNomCouleurFichier(CouleurBlouse couleur) {
        return switch (couleur) {
            case BLANCHE -> "blanc";
            case BLEUE -> "bleu";
            case VERTE -> "vert";
            case JAUNE -> "jaune";
        };
    }

    // nouvelle technique d'appeler en continue une methode par javaFX via AnimationTimer
    private void lancerBoucleDeJeu() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long maintenant) {
                if (dernierTemps == 0) {
                    dernierTemps = maintenant;
                    return;
                }

                double delta = (maintenant - dernierTemps) / 1_000_000_000.0;
                dernierTemps = maintenant;
                delta = Math.min(delta, DELTA_MAX);

                //elle met a jour les deplacement et les zones et inventaire
                mettreAJourDeplacement(delta);
                mettreAJourZoneActive();
                mettreAJourInventaire();
            }
        };

        animationTimer.start();
    }

    private void gererTouchePressee(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.ENTER) {
            interagirAvecZone();
            event.consume();
            return;
        }
        if (estToucheDeplacement(code)) {
            touchesAppuyees.add(code);
            event.consume();
        }
    }

    private void gererToucheRelachee(KeyEvent event) {
        touchesAppuyees.remove(event.getCode());
    }

    private boolean estToucheDeplacement(KeyCode code) {
        return code == KeyCode.UP
                || code == KeyCode.DOWN
                || code == KeyCode.LEFT
                || code == KeyCode.RIGHT;
    }

    private void mettreAJourDeplacement(double delta) {
        double dx = 0;
        double dy = 0;

        if (touchesAppuyees.contains(KeyCode.LEFT)) {
            dx -= 1;
        }
        if (touchesAppuyees.contains(KeyCode.RIGHT)) {
            dx += 1;
        }
        if (touchesAppuyees.contains(KeyCode.UP)) {
            dy -= 1;
        }
        if (touchesAppuyees.contains(KeyCode.DOWN)) {
            dy += 1;
        }

        boolean marche = dx != 0 || dy != 0;

        //formule pour eviter que le joueur aille trop vite en diagonale
        if (marche) {
            double longueur = Math.sqrt(dx * dx + dy * dy);
            dx /= longueur;
            dy /= longueur;

            double xActuel = joueur.getPosX();
            double yActuel = joueur.getPosY();

            //on recalcule sa position en limitant la zone autorisée et on verifie les obstacles
            double nouveauX = limiter(xActuel + dx * VITESSE * delta, MIN_X, MAX_X);
            double nouveauY = limiter(yActuel + dy * VITESSE * delta, MIN_Y, MAX_Y);

            // Glissement si la diagonale est bloquée on tente X seul puis Y seul.
            if (!estPositionBloquee(nouveauX, nouveauY)) {
                joueur.deplacer(nouveauX, nouveauY);
            } else if (!estPositionBloquee(nouveauX, yActuel)) {
                joueur.deplacer(nouveauX, yActuel);
            } else if (!estPositionBloquee(xActuel, nouveauY)) {
                joueur.deplacer(xActuel, nouveauY);
            }

            tempsMarche += delta * VITESSE_ANIMATION_MARCHE;
        } else {
            tempsMarche = 0;
        }

        afficherJoueur(marche);
    }

    //au niveau des pieds du joueur on calcule la collisions
    private boolean estPositionBloquee(double x, double y) {
        double centreX = x + PIED_OFFSET_X;
        double piedY = y + PIED_OFFSET_Y;
        double collisionX = centreX - COLLISION_HALF_WIDTH;
        double collisionY = piedY - COLLISION_HEIGHT;
        double collisionWidth = COLLISION_HALF_WIDTH * 2;

        return chevauche(obstacleTableLabyrinthe, collisionX, collisionY, collisionWidth, COLLISION_HEIGHT)
                || chevauche(obstaclePaillasse, collisionX, collisionY, collisionWidth, COLLISION_HEIGHT)
                || chevauche(obstacleCommode, collisionX, collisionY, collisionWidth, COLLISION_HEIGHT)
                || chevauche(obstaclePorte, collisionX, collisionY, collisionWidth, COLLISION_HEIGHT)
                || chevauche(obstacleInventaire, collisionX, collisionY, collisionWidth, COLLISION_HEIGHT);
    }

    private double limiter(double valeur, double min, double max) {
        return Math.max(min, Math.min(max, valeur));
    }

    private void placerJoueurDepuisModele() {
        joueur.deplacer(JOUEUR_DEPART_X, JOUEUR_DEPART_Y);
        afficherJoueur(false);
    }

    private void afficherJoueur(boolean marche) {
        double sautMarche = marche ? Math.sin(tempsMarche) * 1.1 : 0;

        personnageDosImage.setLayoutX(joueur.getPosX());
        personnageDosImage.setLayoutY(joueur.getPosY() + sautMarche);

        ombrePersonnage.setLayoutX(joueur.getPosX() + PIED_OFFSET_X);
        ombrePersonnage.setLayoutY(joueur.getPosY() + PIED_OFFSET_Y + 2);
        ombrePersonnage.setRadiusX(marche ? 53 : 58);
        ombrePersonnage.setRadiusY(marche ? 13 : 15);
    }

    private void mettreAJourInventaire() {
        if (partie.verifierDefaite()) {
            if (animationTimer != null) {
                animationTimer.stop();
            }
            WindowManager.afficherDefaiteFinale();
            return;
        }

        boolean[] cles = joueur.getCles();

        cleInventaire1.setVisible(cles.length > 0 && cles[0]);
        cleInventaire2.setVisible(cles.length > 1 && cles[1]);
        InventaireController.ajouterLettres(roomRoot, partie);
        chronoLabel.setText(InventaireController.formaterTemps(partie.getTempsRestant()));
    }

    private void mettreAJourZoneActive() {
        Partie.ZoneDefi nouvelleZone = detecterZoneDuJoueur();

        if (nouvelleZone == zoneActive) {
            return;
        }

        zoneActive = nouvelleZone;
        mettreAJourSurbrillance(zoneActive);
        if (zoneActive != Partie.ZoneDefi.AUCUNE
                && !partie.estZoneAccessible(zoneActive)
                && !partie.estZoneDejaReussie(zoneActive)) {
            afficherBulleQuestion();
        }

        // Les surbrillances suffisent à indiquer si une zone est disponible
    }

    private Partie.ZoneDefi detecterZoneDuJoueur() {
        double centreX = joueur.getPosX() + PIED_OFFSET_X;
        double piedY = joueur.getPosY() + PIED_OFFSET_Y;

        if (contient(zoneCables, centreX, piedY)) {
            return Partie.ZoneDefi.CABLES;
        }
        if (contient(zoneRecherche, centreX, piedY)) {
            return Partie.ZoneDefi.RECHERCHE;
        }
        if (contient(zoneLabyrinthe, centreX, piedY)) {
            return Partie.ZoneDefi.LABYRINTHE;
        }
        if (contient(zoneSortie, centreX, piedY)) {
            return Partie.ZoneDefi.SORTIE;
        }

        return Partie.ZoneDefi.AUCUNE;
    }

    private boolean contient(Rectangle zone, double x, double y) {
        Bounds bounds = zone.getBoundsInParent();
        return bounds.contains(x, y);
    }

    private boolean chevauche(Rectangle zone, double x, double y, double largeur, double hauteur) {
        Bounds bounds = zone.getBoundsInParent();
        return bounds.intersects(x, y, largeur, hauteur);
    }

    private void mettreAJourSurbrillance(Partie.ZoneDefi zone) {
        masquerToutesLesSurbrillances();

        if (zone == Partie.ZoneDefi.AUCUNE) {
            return;
        }

        Rectangle highlight = getHighlight(zone);
        Label cadenas = getCadenas(zone);

        if (highlight == null) {
            return;
        }

        if (partie.estZoneDejaReussie(zone)) {
            return;
        }

        highlight.getStyleClass().setAll("room-highlight");

        if (partie.estZoneAccessible(zone)) {
            highlight.getStyleClass().add("room-highlight-ok");
        } else {
            highlight.getStyleClass().add("room-highlight-locked");
            if (cadenas != null) {
                cadenas.setVisible(true);
            }
        }

        highlight.setVisible(true);
    }

    private void masquerToutesLesSurbrillances() {
        highlightCables.setVisible(false);
        highlightRecherche.setVisible(false);
        highlightLabyrinthe.setVisible(false);
        highlightSortie.setVisible(false);

        cadenasCables.setVisible(false);
        cadenasRecherche.setVisible(false);
        cadenasLabyrinthe.setVisible(false);
        cadenasSortie.setVisible(false);
    }

    private Rectangle getHighlight(Partie.ZoneDefi zone) {
        return switch (zone) {
            case CABLES -> highlightCables;
            case RECHERCHE -> highlightRecherche;
            case LABYRINTHE -> highlightLabyrinthe;
            case SORTIE -> highlightSortie;
            default -> null;
        };
    }

    private Label getCadenas(Partie.ZoneDefi zone) {
        return switch (zone) {
            case CABLES -> cadenasCables;
            case RECHERCHE -> cadenasRecherche;
            case LABYRINTHE -> cadenasLabyrinthe;
            case SORTIE -> cadenasSortie;
            default -> null;
        };
    }

    private void interagirAvecZone() {
        if (zoneActive == Partie.ZoneDefi.AUCUNE) {
            return;
        }

        if (partie.estZoneDejaReussie(zoneActive)) {
            return;
        }

        if (!partie.estZoneAccessible(zoneActive)) {
            mettreAJourSurbrillance(zoneActive);
            return;
        }

        ouvrirDefi(zoneActive);
    }

    private void afficherBulleQuestion() {
        questionBubble.setLayoutX(joueur.getPosX() + 105);
        questionBubble.setLayoutY(joueur.getPosY() + 14);
        questionBubble.setTranslateY(0);
        questionBubble.setOpacity(1);
        questionBubble.setVisible(true);

        TranslateTransition montee = new TranslateTransition(Duration.millis(260), questionBubble);
        montee.setByY(-12);

        FadeTransition disparition = new FadeTransition(Duration.millis(700), questionBubble);
        disparition.setDelay(Duration.millis(260));
        disparition.setFromValue(1);
        disparition.setToValue(0);
        disparition.setOnFinished(event -> {
            questionBubble.setVisible(false);
            questionBubble.setTranslateY(0);
        });

        montee.play();
        disparition.play();
    }

    private void ouvrirDefi(Partie.ZoneDefi zone) {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        PauseTransition attente = new PauseTransition(Duration.millis(450));
        attente.setOnFinished(event -> {
            partie.utiliserClePourEntrer(zone);
            switch (zone) {
                case CABLES -> WindowManager.afficherCables();
                case RECHERCHE -> WindowManager.afficherRecherche();
                case LABYRINTHE -> WindowManager.afficherLabyrinthe();
                case SORTIE -> WindowManager.afficherDevinette();
                default -> WindowManager.afficherRoom();
            }
        });
        attente.play();
    }

    private Image chargerPremiereImageDisponible(String... chemins) {
        for (String chemin : chemins) {
            InputStream flux = getClass().getResourceAsStream(chemin);
            if (flux != null) {
                return new Image(flux);
            }
        }

        System.err.println("Aucune image trouvée parmi :");
        for (String chemin : chemins) {
            System.err.println(" - " + chemin);
        }
        return null;
    }
}
