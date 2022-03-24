package com.example.demineur;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Cell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Cell extends Fragment {

    private String posX, posY;

    private ImageView cell_img;
    private TextView cell_txt;

    protected enum State{
        UNDISCOVER, DISCOVER, FLAG, QUEST
    }
    private State state = State.UNDISCOVER;
    private boolean bomb = false;
    private int nearBomb = 0;

    public Cell() {
        // Required empty public constructor
    }

    /**
     * Constructeur de Cell avec sa position dans la grille
     * @param posX Numéro de la ligne dans la grille
     * @param posY Numéro de la ligne dans la grille
     * */
    public static Cell newInstance(int posX, int posY) {
        Cell fragment = new Cell();
        Bundle args = new Bundle();
        args.putString("POSX", ""+posX);
        args.putString("POSY", ""+posY);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            posX = getArguments().getString("POSX");
            posY = getArguments().getString("POSY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cell, container, false);
        cell_img = v.findViewById(R.id.cell_img);
        // Simple = ouvre la grille
        cell_img.setOnClickListener(v1->open());
        // Long   = Change l'état de la case
        cell_img.setOnLongClickListener(v12 -> nextState());
        cell_txt = v.findViewById(R.id.cell_txt);
        return v;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
        setPicture();
    }

    public int getPosX(){
        return Integer.parseInt(this.posX);
    }

    public int getPosY(){
        return Integer.parseInt(this.posY);
    }

    public boolean getBomb() {
        return this.bomb;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    /**
     * Incrémenter le nombre de bombes adjacentes à la case
     * */
    public void addNearBomb(){
        this.nearBomb++;
    }

    /**
     * Passer à l'état suivant de la case
     * Pas découvert > Drapeau > Questionnement
     * */
    private boolean nextState(){
        switch (this.state){
            // Pas découvert -> Flag
            case UNDISCOVER:
                setState(State.FLAG);
                addNBomb(-1);
                break;
            // Flag -> ?
            case FLAG:
                setState(State.QUEST);
                addNBomb(1);
                break;
            // ? -> Pas découvert
            case QUEST:
                setState(State.UNDISCOVER);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Découvrir la case
     * Si besoin envoyer un message à l'activité principale pour découvrir les cases adjacentes
     * */
    public void open(){
        if(this.state == State.UNDISCOVER){
            setState(State.DISCOVER);
            if(!this.bomb && this.nearBomb == 0) {
                // si pas une Bombe et qu'il n'y a pas de bombe proche
                // alors ouvre les cases adjacentes
                Handler handler = new Handler();
                handler.post(() -> {
                    Intent i = new Intent(MainActivity.BROADCAST);
                    i.putExtra("method","OpenNear");
                    i.putExtra("OpenNearX",getPosX());
                    i.putExtra("OpenNearY",getPosY());
                    getActivity().sendBroadcast(i);
                });
            }
        }
    }

    /**
     * Afficher la case tel que son état le permet
     * */
    public void setPicture(){
        switch (this.state){
            case UNDISCOVER:
                cell_txt.setText("_");
                break;
            case DISCOVER:
                if(this.bomb){
                    cell_txt.setText("X");
                } else if (this.nearBomb == 0){
                    cell_txt.setText(" ");
                } else {
                    cell_txt.setText(""+nearBomb);
                }
                break;
            case FLAG:
                cell_txt.setText("F");
                break;
            case QUEST:
                cell_txt.setText("?");
                break;
        }
    }

    /**
     * Envoyer un message à l'activité princpale pour
     * Incrémenter le nombre de bombes restantes sur le compteur
     * */
    private void addNBomb(int nBomb){
        Handler handler = new Handler();
        handler.post(() -> {
            Intent i = new Intent(MainActivity.BROADCAST);
            i.putExtra("method","addNBomb");
            i.putExtra("addNBomb",nBomb);
            getActivity().sendBroadcast(i);
        });
    }
}