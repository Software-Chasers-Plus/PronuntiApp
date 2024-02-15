package it.uniba.dib.sms232419.pronuntiapp;

import static android.app.PendingIntent.getActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class GiocoActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gioco);

        // Inizializza il MediaPlayer con il file audio desiderato
        mediaPlayer = MediaPlayer.create(this, R.raw.audio_sottofondo);

        // Avvia la riproduzione
        mediaPlayer.start();

        ImageView backButton = findViewById(R.id.exit_button_start_game);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quando si fa clic sul pulsante di uscita, ferma la riproduzione della musica
                mediaPlayer.stop();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Assicurati di rilasciare le risorse del MediaPlayer quando l'activity viene distrutta
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

