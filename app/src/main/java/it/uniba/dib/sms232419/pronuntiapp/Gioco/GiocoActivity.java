package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia2;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia3;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class GiocoActivity extends AppCompatActivity {

    public MediaPlayer mediaPlayer;
    public Integer sfondoSelezionato, personaggioSelezionato;
    public Scheda scheda;
    public Figlio figlio;
    private static final int FETCH_TERMINATO = 1;
    private static int NUMERO_IMMAGINI;
    private static int NUMERO_IMMAGINI_SCARICATI;
    FirebaseFirestore db;
    AlertDialog dialogPopupCaricamentoEsercizio;

    Map<String, Object> nuovoEsercizio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gioco);

        Log.d("GiocoActivityInizio", "Activity avviata");

        // Inizializza il MediaPlayer con il file audio desiderato
        mediaPlayer = MediaPlayer.create(this, R.raw.audio_sottofondo);

        // Avvia la riproduzione in loop
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Recupera la scheda selezionata dall'utente
        if(getIntent().getParcelableExtra("scheda") != null) {
            scheda = getIntent().getParcelableExtra("scheda");
            Log.d("GiocoActivity", "Scheda: " + scheda.getNome() + " caricata");

            figlio = getIntent().getParcelableExtra("figlio");
            Log.d("GiocoActivity", "Figlio: " + figlio.getNome() + " caricato");
            //Assegniamo i valori di sfondo e personaggio selezionato recuperandoli da firebase
            db = FirebaseFirestore.getInstance();
            db.collection("figli")
                    .whereEqualTo("codiceFiscale", figlio.getCodiceFiscale())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                sfondoSelezionato = Integer.parseInt(Objects.requireNonNull(document.get("sfondoSelezionato")).toString());
                                personaggioSelezionato = Integer.parseInt(Objects.requireNonNull(document.get("personaggioSelezionato")).toString());
                            }
                        } else {
                            Log.d("GiocoActivity", "Errore nel recupero del figlio");
                        }
                    });
        }


        Bundle bundle = new Bundle();
        bundle.putParcelable("scheda", scheda);
        bundle.putParcelable("figlio", figlio);
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
                                nuovoEsercizio = document.getData();
                                if(nuovoEsercizio.get("tipologia").toString().equals("1")){
                                    mostraPopupCaricamentoImmagine();
                                    Log.d("PopUp_Fragmnet", "PopUp1 Fragmnet visulizzato");
                                    Log.d("PopUp_Fragmnet", "Popup1: " + dialogPopupCaricamentoEsercizio);
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

                                    NUMERO_IMMAGINI = 1;
                                    NUMERO_IMMAGINI_SCARICATI = 0;

                                    ArrayList<String> key = new ArrayList<>();
                                    key.add("immagine");

                                    ArrayList<String> immagini = new ArrayList<>();
                                    immagini.add(nuovoEsercizio.get("immagine").toString());

                                    Log.d("GiocoActivityAvvioEsercizio", "Avvio download immagini per esercizio tipologia 1");
                                    eseguiDownloadImmagine(immagini, key, bundle, EsercizioGiocoFragmentTipologia1.class);
                                }else if(nuovoEsercizio.get("tipologia").toString().equals("2")){
                                    mostraPopupCaricamentoImmagine();
                                    Log.d("PopUp_Fragmnet", "PopUp2 Fragmnet visulizzato");
                                    Log.d("PopUp_Fragmnet", "Popup2: " + dialogPopupCaricamentoEsercizio);
                                    // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia2 e lo aggiunge al back stack
                                    EsercizioTipologia2 esercizioTipologia2 = new EsercizioTipologia2(document.getId(),
                                            nuovoEsercizio.get("nome").toString(),
                                            nuovoEsercizio.get("logopedista").toString(),
                                            nuovoEsercizio.get("tipologia").toString(),
                                            nuovoEsercizio.get("audio").toString(),
                                            nuovoEsercizio.get("trascrizione_audio").toString());
                                    bundle.putParcelable("esercizio", esercizioTipologia2);
                                    aggiungiFragmentGioco(EsercizioGiocoFragmentTipologia2.class, bundle);
                                }else if(nuovoEsercizio.get("tipologia").toString().equals("3")){
                                    mostraPopupCaricamentoImmagine();
                                    Log.d("PopUp_Fragmnet", "PopUp3 Fragmnet visulizzato");
                                    Log.d("PopUp_Fragmnet", "Popup3: " + dialogPopupCaricamentoEsercizio);
                                    // Avvia la transizione al fragment EsercizioGiocoFragmentTipologia1 e lo aggiunge al back stack
                                    EsercizioTipologia3 esercizioTipologia3 = new EsercizioTipologia3(document.getId(),
                                            nuovoEsercizio.get("nome").toString(),
                                            nuovoEsercizio.get("logopedista").toString(),
                                            nuovoEsercizio.get("tipologia").toString(),
                                            nuovoEsercizio.get("audio").toString(),
                                            nuovoEsercizio.get("immagine1").toString(),
                                            nuovoEsercizio.get("immagine2").toString(),
                                            (long)nuovoEsercizio.get("immagine_corretta"));
                                    bundle.putParcelable("esercizio3", esercizioTipologia3);

                                    NUMERO_IMMAGINI = 2;
                                    NUMERO_IMMAGINI_SCARICATI = 0;

                                    ArrayList<String> key = new ArrayList<>();
                                    key.add("immagine1");
                                    key.add("immagine2");

                                    ArrayList<String> immagini = new ArrayList<>();
                                    immagini.add(nuovoEsercizio.get("immagine1").toString());
                                    immagini.add(nuovoEsercizio.get("immagine2").toString());

                                    Log.d("GiocoActivityAvvioEsercizio", "Avvio download immagini per esercizio tipologia 3");
                                    eseguiDownloadImmagine(immagini, key, bundle, EsercizioGiocoFragmentTipologia3.class);
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

    private void eseguiDownloadImmagine(ArrayList<String> pathImmagine, ArrayList<String> key, Bundle bundle_esercizio, Class fragmentClass) {
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(pathImmagine.get(NUMERO_IMMAGINI_SCARICATI));

        // Download directly from StorageReference using Glide
        final long MAX_DOWNLOAD_SIZE = 2048 * 2048; // 1MB max download size
        storageRef.getBytes(MAX_DOWNLOAD_SIZE)
                .addOnSuccessListener(bytes -> {
                    Log.d("GiocoActivityDownload", "NUMERO_IMMAGINI_SCARICATI: " + NUMERO_IMMAGINI_SCARICATI);
                    Log.d("GiocoActivityDownload", pathImmagine.get(NUMERO_IMMAGINI_SCARICATI) + " scaricata");
                    // Decode the byte array into a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    // Add the bitmap to the cache
                    BitmapCache.addBitmapToMemoryCache(key.get(NUMERO_IMMAGINI_SCARICATI), bitmap);
                    Log.d("GiocoActivityDownload", key.get(NUMERO_IMMAGINI_SCARICATI) + " aggiunta in cash");
                    NUMERO_IMMAGINI_SCARICATI++;


                    if(NUMERO_IMMAGINI_SCARICATI == NUMERO_IMMAGINI){
                        Log.d("GiocoActivityDownload", "Tutte le immagini scaricate");
                        aggiungiFragmentGioco(fragmentClass, bundle_esercizio);
                        dialogPopupCaricamentoEsercizio.dismiss();
                    }else{
                        Log.d("GiocoActivityDownload", "Tutte le immagini non scaricate");
                        eseguiDownloadImmagine(pathImmagine, key, bundle_esercizio, fragmentClass);
                    }

                })
                .addOnFailureListener(exception -> {
                    // Handle any errors that occur during the download process
                    Log.e("GiocoActivity", "Errore durante il download dell'immagine: " + exception.getMessage());
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