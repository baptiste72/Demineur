package com.example.demineur.model;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de lire et d'enregistrer les résultats des parties dans les SharedPreferences
 * Celle-ci doit être accessible et partagée par plusieurs classes de l'application
 * Il est donc intéressant qu'elle prenne la forme d'un singleton
 */

public class SaveHighScore {

    private static final String SP_KEY = "SAVE_HIGH_SCORE";
    private static final String GAME_KEY = "GAME_KEY";
    private static final Gson GSON = new Gson();

    private static SaveHighScore INSTANCE;

    private final SharedPreferences PREFERENCES;

    private List<Game> games = new ArrayList<>();

    private SaveHighScore() {
        PREFERENCES = ExampleApplication.getContext().getSharedPreferences(SP_KEY, MODE_PRIVATE);
    }

    public static SaveHighScore getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SaveHighScore();

        return INSTANCE;
    }

    public List<Game> getGames() {
        return games;
    }

    private void setGames(List<Game> games) {
        this.games = games;
    }

    /**
     * Lit les SharedPreferences
     */
    public void readSP() {
        final String READ_SP = PREFERENCES.getString(GAME_KEY, null);

        if (READ_SP != null)
            // Stocke la liste des parties dans la liste games
            setGames(GSON.fromJson(READ_SP, new TypeToken<ArrayList<Game>>(){}.getType()));
    }

    /**
     * Écrit dans les SharedPreferences
     * @param game Partie à ajouter aux SharedPreferences
     */
    public void writeSP(Game game) {
        final String NAME = game.getPlayer().getName();
        final int SCORE = game.getScore();
        final Game.Level LEVEL = game.getLevel();
        boolean playerAlreadyExists = false;

        // On récupère la liste des parties enregistrée dans les SharedPreferences
        readSP();

        // On parcourt la liste des parties enregistrées
        for (Game currentGame : getGames()) {
            // Si le joueur existe et qu'il a déjà joué ce niveau
            if (currentGame.getPlayer().getName().equals(NAME) && currentGame.getLevel() == LEVEL) {
                // Si son nouveau score est meilleur que le précédent, on le met à jour
                if (currentGame.getScore() > SCORE)
                    currentGame.setScore(SCORE);

                playerAlreadyExists = true;
                break;
            }
        }

        // Si le joueur n'a jamais joué
        if (!playerAlreadyExists)
            getGames().add(game);

        PREFERENCES.edit().putString(GAME_KEY, GSON.toJson(getGames())).apply();
    }
}
