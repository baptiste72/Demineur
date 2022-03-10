package com.example.demineur;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Variables xml
    private ImageView replay,trophy,sound;
    private EditText name;
    private Button level;
    private TextView bomb_number,timer;

    // Variables
    private boolean gameBegin = false;
    private boolean soundOn = true;
    private int nLevel = 1; // 1 2 3
    private int nBomb = 20; // 20 30 40
    private  int nTimer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get
        replay = findViewById(R.id.replay);
        trophy = findViewById(R.id.trophy);
        sound = findViewById(R.id.sound);
        name = findViewById(R.id.name);
        level = findViewById(R.id.level);
        bomb_number = findViewById(R.id.bomb_number);
        timer = findViewById(R.id.timer);

        // Affichage
        setNumber(bomb_number,nBomb);
        setSoundOn(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        level.setOnClickListener(v -> nextlevel());
        sound.setOnClickListener(v -> setSoundOn());
    }

    private void nextlevel(){
        if(!gameBegin){
            nLevel = nLevel %3 +1;
            // Affichage du niveau
            level.setText("LEVEL "+ nLevel);
            // Changer la taille de la grille / nombre de bombe
            gridSide(4+4* nLevel); // 8 12 16
            nBomb = 10+ nLevel *10;
            setNumber(bomb_number,nBomb);
        }
    }

    // Afficher les compteurs avec des 0 devant
    private void setNumber(TextView tv, int x){
        String txt = ""+x;
        if(x < 10){
            txt = "00"+txt;
        } else if(x < 100){
            txt = "0"+txt;
        }
        tv.setText(txt);
    }

    private void gridSide(int x){

    }

    private void setSoundOn(){
        soundOn = !soundOn;
        setSoundOn(soundOn);
    }

    private void setSoundOn(boolean bool){
        sound.setImageResource(soundOn ? R.drawable.sound : R.drawable.nosound);
        // Ajouter start and stop du service

    }
}