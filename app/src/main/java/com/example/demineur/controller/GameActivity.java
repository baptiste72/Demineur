package com.example.demineur.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.demineur.R;
import com.example.demineur.model.Cell;
import com.example.demineur.model.Game;
import com.example.demineur.model.Grid;
import com.example.demineur.model.SaveHighScore;

/**
 * Activité de jeu
 * Suivant le nouveau choisi, on génère une grille avec une taille et un nombre de bombes spécifiques
 */
public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String BROADCAST = "com.cfc.slides.event";

    private TextView levelTextview2, bomb_number, timer;
    private ImageView replay, trophy, sound;
    private TableLayout tlGrid;

    private boolean gameBegin = false;
    private boolean gameEnd = false;
    private boolean soundOn = true;
    private int nLevel = 0; // 1 2 3
    private int nBomb = 0; // 10 25 40 99
    private int nTimer = 0;
    private boolean pauseTimer = false;
    private Grid grille;

    //private ServiceMusique mServiceMusique;
    //Intent n'est pas visible depuis l'exterieur
    private Intent intentService;

    private Game game = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Ces deux lignes permettent de rendre la status bar transparente
        Ceci garantit que notre fond d'écran s'adapte à tout l'écran */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.TYPE_STATUS_BAR);

        setContentView(R.layout.activity_game);

        levelTextview2 = findViewById(R.id.level_textview2);
        bomb_number = findViewById(R.id.bomb_number);
        timer = findViewById(R.id.timer);
        tlGrid = findViewById(R.id.grid);
        replay = findViewById(R.id.replay);
        trophy = findViewById(R.id.trophy);
        sound = findViewById(R.id.sound);

        // Affichage
        setSoundOn(true);
        //Appel de la fonction musique au demarrage
//        startMusicService("game");
        //Debut du chronometre
        timer = findViewById(R.id.timer);
    }

    /**
     * Lancement du service pour la musique de fond
     */
//    protected void startMusicService(String morceau) {
//        Intent intentMusic = new Intent(this, ServiceMusique.class);
//        intentMusic.putExtra("morceau", morceau);
//        startService(intentMusic);
//    }

    private void startTimer(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(!pauseTimer && !gameEnd){
                    nTimer++;
                    setNumber(timer,nTimer);
                }
                if(!gameEnd){
                    // On incrémente toutes les secondes
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,new IntentFilter(BROADCAST));

        replay.setOnClickListener(this);
        trophy.setOnClickListener(this);
        sound.setOnClickListener(this);

//        startService(intentService);
//        stopService(new Intent(this, ServiceMusique.class));

        game = getIntentParams();
        newGame(game);
        setSoundOn(true);
        pauseTimer = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        pauseTimer = true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.replay) {

            newGame(game);
        }

        else if (id == R.id.trophy) {
            Intent highScoreActivity = new Intent(this, HighScoreActivity.class);
            startActivity(highScoreActivity);
        }
        else if (id == R.id.sound)
            setSoundOn();
    }

    private Game getIntentParams() {
        Intent receiveIntent = getIntent();
        Bundle bundle = receiveIntent.getExtras();

        return (Game) bundle.getSerializable(MainActivity.GAME_ACTIVITY_KEY);
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
                            if(!gameBegin){
                                startTimer();
                                gameBegin = true;
                            }
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
    private void gameInit(int largeur, int hauteur, int bomb){
        Toast t = Toast.makeText(this, "Chargement d'une nouvelle grille", Toast.LENGTH_LONG);
        t.show();
        // Init du Timer
        nTimer = 0;
        setNumber(timer, nTimer);
        // Init des bombes
        nBomb = bomb;
        setNumber(bomb_number, nBomb);
        // Effacer l'ancienne grille
        if(grille != null){
            // Lbérer la mémoire
            for(int nCell = grille.getGrille().size()-1; nCell>=0; nCell--){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.remove(grille.getGrille().get(nCell));
                ft.commit();
            }
        }
        // Suppression des lignes existantes dans la vue
        tlGrid.removeAllViews();
        // Création d'une nouvelle grille
        grille = new Grid(largeur, hauteur, bomb);
        int idtr = 8190; // id défini (car par défaut tous à -1)
        for (int x = 0; x < hauteur; x++) {
            // Ajouter une ligne dans la vue
            TableRow tr = new TableRow(this);
            // identifiant différent pour chaque ligne
            idtr++;
            tr.setId(idtr);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tlGrid.addView(tr);
            for (int y = 0; y < largeur; y++) {
                // Ajouter une cellule de la grille(mémoire) dans la ligne de la table(vue)
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(tr.getId(), grille.getCell(x, y));
                ft.commit();
            }
        }
        // Attendre la fin de toutes les transactions
        getSupportFragmentManager().executePendingTransactions();
        t.cancel();
    }

    /**
     * Vérification de la victoire
     * */
    private void checkWin(){
        if(grille.checkWin()){
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
            game.setScore(nTimer);
            SaveHighScore.getInstance().writeSP(game);
        } else {
            // Défaite
        }
    }

    /**
     * Mise à jour de la vue et génération de la grille
     * @param game Contient le nom du joueur et le niveau de difficulté choisi
     */
    private void newGame(Game game){
        if (isNewGame()) {
            final Game.Level LEVEL = game.getLevel();

            switch (LEVEL) {
                case EASY:
                    levelTextview2.setTextColor(Color.GREEN);
                    levelTextview2.setText(R.string.easy);
                    gameInit(8,8,10);
                    break;

                case MEDIUM:
                    levelTextview2.setTextColor(Color.parseColor("#FFA500"));
                    levelTextview2.setText(R.string.medium);
                    gameInit(16,16,40);
                    break;

                case HARD:
                    levelTextview2.setTextColor(Color.RED);
                    levelTextview2.setText(R.string.hard);
                    gameInit(16,32,99);
                    break;
            }
        }
    }

    private boolean isNewGame(){
        if(!gameBegin || gameEnd) {
            gameEnd = false;
            gameBegin = false;
            return true;
        } else {
            Toast.makeText(this,"Veuillez terminer la partie", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void setSoundOn(){
        soundOn = !soundOn;
        setSoundOn(soundOn);
    }

    private void setSoundOn(boolean bool){
        sound.setImageResource(soundOn ? R.drawable.sound : R.drawable.nosound);
    }
}