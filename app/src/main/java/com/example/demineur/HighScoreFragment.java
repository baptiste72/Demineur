package com.example.demineur;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * Classe représentant les données reçues par le fragment HighScoreFragment
 * On récupère le nom du joueur et son score en paramètres du fragment
 */

public class HighScoreFragment extends Fragment {

    public static final String NAME_KEY = "NAME";
    public static final String SCORE_KEY = "SCORE";

    private TextView f_name;
    private TextView f_score;
    private String name;
    private int score;

    public HighScoreFragment() {
    }

    /**
     * Créer une nouvelle instance avec le nom et le score du joueur
     * @param name Nom du joueur
     * @param score Score du joueur
     * @return fragment Retourne le fragment
     */
    public static HighScoreFragment newInstance(String name, int score) {
        HighScoreFragment fragment = new HighScoreFragment();
        Bundle args = new Bundle();
        args.putString(NAME_KEY, name);
        args.putInt(SCORE_KEY, score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // S'il y a des arguments
        if (getArguments() != null) {
            name = getArguments().getString(NAME_KEY);
            score = getArguments().getInt(SCORE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_high_score, container, false);
        f_name = view.findViewById(R.id.f_name);
        f_name.setText(name);
        f_score = view.findViewById(R.id.f_score);
        f_score.setText(String.valueOf(score));
        return view;
    }
}