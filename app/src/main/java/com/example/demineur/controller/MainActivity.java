package com.example.demineur.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demineur.R;
import com.example.demineur.model.Game;
import com.example.demineur.model.Player;

/**
 * Projet réalisé par Gabin Caudan / Baptiste Chauvelier / Constantin Chtanko
 */

/**
 * Activité principale démarée au lancement de l'application
 * Elle permet de renseigner le nom d'un nouveau joueur et de choisir un niveau de difficulté
 */
public class MainActivity extends AppCompatActivity {

    private static final String GAME_ACTIVITY_KEY = "GAME_ACTIVITY_KEY";

    private EditText name;
    private SeekBar levelSeekbar;
    private TextView levelTextview;
    private Button newPartie;

    private Game.Level level = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Ces deux lignes permettent de rendre la status bar transparente
        Ceci garantit que notre fond d'écran s'adapte à tout l'écran */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.TYPE_STATUS_BAR);

        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        levelSeekbar = findViewById(R.id.level_seekbar);
        levelTextview = findViewById(R.id.level_textview);
        newPartie = findViewById(R.id.new_game);
    }

    @Override
    protected void onResume() {
        super.onResume();

        enabledButton();
        setLevelTextview();
        launchGame();
    }

    /**
     * Le bouton pour lancer la partie est par défaut désactivé
     * Il ne s'active que lorsqu'un pseudo d'au moins quatre caractères est renseigné
     */
    private void enabledButton() {
        newPartie.setEnabled(false);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newPartie.setEnabled(s.toString().trim().length() >= 4);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * On détermine le niveau de difficulté en fonction de la progression de la seekbar
     */
    private void setLevelTextview() {
        levelSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    level = Game.Level.EASY;
                    levelTextview.setText(R.string.easy);
                } else if (progress == 1) {
                    level = Game.Level.MEDIUM;
                    levelTextview.setText(R.string.medium);
                } else if (progress == 2) {
                    level = Game.Level.HARD;
                    levelTextview.setText(R.string.hard);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Lorsque le bouton est cliqué on lance l'activité GameAcitivity
     */
    private void launchGame() {
        newPartie.setOnClickListener(v -> startGameActivity());
    }

    /**
     * On définit les paramètres de l'objet Game à passer à l'activité GameActivity
     * Cet objet récupère le pseudo définit dans l'EditText et le niveau de difficulté
     * Il est ensuite passé dans l'intent pour pouvoir être utilisé dans l'activité GameActivity
     */
    private void startGameActivity() {
        final Game GAME = new Game(new Player(name.getText().toString().trim()), level);
        Intent gameActivity = new Intent(this, GameActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable(GAME_ACTIVITY_KEY, GAME);
        gameActivity.putExtras(bundle);
        startActivity(gameActivity);
    }
}