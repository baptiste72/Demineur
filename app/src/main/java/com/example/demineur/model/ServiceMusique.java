package com.example.demineur.model;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.demineur.R;

import java.util.logging.Handler;

import androidx.annotation.Nullable;

public class ServiceMusique extends Service {

    public ServiceMusique() {
    }

    private static String GAME = "game";
    private static String MENU = "menu";
    private static String SCORE = "score";

    private MediaPlayer player;
    private Handler handler;

    private final MyBinder myBinder = new MyBinder();

    //Implementation de soundService
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyBinder extends Binder {
        ServiceMusique getService(){
            return ServiceMusique.this;
        }
    }
    /**
    * Actions au lancement du service (startService) : player avec musique game
     * @param intent : Intent concernée par le service
     * @param flags
     * @param startId
     * @return
             */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(player == null){
            //On récupère le nom du morceau à jouer dans l'intent
            String morceau = intent.getStringExtra("morceau");
            if (morceau.equals(MENU)) {
                player = MediaPlayer.create(this.getBaseContext(), R.raw.menu);
            } else if (morceau.equals(GAME)) {
                player = MediaPlayer.create(this.getBaseContext(), R.raw.game);
            } else if (morceau.equals(SCORE)) {
                player = MediaPlayer.create(this.getBaseContext(), R.raw.score);
            } else {
                player = MediaPlayer.create(this.getBaseContext(), R.raw.menu);
            }

            //Quand la musique est fini, la relance automatiquement
            player.setLooping(true);

            //Démarre la musique
            player.start();
        }
        //Options pour le service
        return START_NOT_STICKY;
    }    /**
     * Actions à l'arrêt du service (stopService) : arrêt du player
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //quand le service est destroy on stop la musique
        player.stop();
    }
}