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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Variables xml
    private ImageView replay,trophy,sound;
    private EditText name;
    private Button level;
    private TextView bomb_number,timer;
    private TableLayout tlGrid;

    // Variables
    private boolean gameBegin = false;
    private boolean gameEnd = false;
    private boolean soundOn = true;
    private int nLevel = 0; // 1 2 3
    private int nBomb = 0; // 10 25 40
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
            // Ne pouvoir faire des actions
            // que lorsque la game n'est pas terminée
            if(!gameEnd) {
                int posX = intent.getIntExtra("posX", 0);
                int posY = intent.getIntExtra("posY", 0);
                Cell c = grille.getCell(posX, posY);
                switch (intent.getStringExtra("method")) {
                    case "open":
                        grille.openCell(posX, posY);
                        if (c.getBomb()) {
                            // Afficher toutes les bombes
                            grille.openBomb();
                            // Fond rouge pour la case qui a perdu
                            c.setLooseCase();
                            //  Dire que la game est perdu
                            gameWin(false);
                        } else {
                            gameBegin = true;
                            // S'il n'y a pas de bombes adjacentes
                            if(c.getNearBomb() == 0){
                                // Ouvre les cases adjacentes
                                grille.openNear(posX, posY);
                            }
                            // Vérification de la victoire
                            checkWin();
                        }
                        break;
                    case "nextState":
                        c.nextState();
                        break;
                    case "addNBomb":
                        int addNBomb = intent.getIntExtra("addNBomb", 0);
                        addNBomb(addNBomb);
                        // Vérification de la victoire
                        checkWin();
                    default:
                        break;
                }
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
        if(!gameBegin || gameEnd){
            gameEnd = false;
            gameBegin = false;
            nLevel = nLevel %3 +1;
            // Affichage du niveau
            level.setText("LEVEL "+ nLevel);
            // Changer le nombre de bombe
            nBomb = 10+ ((nLevel-1) *15);
            setNumber(bomb_number,nBomb);
            // Changer le timer
            nTimer = 0;
            setNumber(timer,nTimer);
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
        String txt = ""+Math.abs(x);
        if(x <= -10){
            txt = "-"+txt;
        } else if (x < 0){
            txt = "-0"+txt;
        } else if(x < 10){
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

    /**
     * Vérification de la victoire
     * */
    private void checkWin(){
        if(grille.checkWin(nBomb)){
            gameWin(true);
        }
    }

    /**
     * Traitement de la fin du jeu
     * @param win Est-ce que le joueur à gagné ?
     * */
    private void gameWin(boolean win){
        // Fin de la game
        gameEnd = true;
        if(win){
            // Ajouter la victoire
            Toast.makeText(this,"Victoire !",Toast.LENGTH_SHORT).show();
        } else {
            // Défaite
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