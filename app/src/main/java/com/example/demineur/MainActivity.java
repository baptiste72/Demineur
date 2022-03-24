package com.example.demineur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Variables xml
    private ImageView replay,trophy,sound;
    private EditText name;
    private Button level;
    private TextView bomb_number,timer;
    private TableLayout tlGrid;

    // Variables
    private boolean gameBegin = false;
    private boolean soundOn = true;
    private int nLevel = 0; // 1 2 3
    private int nBomb = 20; // 20 30 40
    private  int nTimer = 0;
    private Grid grille;
    public static final String BROADCAST = "com.cfc.slides.event";

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
        tlGrid = findViewById(R.id.grid);

        // Affichage
        setNumber(bomb_number,nBomb);
        setSoundOn(true);
        nextlevel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        level.setOnClickListener(v -> nextlevel());
        sound.setOnClickListener(v -> setSoundOn());
        registerReceiver(receiver,new IntentFilter(BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("method")){
                case "OpenNear":
                    grille.openNear(
                            intent.getIntExtra("OpenNearX",0),
                            intent.getIntExtra("OpenNearY",0));
                    break;
                case "addNBomb":
                    addNBomb(intent.getIntExtra("addNBomb",0));
                default:
                    break;
            }

        }
    };

    /**
     * Changer le nombre de bombe affiché dans le compteur
     * @param nBomb nombre de bombe à incrémenter
     * */
    private void addNBomb(int nBomb){
        this.nBomb += nBomb;
        setNumber(bomb_number,this.nBomb);
    }

    /**
     * Changer la difficulté de jeu
     * */
    private void nextlevel(){
        if(!gameBegin){
            nLevel = nLevel %3 +1;
            // Affichage du niveau
            level.setText("LEVEL "+ nLevel);
            // Changer le nombre de bombe
            nBomb = 10+ nLevel *10;
            setNumber(bomb_number,nBomb);
            // Changer la taille de grille
            gridSide(4+4* nLevel); // 8 12 16
        }
    }

    /**
     * Afficher un nombre sous le format 000
     * @param tv TextView à modifier
     * @param x nombre à afficher
     * */
    private void setNumber(TextView tv, int x){
        String txt = ""+x;
        if(x < 10){
            txt = "00"+txt;
        } else if(x < 100){
            txt = "0"+txt;
        }
        tv.setText(txt);
    }

    /**
     * Changer la largeur de la grille
     * @param largeur Largeur de la grille à créer
     * */
    private void gridSide(int largeur){
        // Suppression des lignes existantes dans la vue
        tlGrid.removeAllViews();
        // Création d'une nouvelle grille
        grille = new Grid(largeur,nBomb);
        int idtr = 8190; // id défini (car par défaut tous à -1)
        for(int x = 0; x < largeur; x++){
            // Ajouter une ligne dans la vue
            TableRow tr = new TableRow(this);
            // identifiant différent pour chaque ligne
            idtr++; tr.setId(idtr);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
            tlGrid.addView(tr);
            for(int y = 0; y < largeur; y++) {
                // Ajouter une cellule de la grille(mémoire) dans la ligne de la table(vue)
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(tr.getId(),grille.getCell(x,y));
                ft.commit();
            }
        }
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