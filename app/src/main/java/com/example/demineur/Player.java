package com.example.demineur;

/**
 * Classe représentant un joueur
 * Les joueurs possèdent un nom
 */

public class Player {

    private String name;

    /**
     * Constructeur de la classe Joueur
     * @param name Le nom du joueur
     */
    public Player(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}