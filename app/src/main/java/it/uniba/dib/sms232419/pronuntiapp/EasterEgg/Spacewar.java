package it.uniba.dib.sms232419.pronuntiapp.EasterEgg;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.LoginFragment;
import it.uniba.dib.sms232419.pronuntiapp.R;

public class Spacewar extends AppCompatActivity implements View.OnTouchListener {
    private boolean misPlaying = true; // Stato del suono: true se è in riproduzione, altrimenti false
    private MusicService mServ = new MusicService(); // Servizio musicale
    int score = Spaceship.score; // Punteggio iniziale dell'astronave
    MediaPlayer pewPlayer; // Lettore per il suono dello sparo
    MediaPlayer boomPlayer; // Lettore per il suono dell'esplosione
    int oldScore = 0; // Vecchio punteggio
    int btn = 0; // Pulsante premuto (1=destro, 2=sinistro, 3=su, 4=giù)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.easter_egg);
        Button up = (Button) findViewById(R.id.upBtn);
        up.setOnTouchListener(this); // Attiva il controllo del tocco sul pulsante su
        Button down = (Button) findViewById(R.id.downBtn);
        down.setOnTouchListener(this); // Attiva il controllo del tocco sul pulsante giù
        Button right = (Button) findViewById(R.id.rightBtn);
        right.setOnTouchListener(this); // Attiva il controllo del tocco sul pulsante destro
        Button left = (Button) findViewById(R.id.leftBtn);
        left.setOnTouchListener(this); // Attiva il controllo del tocco sul pulsante sinistro
        Button fire = (Button) findViewById(R.id.fireBtn);
        fire.setOnTouchListener(this); // Attiva il controllo del tocco sul pulsante di fuoco

        // Avvia i thread
        t.start();
        drawIt.start();
    }



    // Aggiorna il canvas, controlla se ci sono aggiornamenti del punteggio e controlla se sta riproducendo il suono dell'esplosione
    Thread drawIt = new Thread() {
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Thread.sleep(30); // FPS
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Aggiorna il canvas
                            View v = findViewById(R.id.table);
                            v.postInvalidate();
                            // Aggiorna il punteggio
                            updateScore();
                            // Riproduce il suono dell'esplosione se necessario
                            if (Spaceship.boomPlay){boom();};
                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };


    // Controlla i pulsanti premuti
    Thread t = new Thread() {
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Thread.sleep(30);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Controlla i pulsanti premuti e aggiorna le coordinate dell'astronave
                            switch (btn) {
                                case 1:
                                    Spaceship.cx += Spaceship.vl;
                                    Spaceship.dr = Spaceship.Direction.EAST;
                                    break;
                                case 2:
                                    Spaceship.cx -= Spaceship.vl;
                                    Spaceship.dr = Spaceship.Direction.WEST;
                                    break;
                                case 3:
                                    Spaceship.cy -= Spaceship.vl;
                                    Spaceship.dr = Spaceship.Direction.NORTH;
                                    break;
                                case 4:
                                    Spaceship.cy += Spaceship.vl;
                                    Spaceship.dr = Spaceship.Direction.SOUTH;
                                    break;
                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };


    // Gestisce il tocco sugli elementi della UI
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            // Gestisce l'evento di tocco per ogni pulsante
            if (v.getId() == R.id.rightBtn) {
                Log.d("Action", "Right button");
                btn = 1;
            } else if (v.getId() == R.id.leftBtn) {
                Log.d("Action", "Left button");
                btn = 2;
            } else if (v.getId() == R.id.upBtn) {
                Log.d("Action", "Up button");
                btn = 3;
            } else if (v.getId() == R.id.downBtn) {
                Log.d("Action", "Down button");
                btn = 4;
            } else if (v.getId() == R.id.fireBtn) {
                Log.d("Action", "Fire button");
                pewPew(); // Riproduce il suono dello sparo
                Spaceship.dahFlag = true; // Imposta il flag del suono di sparatoria su true
            }
        } else {
            // Gestisce il caso in cui nessun pulsante è premuto
            btn = 0;
        }
        return false;
    }


    // Aggiorna il punteggio nella textview
    public void updateScore() {
        score = Spaceship.score;
        if (score - oldScore >= 0 | score==0){
            TextView tv1 = (TextView) findViewById(R.id.score);
            String thescore = Integer.toString(score);
            tv1.setText(thescore);
            oldScore = score;
        }
    }

    // Interfaccia per la connessione al servizio musicale
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };


    @Override
    protected void onPause() {
        // Mette in pausa il suono
        super.onPause();
        if (misPlaying){mServ.pauseMusic(); misPlaying=false;}
        score = 0;
        finishActivity(0);
    }

    @Override
    protected void onResume() {
        // Riproduce il suono di background
        super.onResume();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        startService(music);
        if (!misPlaying){mServ.resumeMusic(); misPlaying=true;}
    }

    // Riproduce il suono dello sparo
    void pewPew(){
        if(pewPlayer!=null){
            pewPlayer.release();
            pewPlayer=null;
        }
        pewPlayer = MediaPlayer.create(this,R.raw.lasershoot);
        pewPlayer.start();
    }

    // Riproduce il suono dell'esplosione
    void boom(){
        if(boomPlayer!=null){
            boomPlayer.release();
            boomPlayer=null;
        }
        boomPlayer = MediaPlayer.create(this, R.raw.boom);
        boomPlayer.start();
        Spaceship.boomPlay = false;
    }

}
