package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia2;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class GiocoActivity extends AppCompatActivity {

    public MediaPlayer mediaPlayer;
    public Integer sfondoSelezionato = 0,personaggioSelezionato = 0;
    public Scheda scheda;
    public Figlio figlio;
    FirebaseFirestore db;
    AlertDialog dialogPopupCaricamentoEsercizio;

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

            figlio = (Figlio) getIntent().getParcelableExtra("figlio");
            Log.d("GiocoActivity", "Figlio: " + figlio.getNome() + " caricato");
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

        Bundle bundle = new Bundle();

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
                                    mostraPopupCaricamentoImmagine();
                                    Log.d("PopUp_Fragmnet", "PopUp Fragmnet visulizzato");
                                    Log.d("PopUp_Fragmnet", "Popup: " + dialogPopupCaricamentoEsercizio);
                                    // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia1 e lo aggiunge al back stack
                                    EsercizioTipologia1 esercizioTipologia1 = new EsercizioTipologia1(document.getId(),
                                            nuovoEsercizio.get("nome").toString(),
                                            nuovoEsercizio.get("logopedista").toString(),
                                            nuovoEsercizio.get("tipologia").toString(),
                                            nuovoEsercizio.get("immagine").toString(),
                                            nuovoEsercizio.get("descrizioneImmagine").toString(),
                                            nuovoEsercizio.get("audio1").toString(),
                                            nuovoEsercizio.get("audio2").toString(),
                                            nuovoEsercizio.get("audio3").toString());
                                    bundle.putParcelable("esercizio", esercizioTipologia1);
                                    eseguiDownloadImmagine(nuovoEsercizio.get("immagine").toString(), bundle);
                                }else if(nuovoEsercizio.get("tipologia").toString().equals("2")){
                                    mostraPopupCaricamentoImmagine();
                                    Log.d("PopUp_Fragmnet", "PopUp Fragmnet visulizzato");
                                    Log.d("PopUp_Fragmnet", "Popup: " + dialogPopupCaricamentoEsercizio);
                                    // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia2 e lo aggiunge al back stack
                                    EsercizioTipologia2 esercizioTipologia2 = new EsercizioTipologia2(nuovoEsercizio.get("tipologia").toString(),
                                            nuovoEsercizio.get("nome").toString(),
                                            nuovoEsercizio.get("logopedista").toString(),
                                            nuovoEsercizio.get("tipologia").toString(),
                                            nuovoEsercizio.get("audio").toString(),
                                            nuovoEsercizio.get("trascrizione_audio").toString());
                                    bundle.putParcelable("esercizio", esercizioTipologia2);
                                    aggiungiFragmentGioco(EsercizioGiocoFragmentTipologia2.class, bundle);
                                }else if(nuovoEsercizio.get("tipologia").toString().equals("3")){
                                    // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia3 e lo aggiunge al back stack
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

    private void aggiungiFragmentGioco(Class fragmentClass, Bundle bundle){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.avvio_gioco_fragment, fragmentClass, bundle)
                .commit();
    }

    private void eseguiDownloadImmagine(String pathImmagine, Bundle bundle){

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(pathImmagine);

        // Download directly from StorageReference using Glide
        final long MAX_DOWNLOAD_SIZE = 2048 * 2048; // 1MB max download size
        storageRef.getBytes(MAX_DOWNLOAD_SIZE).addOnSuccessListener(bytes -> {
            // Decode the byte array into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            // Set the Bitmap to the ImageView
            BitmapCache bitmapCache = new BitmapCache();
            bitmapCache.addBitmapToMemoryCache("immagine", bitmap);

            aggiungiFragmentGioco(EsercizioGiocoFragmentTipologia1.class, bundle);
        });
    }

    private void mostraPopupCaricamentoImmagine(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_caricamento_esercizio, null);
        int coloreSfondo = 0;
        switch (this.sfondoSelezionato) {
            case 0:
                coloreSfondo = R.color.secondaryDeserto;
                break;
            case 1:
                coloreSfondo = R.color.secondaryAntartide;
                break;
            case 2:
                coloreSfondo = R.color.secondaryGiungla;
                break;
        }
        view.findViewById(R.id.card_view_caricamento_esercizio)
                .setBackgroundColor(getResources().getColor(coloreSfondo));

        int coloreTesto = 0;
        switch (this.personaggioSelezionato) {
            case 0:
                coloreTesto = R.color.primaryDeserto;
                break;
            case 1:
                coloreTesto = R.color.primaryAntartide;
                break;
            case 2:
                coloreTesto = R.color.primaryGiungla;
                break;
        }
        TextView testo = view.findViewById(R.id.txt_popup_caricamento_esercizio);
        testo.setTextColor(getResources().getColor(coloreTesto));

        CircularProgressIndicator progressBar = view.findViewById(R.id.progressIndicator_caricamento_esercizio);
        progressBar.setIndicatorColor(getResources().getColor(coloreTesto));

        dialogPopupCaricamentoEsercizio = builder.create();
        dialogPopupCaricamentoEsercizio.setView(view);
        dialogPopupCaricamentoEsercizio.show();
    }
}