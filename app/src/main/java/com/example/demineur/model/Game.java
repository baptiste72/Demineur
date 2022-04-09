package com.example.demineur.model;

/**
 * Classe représentant les paramètres d'une partie
 * Chaque partie possède un joueur
 * Les joueurs obtiennent un score associé à un niveau
 */

public class Game {

    // Le niveau d'une partie peut prendre trois valeurs
    public enum Level {
        EASY, MEDIUM, HARD
    }

    private Player player;
    private int score;
    private Level level;

    /**
     * Constructeur de la classe Game
     * @param player Joueur de la partie
     * @param score Score de la partie
     * @param level Difficulté de la partie
     */
    public Game(Player player, int score, Level level) {
        setPlayer(player);
        setScore(score);
        setLevel(level);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}