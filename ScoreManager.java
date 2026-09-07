package com.projetihm.application.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScoreManager {

    private static final Path FICHIER_SCORES = Path.of("scores.json");
    private static final Pattern ENTREE_SCORE = Pattern.compile("\\{\\s*\"nom\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"difficulte\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"tempsRestant\"\\s*:\\s*(\\d+)\\s*,\\s*\"score\"\\s*:\\s*(\\d+)\\s*,\\s*\"date\"\\s*:\\s*\"(.*?)\"\\s*\\}", Pattern.DOTALL);

    private ScoreManager() {
    }

    public static void enregistrerScore(Score score) {
        List<Score> scores = lireScores();
        scores.add(score);
        scores.sort(Comparator.comparingInt(Score::getScore).reversed());
        ecrireScores(scores);
    }

    public static List<Score> lireMeilleursScores(int limite) {
        List<Score> scores = lireScores();
        scores.sort(Comparator.comparingInt(Score::getScore).reversed());
        return scores.stream().limit(limite).toList();
    }

    public static Path getFichierScores() {
        return FICHIER_SCORES.toAbsolutePath();
    }

    private static List<Score> lireScores() {
        if (!Files.exists(FICHIER_SCORES)) {
            return new ArrayList<>();
        }

        try {
            String contenu = Files.readString(FICHIER_SCORES, StandardCharsets.UTF_8);
            List<Score> scores = new ArrayList<>();
            Matcher matcher = ENTREE_SCORE.matcher(contenu);
            while (matcher.find()) {
                scores.add(new Score(
                        desechapper(matcher.group(1)),
                        Difficulte.valueOf(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(4)),
                        desechapper(matcher.group(5))
                ));
            }
            return scores;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Impossible de lire les scores : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void ecrireScores(List<Score> scores) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            json.append("  {\n")
                    .append("    \"nom\": \"").append(echapper(score.getNomJoueur())).append("\",\n")
                    .append("    \"difficulte\": \"").append(score.getDifficulte().name()).append("\",\n")
                    .append("    \"tempsRestant\": ").append(score.getTempsRestant()).append(",\n")
                    .append("    \"score\": ").append(score.getScore()).append(",\n")
                    .append("    \"date\": \"").append(echapper(score.getDate())).append("\"\n")
                    .append("  }");
            if (i < scores.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");

        try {
            Files.writeString(FICHIER_SCORES, json.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Impossible d'enregistrer les scores : " + e.getMessage());
        }
    }

    private static String echapper(String texte) {
        return texte.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String desechapper(String texte) {
        return texte.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
