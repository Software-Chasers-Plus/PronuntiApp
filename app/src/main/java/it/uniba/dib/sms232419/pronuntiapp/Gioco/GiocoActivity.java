package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia2;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia3;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class GiocoActivity extends AppCompatActivity {

    private static final int RETRIVE_ESERCIZI_COMPLETATO = 1;
    private static int NUMERO_ESERCIZI;
    private static int NUMERO_ESERCIZI_SCARICATI;
    public MediaPlayer mediaPlayer;
    public Integer sfondoSelezionato = 0,personaggioSelezionato = 0;
    private Scheda scheda;
    private ArrayList<Esercizio> esercizi = new ArrayList<>();
    FirebaseFirestore db;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case RETRIVE_ESERCIZI_COMPLETATO:
                mostraFragmentAvvioGioco();
                break;
            }
        }
    };

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

        retriveEserciziScheda();
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

    private void mostraFragmentAvvioGioco(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("scheda", scheda);
        Log.d("GiocoActivity", "Fragment AvvioGiocoFragment caricato");
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.avvio_gioco_fragment, AvvioGiocoFragment.class, bundle)
                .commit();
    }

    private void retriveEserciziScheda(){
        db = FirebaseFirestore.getInstance();
        NUMERO_ESERCIZI = scheda.getNumeroEsercizi();
        NUMERO_ESERCIZI_SCARICATI = 0;

        for(int i=0; i<NUMERO_ESERCIZI; i++){
            db.collection("esercizi")
                    .document(scheda.getEsercizi().get(i).get(0))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Map<String, Object> esercizio = document.getData();
                                switch (Integer.valueOf(esercizio.get("tipologia").toString())){
                                    case 1:
                                        esercizi.add(new EsercizioTipologia1(esercizio.get("tipologia").toString(),
                                                esercizio.get("nome").toString(),
                                                esercizio.get("logopedista").toString(),
                                                esercizio.get("tipologia").toString(),
                                                esercizio.get("immagine").toString(),
                                                esercizio.get("descrizioneImmagine").toString(),
                                                esercizio.get("audio1").toString(),
                                                esercizio.get("audio2").toString(),
                                                esercizio.get("audio3").toString()));
                                        NUMERO_ESERCIZI_SCARICATI++;
                                        break;
                                    case 2:
                                        esercizi.add(new EsercizioTipologia2(esercizio.get("tipologia").toString(),
                                                esercizio.get("nome").toString(),
                                                esercizio.get("logopedista").toString(),
                                                esercizio.get("tipologia").toString(),
                                                esercizio.get("audio").toString(),
                                                esercizio.get("trascrizione_audio").toString()));
                                        NUMERO_ESERCIZI_SCARICATI++;
                                        break;
                                    case 3:
                                        esercizi.add(new EsercizioTipologia3(esercizio.get("tipologia").toString(),
                                                esercizio.get("nome").toString(),
                                                esercizio.get("logopedista").toString(),
                                                esercizio.get("tipologia").toString(),
                                                esercizio.get("audio").toString(),
                                                esercizio.get("immagine1").toString(),
                                                esercizio.get("immagine2").toString(),
                                                Long.valueOf(esercizio.get("immagine_corretta").toString())));
                                        NUMERO_ESERCIZI_SCARICATI++;
                                        break;
                                }
                                if(NUMERO_ESERCIZI_SCARICATI == NUMERO_ESERCIZI){
                                    mHandler.sendEmptyMessage(RETRIVE_ESERCIZI_COMPLETATO);
                                }
                                Log.d("GiocoActivity", "Esercizio " + esercizi.get(NUMERO_ESERCIZI_SCARICATI-1).getNome() + " scaricato tipologia: " + esercizi.get(NUMERO_ESERCIZI_SCARICATI-1).getTipologia());

                            } else {
                                NUMERO_ESERCIZI_SCARICATI++;
                            }
                        }
                    });
        }
    }

    public void avviaEsercizio(int position){

        switch (Integer.valueOf(esercizi.get(position).getTipologia())){
            case 1:
                EsercizioTipologia1 esercizio1 = (EsercizioTipologia1)esercizi.get(position);
                // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia1 e lo aggiunge al back stack
                Bundle bundle = new Bundle();
                bundle.putParcelable("esercizio", esercizio1);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.avvio_gioco_fragment, EsercizioGiocoFragmentTipologia1.class, bundle)
                        .commit();
                break;
            case 2:
                EsercizioTipologia2 esercizio2 = (EsercizioTipologia2)esercizi.get(position);
                // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia2 e lo aggiunge al back stack
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("esercizio", esercizio2);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.avvio_gioco_fragment, EsercizioGiocoFragmentTipologia2.class, bundle2)
                        .commit();
                break;
            case 3:
                EsercizioTipologia3 esercizio3 = (EsercizioTipologia3)esercizi.get(position);
                // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia3 e lo aggiunge al back stack
                Bundle bundle3 = new Bundle();
                bundle3.putParcelable("esercizio", esercizio3);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.avvio_gioco_fragment, EsercizioGiocoFragmentTipologia3.class, bundle3)
                        .commit();
                break;
        }

    }
}
