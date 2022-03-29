package com.example.demineur;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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

    private ImageView cell_bg, cell_img;
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
        cell_bg = v.findViewById(R.id.cell_bg);
        cell_img = v.findViewById(R.id.cell_img);
        cell_txt = v.findViewById(R.id.cell_txt);
        // Simple = ouvre la grille
        cell_img.setOnClickListener(v1->sendOpen());
        // Long   = Change l'état de la case
        cell_img.setOnLongClickListener(v12 -> sendNextState());
        setPicture();
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

    public int getNearBomb() {
        return this.nearBomb;
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
    public void nextState(){
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
    }

    /**
     * Découvrir la case
     * Si besoin envoyer un message à l'activité principale pour découvrir les cases adjacentes
     * */
    public void open(){
        if(!this.bomb && discover(false)){
            if(this.nearBomb == 0) {
                // si pas une Bombe et qu'il n'y a pas de bombe proche
                // alors ouvre les cases adjacentes
                sendOpen();
            }
        }
    }

    /**
     * Découvrir une case
     * */
    public boolean discover(boolean gameEnd){
        if(this.state == State.UNDISCOVER) {
            setState(State.DISCOVER);
            return true;
        } else if(gameEnd){
            if(this.state == State.QUEST){
                setState(State.DISCOVER);
            } else if(this.state == State.FLAG){
                cell_bg.setImageResource(R.drawable.case1);
                cell_img.setImageResource(R.drawable.bombflaged);
            }
        }
        return false;
    }

    private void sendOpen(){
        // Demander l'ouverture que si la case
        // n'est pas FLAG ou QUEST
        if(getState() != State.FLAG && getState() != State.QUEST){
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent i = new Intent(MainActivity.BROADCAST);
                i.putExtra("method", "open");
                i.putExtra("posX", getPosX());
                i.putExtra("posY", getPosY());
                getActivity().sendBroadcast(i);
            },10);
        }
    }

    private boolean sendNextState(){
        Handler handler = new Handler();
        handler.post(() -> {
            Intent i = new Intent(MainActivity.BROADCAST);
            i.putExtra("method","nextState");
            i.putExtra("posX",getPosX());
            i.putExtra("posY",getPosY());
            getActivity().sendBroadcast(i);
        });
        return true;
    }

    /**
     * Afficher la case tel que son état le permet
     * */
    public void setPicture(){
        switch (this.state){
            case UNDISCOVER:
                cell_bg.setImageResource(R.drawable.case0);
                cell_img.setImageResource(R.drawable.vide);
                cell_txt.setText("");
                break;
            case DISCOVER:
                cell_bg.setImageResource(R.drawable.case1);
                cell_img.setImageResource(R.drawable.vide);
                if(this.bomb){
                    cell_img.setImageResource(R.drawable.bomb);
                } else if (this.nearBomb > 0){
                    cell_txt.setText(""+nearBomb);
                    if(this.nearBomb == 1){
                        cell_txt.setTextColor(getResources().getColor(R.color.blue));
                    } else if(this.nearBomb == 2){
                        cell_txt.setTextColor(getResources().getColor(R.color.darkgreen));
                    } else if(this.nearBomb == 3){
                        cell_txt.setTextColor(getResources().getColor(R.color.red));
                    } else if(this.nearBomb == 4){
                        cell_txt.setTextColor(getResources().getColor(R.color.darkblue));
                    } else if(this.nearBomb == 5){
                        cell_txt.setTextColor(getResources().getColor(R.color.green));
                    } else if(this.nearBomb == 6){
                        cell_txt.setTextColor(getResources().getColor(R.color.darkred));
                    } else {
                        cell_txt.setTextColor(getResources().getColor(R.color.black));
                    }
                }
                break;
            case FLAG:
                cell_bg.setImageResource(R.drawable.case0);
                cell_img.setImageResource(R.drawable.drapeau);
                break;
            case QUEST:
                cell_bg.setImageResource(R.drawable.case0);
                cell_img.setImageResource(R.drawable.vide);
                cell_txt.setText("?");
                cell_txt.setTextColor(getResources().getColor(R.color.black));
                break;
        }
    }

    /**
     * Mettre en évidence la case qui à perdu
     * */
    public void setLooseCase(){
        cell_bg.setImageResource(R.drawable.caser);
        cell_img.setImageResource(R.drawable.bomb);
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