package com.example.demineur;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SaveHighScore {

    private static final String NAME_KEY = "MY_PREFS_NAME";
    private static final String GAME_KEY = "GAME_KEY";
    private static final Gson GSON = new Gson();

    private static SharedPreferences.Editor editor;

    private static List<Game> games = new ArrayList<>();

    public static List<Game> getGames() {
        return games;
    }

    private static void setGames(List<Game> games) {
        SaveHighScore.games = games;
    }

    /**
     * Lit les SharedPreferences
     * @param context Contexte depuis lequel la méthode est exécutée
     */
    public static void readSP(Context context) {
        final SharedPreferences PREFS = context.getSharedPreferences(NAME_KEY, MODE_PRIVATE);
        editor = PREFS.edit();

        final String READ_SP = PREFS.getString(GAME_KEY, "");

        if (!READ_SP.isEmpty())
            // Stocke la liste des parties dans la liste games
            setGames(GSON.fromJson(READ_SP, new TypeToken<ArrayList<Game>>(){}.getType()));
    }

    /**
     * Écrit dans les SharedPreferences
     * @param context Contexte depuis lequel la méthode est exécutée
     * @param game Partie à ajouter aux SharedPreferences
     */
    public static void writeSP(Context context, Game game) {
        final String NAME = game.getPlayer().getName();
        final int SCORE = game.getScore();
        final Game.Level LEVEL = game.getLevel();
        boolean playerAlreadyExists = false;

        readSP(context);

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

        editor.putString(GAME_KEY, GSON.toJson(getGames()));
        editor.apply();
    }
}
