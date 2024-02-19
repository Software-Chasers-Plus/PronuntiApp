package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class GiocoActivity extends AppCompatActivity {

    public MediaPlayer mediaPlayer;
    public Integer sfondoSelezionato = 0,personaggioSelezionato = 0;
    private Scheda scheda;
    FirebaseFirestore db;

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
        db = FirebaseFirestore.getInstance();

       // Cerchiamo negli esercizi di quale tipologia è l'esercizio selezionato
        db.collection("esercizi")
                .document(esercizio.get(0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("GiocoActivity", "Esercizio Trovato");
                                Map<String, Object> nuovoEsercizio = document.getData();
                                if(nuovoEsercizio.get("tipologia").toString().equals("1")){
                                    // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia1 e lo aggiunge al back stack
                                    Bundle bundle = new Bundle();
                                    bundle.putString("esercizio", esercizio.get(0));
                                    getSupportFragmentManager().beginTransaction()
                                            .setReorderingAllowed(true)
                                            .replace(R.id.avvio_gioco_fragment, EsercizioGiocoFragmentTipologia1.class, bundle)
                                            .commit();
                                }else if(nuovoEsercizio.get("tipologia").toString().equals("2")){
                                    // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia2 e lo aggiunge al back stack
                                    Bundle bundle = new Bundle();
                                    bundle.putString("esercizio", esercizio.get(0));
                                    getSupportFragmentManager().beginTransaction()
                                            .setReorderingAllowed(true)
                                            .replace(R.id.avvio_gioco_fragment, EsercizioGiocoFragmentTipologia2.class, bundle)
                                            .commit();
                                }else if(nuovoEsercizio.get("tipologia").toString().equals("3")){
                                    // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia3 e lo aggiunge al back stack
                                    Bundle bundle = new Bundle();
                                    bundle.putString("esercizio", esercizio.get(0));
                                    getSupportFragmentManager().beginTransaction()
                                            .setReorderingAllowed(true)
                                            .replace(R.id.avvio_gioco_fragment, EsercizioGiocoFragmentTipologia3.class, bundle)
                                            .commit();
                                }
                            } else {
                                // Stampa nel log un messaggio di errore
                                Log.d("GiocoActivity", "No esercizio con id:" + esercizio.get(0));
                            }
                        } else {
                            Log.d("GiocoActivity", "Task fallito");
                        }
                    }
                });
    }
}
