package com.example.demineur.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid {

    private int nLargeur;
    private int nHauteur;
    private List<Cell> grille;

    /**
     * Constructeur
     * @param largeur Largeur de la grille
     * @param nbBomb Nombre de bombe à placer
     * */
    public Grid(int largeur, int hauteur, int nbBomb){
        nLargeur = largeur;
        nHauteur = hauteur;
        grille = new ArrayList<>();
        setGrille();
        placeBomb(nbBomb);
    }

    public List<Cell> getGrille(){ return this.grille; }

    /**
     * Pouvoir accéder simplement à une cellule
     * @param ligne Numéro de la ligne de la Grille
     * @param col Numéro de la colonne de la Grille
     * */
    public Cell getCell(int ligne,int col){
        return grille.get(nLargeur*ligne + col);
    }

    /**
     * Ajouter le bon nombre de cellules dans la grille
     * */
    private void setGrille(){
        for(int x = 0; x < nHauteur; x++){
            for(int y = 0; y < nLargeur; y++){
                grille.add(Cell.newInstance(x, y));
            }
        }
    }

    /**
     * Placer les bombes dans la grille
     * @param nBomb Nombre de bombe à placer
     * */
    private void placeBomb(int nBomb){
        int bombToPlace = nBomb;
        while(bombToPlace > 0){
            // Position aléatoire des bombes
            int rx = new Random().nextInt(nHauteur);
            int ry = new Random().nextInt(nLargeur);
            Cell c = getCell(rx,ry);
            // Si la case à déjà une bombe passe sans décrémenter
            if(!c.getBomb()){
                // Ajouter la bombe dans la cellule
                c.setBomb(true);
                // Incrémenter le nombre de bombes proches sur les cases à côté
                addNearBomb(rx,ry);
                // Décrémenter le nombre de bombe à placer
                bombToPlace--;
            }
        }
    }

    /**
     * Incrémenter le nombre de bombes proches sur les cellules adjacentes
     * @param x position de la ligne de la case
     * @param y position de la colonne de la case
     * */
    public void addNearBomb(int x, int y){
        for(int i=x-1; i<=x+1; i++){
            for(int j=y-1; j<=y+1; j++){
                // Cibler que des cases dans la grille
                if(i>=0 && i<nHauteur && j>=0 && j<nLargeur) {
                    getCell(i, j).addNearBomb();
                }
            }
        }
    }

    /**
     * Demmander à une cellule de s'ouvrir
     * @param x position de la ligne de la case
     * @param y position de la colonne de la case
     * */
    public void openCell(int x, int y){
        getCell(x,y).open();
    }

    /**
     * Découvrir les cellules adjcentes
     * @param x position de la ligne de la case
     * @param y position de la colonne de la case
     * */
    public void openNear(int x, int y){
        for(int i=x-1; i<=x+1; i++){
            for(int j=y-1; j<=y+1; j++){
                // Cibler que des cases dans la grille
                if(i>=0 && i<nHauteur && j>=0 && j<nLargeur) {
                    openCell(i,j);
                }
            }
        }
    }

    /**
     * Ouvrir uniquement les cases où il y a des bombes
     * */
    public void openBomb(){
        Cell c;
        for(int x = 0; x < nHauteur; x++){
            for(int y = 0; y < nLargeur; y++){
                c = getCell(x,y);
                if(c.getBomb()){
                    c.discover(true);
                }
            }
        }
    }

    /**
     * Victoire si :
     *  - toutes les Cell sans bombe DISCOVER
     * */
    public boolean checkWin(){
        Cell c;
        for(int x = 0; x < nHauteur; x++){
            for(int y = 0; y < nLargeur; y++){
                c = getCell(x,y);
                if(!c.getBomb() && c.getState() != Cell.State.DISCOVER){
                    // Toutes les cell sans bombe DISCOVER
                    return false;
                }
            }
        }
        // Si victoire affiche des drapeaux sur toutes les bombes
        for(int x = 0; x < nHauteur; x++){
            for(int y = 0; y < nLargeur; y++){
                c = getCell(x,y);
                if(c.getBomb() && c.getState() != Cell.State.FLAG){
                    c.setState(Cell.State.FLAG);
                }
            }
        }
        return true;
    }
}
