package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class GiocoActivity extends AppCompatActivity {

    public MediaPlayer mediaPlayer;
    public Integer sfondoSelezionato = 0,personaggioSelezionato = 0;
    private Scheda scheda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gioco);

        // Inizializza il MediaPlayer con il file audio desiderato
        mediaPlayer = MediaPlayer.create(this, R.raw.audio_sottofondo);

        // Avvia la riproduzione in loop
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Recupera la scheda selezionata dall'utente
        if(getIntent().getParcelableExtra("scheda") != null) {
            scheda = (Scheda) getIntent().getParcelableExtra("scheda");
            Log.d("GiocoActivity", "Scheda: " + scheda.getNome() + " caricata");
        }


        Bundle bundle = new Bundle();
        bundle.putParcelable("scheda", scheda);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.avvio_gioco_fragment, AvvioGiocoFragment.class, bundle)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Metti in pausa la riproduzione del MediaPlayer quando l'attività entra in pausa
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Riprendi la riproduzione del MediaPlayer se era in pausa quando l'attività è entrata in pausa
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Rilascia il MediaPlayer quando l'attività viene distrutta
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void avviaEsercizio(ArrayList<String> esercizio){
        Bundle bundle = new Bundle();
        bundle.putString("esercizio", esercizio.get(0));
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.avvio_gioco_fragment, EsercizioGiocoFragment.class, bundle)
                .commit();
    }
}
