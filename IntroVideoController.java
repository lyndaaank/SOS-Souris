package com.projetihm.application.Controllers;

import com.projetihm.application.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class IntroVideoController {

    private static final String VIDEO_INTRO = "/com/projetihm/application/images/video_intro.mp4";

    @FXML
    private MediaView videoView;

    private MediaPlayer mediaPlayer;
    private boolean transitionEnCours = false;

    @FXML
    public void initialize() {
        String video = getClass().getResource(VIDEO_INTRO).toExternalForm();

        mediaPlayer = new MediaPlayer(new Media(video));
        mediaPlayer.setOnEndOfMedia(this::continuer);
        videoView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
    }

    @FXML
    public void passerIntro() {
        continuer();
    }

    private void continuer() {
        if (transitionEnCours) {
            return;
        }

        transitionEnCours = true;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        WindowManager.afficherMenu();
    }
}
