package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia3;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class EsercizioGiocoFragmentTipologia3 extends Fragment {

    private EsercizioTipologia3 esercizioTipologia3;
    private GiocoActivity giocoActivity;
    private ConstraintLayout layout;
    private Bitmap immagine1Bitmap;
    private Bitmap immagine2Bitmap;
    private String audioRispostaName, audioRispostaPathFirebase;
    private Uri audioUriRisposta;
    private static MediaPlayer mediaPlayerEsercizio;
    private final int sfondoSelezionato = 0;
    private String pathAudio;
    private int countAiuto = 0;
    private MediaPlayer mediaPlayer;
    private FirebaseStorage storage;
    private StorageReference audioRef;
    private boolean audioInRiproduzione = false;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private static final int STOP_AUDIO_AIUTO = 0;
    AlertDialog dialogPopupRisultatoEsercizio;

    private String audioDownloadUrl;

    private int immagineScelta = 0;

    String idFiglio;

    private final Handler handlerAudio = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_AUDIO_AIUTO:
                    //rendi possibile la riproduzione di un nuovo audio
                    audioInRiproduzione = false;
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pausa la riproduzione dell'audio di sottofondo
        giocoActivity = (GiocoActivity) getActivity();
        giocoActivity.mediaPlayer.pause();

        // Recupera la scheda selezionata dall'utente
        if(getArguments() != null && getArguments().getParcelable("esercizio3") != null) {
            esercizioTipologia3 = getArguments().getParcelable("esercizio3");
            Log.d("EsercizioGiocoFragmentTipologia3", "Scheda: " + esercizioTipologia3.getNome());

            // Recupera l'immagine1 dell'esercizio dalla cache
            immagine1Bitmap = BitmapCache.getBitmapFromMemCache("immagine1");
            Log.d("EsercizioGiocoFragmentTipologia3", "Immagine1: " + immagine1Bitmap);

            // Recupera l'immagine2 dell'esercizio dalla cache
            immagine2Bitmap = BitmapCache.getBitmapFromMemCache("immagine2");
            Log.d("EsercizioGiocoFragmentTipologia3", "Immagine2: " + immagine2Bitmap);
        }

        giocoActivity.dialogPopupCaricamentoEsercizio.dismiss();
        Log.d("PopUp_Fragmnet", "PopUp Fragmnet svisulizzato");
        Log.d("PopUp_Fragmnet", "Popup: " + giocoActivity.dialogPopupCaricamentoEsercizio);

        mediaPlayerEsercizio = new MediaPlayer();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_esercizio_gioco_tipologia3, container, false);
        layout = view.findViewById(R.id.esercizio_tipologia3);

        storage = FirebaseStorage.getInstance();

        // Inizializza il layout con l'immagine di sfondo selezionata in base a  public Integer sfondoSelezionato di GiocoActivity
        switch (giocoActivity.sfondoSelezionato) {
            case 0:
                layout.setBackgroundResource(R.drawable.deserto);
                break;
            case 1:
                layout.setBackgroundResource(R.drawable.antartide);
                break;
            case 2:
                layout.setBackgroundResource(R.drawable.giungla);
                break;
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //importo le immagini dell'esercizio
        ImageView immagine1Esercizio = view.findViewById(R.id.immagine1EsercizioTipologia1);
        immagine1Esercizio.setImageBitmap(immagine1Bitmap);

        ImageView immagine2Esercizio = view.findViewById(R.id.immagine2EsercizioTipologia1);
        immagine2Esercizio.setImageBitmap(immagine2Bitmap);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup_esercizio3);

        //recupero l'audio della risposta
        pathAudio = esercizioTipologia3.getAudio();

        mediaPlayer = giocoActivity.mediaPlayer;

        FloatingActionButton playButton = view.findViewById(R.id.audioEsercizioTipologia3);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia la riproduzione dell'audio associato al pulsante
                audioRef = storage.getReference().child(pathAudio);
                FirebaseHelper.startAudioPlayback(audioRef, playButton);
            }
        });

        audioRispostaName = getActivity().getExternalCacheDir().getAbsolutePath();
        audioRispostaName += "/audioRispostaEsercizio3"+giocoActivity.scheda.getNome()+".mp3";

        //Selezione immagine corretta
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonImmagine1) {
                immagineScelta = 1;
            } else if (checkedId == R.id.radioButtonImmagine2) {
                immagineScelta = 2;
            } else {
            }
        });

        Button confermaButton = view.findViewById(R.id.conferma_risposta);
        confermaButton.setOnClickListener(v -> {
            if(immagineScelta == 0) {
                Toasty.error(getContext(), "Seleziona un'immagine", Toast.LENGTH_SHORT, true).show();
                return;
            }

            // Get a Firestore instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            //Riferimento al bambino
            Figlio bambino = giocoActivity.figlio;
            db.collection("figli")
                    .whereEqualTo("codiceFiscale", bambino.getCodiceFiscale())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if(queryDocumentSnapshots.isEmpty()) {
                            Log.d("EsercizioGiocoFragmentTipologia3", "Nessun figlio trovato");
                            return;
                        }
                        else {
                            idFiglio = queryDocumentSnapshots.getDocuments().get(0).getId();
                        }

                        // Create a Map to represent the data
                        Map<String, Object> data = new HashMap<>();
                        data.put("bambino", idFiglio);
                        data.put("esercizio", esercizioTipologia3.getEsercizioId());
                        data.put("risposta", immagineScelta);

                        // Add the document to the collection
                        db.collection("risultato_esercizio3")
                                .add(data)
                                .addOnSuccessListener(documentReference -> {
                                    // Document added successfully
                                    Log.d("EsercizioGiocoFragmentTipologia3", "DocumentSnapshot added with ID: " + documentReference.getId());

                                    new Thread(new Runnable() {
                                        public void run() {
                                            if(esercizioTipologia3.correzioneEsercizio(immagineScelta)){
                                                // Calcola il punteggio dell'esercizio
                                                giocoActivity.figlio.setPunteggioGioco(calcolaPunteggio(esercizioTipologia3.correzioneEsercizio(immagineScelta)));

                                                // Aggiorna il punteggio del figlio nel database
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("figli")
                                                        .whereEqualTo("token", giocoActivity.figlio.getToken())
                                                        .get()
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    // Ottieni l'ID del documento
                                                                    String docId = document.getId();

                                                                    // Aggiorna il campo punteggioGioco
                                                                    db.collection("figli").document(docId)
                                                                            .update("punteggioGioco", giocoActivity.figlio.getPunteggioGioco())
                                                                            .addOnSuccessListener(aVoid -> {
                                                                                Log.d("EsercizioGiocoFragmentTipologia3", "Punteggio aggiornato con successo: " + giocoActivity.figlio.getPunteggioGioco());

                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        // Il tuo codice che interagisce con la UI qui
                                                                                        mostraPopupEsercizioCorretto();
                                                                                    }
                                                                                });

                                                                            })
                                                                            .addOnFailureListener(e -> {
                                                                                Log.e("EsercizioGiocoFragmentTipologia3", "Errore nell'aggiornare il punteggio: " + e.getMessage());

                                                                            });
                                                                }
                                                            } else {
                                                                Log.e("EsercizioGiocoFragmentTipologia3", "Errore nell'ottenere i documenti: ", task.getException());
                                                            }
                                                        });
                                            }else{
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Il tuo codice che interagisce con la UI qui
                                                        mostraPopupEsercizioSbagliato();
                                                    }
                                                });
                                            }
                                        }
                                    }).start();

                                })
                                .addOnFailureListener(e -> {
                                    // Error adding document
                                    Log.e("EsercizioGiocoFragmentTipologia3", "Error adding document", e);
                                });
                    });
        });
    }

    public static int calcolaPunteggio(boolean corretto) {
        int punteggio = 10; // Initial score

        if (corretto) {
            return punteggio;
        } else {
            return 1;
        }
    }

    private void mostraPopupEsercizioCorretto(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

        LayoutInflater inflater = giocoActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_esercizio_completato, null);
        int coloreSfondo = 0;
        switch (giocoActivity.sfondoSelezionato) {
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
        view.findViewById(R.id.card_view_esercizio_completato).setBackgroundColor(getResources().getColor(coloreSfondo));

        int coloreTesto = 0;
        switch (giocoActivity.personaggioSelezionato) {
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
        TextView testo = view.findViewById(R.id.txt_popup__esercizio_completato);
        testo.setTextColor(getResources().getColor(coloreTesto));

        TextView txtPunteggio = view.findViewById(R.id.txt_punteggio_esercizio_completato);
        txtPunteggio.setTextColor(getResources().getColor(coloreTesto));
        txtPunteggio.setText(String.valueOf(giocoActivity.figlio.getPunteggioGioco()));

        Button btnContinua = view.findViewById(R.id.btn_continua_esercizio_completato);
        btnContinua.setBackgroundColor(getResources().getColor(coloreTesto));
        btnContinua.setOnClickListener(v -> {
            dialogPopupRisultatoEsercizio.dismiss();
            Bundle bundle = new Bundle();
            bundle.putParcelable("scheda", giocoActivity.scheda);
            giocoActivity.getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.avvio_gioco_fragment, GiocoFragment.class, bundle)
                    .commit();
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("scheda", giocoActivity.scheda);
                giocoActivity.getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.avvio_gioco_fragment, GiocoFragment.class, bundle)
                        .commit();
            }
        });


        dialogPopupRisultatoEsercizio = builder.create();
        dialogPopupRisultatoEsercizio.setView(view);
        dialogPopupRisultatoEsercizio.show();
    }

    private void mostraPopupEsercizioSbagliato(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

        LayoutInflater inflater = giocoActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_esercizio_sbagliato, null);
        int coloreSfondo = 0;
        switch (giocoActivity.sfondoSelezionato) {
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
        view.findViewById(R.id.card_view_esercizio_sbagliato).setBackgroundColor(getResources().getColor(coloreSfondo));

        int coloreTesto = 0;
        switch (giocoActivity.personaggioSelezionato) {
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
        TextView testo = view.findViewById(R.id.txt_popup__esercizio_sbagliato);
        testo.setTextColor(getResources().getColor(coloreTesto));

        Button btnContinua = view.findViewById(R.id.btn_continua_esercizio_sbagliato);
        btnContinua.setBackgroundColor(getResources().getColor(coloreTesto));
        btnContinua.setOnClickListener(v -> {
            dialogPopupRisultatoEsercizio.dismiss();
            Bundle bundle = new Bundle();
            bundle.putParcelable("scheda", giocoActivity.scheda);
            giocoActivity.getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.avvio_gioco_fragment, GiocoFragment.class, bundle)
                    .commit();
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("scheda", giocoActivity.scheda);
                giocoActivity.getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.avvio_gioco_fragment, GiocoFragment.class, bundle)
                        .commit();
            }
        });

        dialogPopupRisultatoEsercizio = builder.create();
        dialogPopupRisultatoEsercizio.setView(view);
        dialogPopupRisultatoEsercizio.show();
    }
}
