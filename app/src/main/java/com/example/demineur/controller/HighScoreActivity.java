package com.example.demineur.controller;

import android.os.Bundle;

import com.example.demineur.model.Game;
import com.example.demineur.R;
import com.example.demineur.model.SaveHighScore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.demineur.R;
import com.example.demineur.model.Game;
import com.example.demineur.model.SaveHighScore;

import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Récupère le nom des joueurs avec les scores associés en fonction du niveau
        SaveHighScore.getInstance().readSP();
        // Affiche les high scores dans des fragments
        printFragments(SaveHighScore.getInstance().getGames());
    }

    private void printFragments(List<Game> games) {
        List<HighScoreFragment> highScoreFragments = new ArrayList<>();

        for (Game game: games) {
            highScoreFragments.add(HighScoreFragment.newInstance(game.getPlayer().getName(), game.getScore()));
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        for (HighScoreFragment highScoreFragment : highScoreFragments) {
            fragmentTransaction.add(R.id.fragment_container, highScoreFragment);
        }

        fragmentTransaction.commit();
    }
}