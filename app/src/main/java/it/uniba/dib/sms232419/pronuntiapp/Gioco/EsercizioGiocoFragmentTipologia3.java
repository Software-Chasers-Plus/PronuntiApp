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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia3;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class EsercizioGiocoFragmentTipologia3 extends Fragment {

    private EsercizioTipologia3 esercizioTipologia3;
    private GiocoActivity giocoActivity;

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
    private int punteggioEsercizio;

    String dataEsercizio;

    String idFiglio;
    private ScrollView layout;

    private int coloreSfondoPopup, coloreTestoPopup;

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

        TextView esercizioGiocoTipologia3 = view.findViewById(R.id.esercizioGiocoTipologia3);
        TextView titoloImmagine1 = view.findViewById(R.id.titoloImmagine1);
        TextView titoloImmagine2 = view.findViewById(R.id.titoloImmagine2);
        CardView cardViewImmagine1 = view.findViewById(R.id.cardView1EsercizioTipologia3);
        CardView cardViewImmagine2 = view.findViewById(R.id.cardView2EsercizioTipologia3);
        TextView riproduciAudio = view.findViewById(R.id.riproduci_audio);
        TextView dataEsercizioTextView = view.findViewById(R.id.dataEsercizioTipologia3);

        storage = FirebaseStorage.getInstance();

        // Inizializza il layout con l'immagine di sfondo selezionata in base a  public Integer sfondoSelezionato di GiocoActivity
        switch (giocoActivity.sfondoSelezionato) {
            case 0:
                layout.setBackgroundResource(R.drawable.deserto);
                esercizioGiocoTipologia3.setTextColor(getResources().getColor(R.color.secondaryDeserto));
                titoloImmagine1.setTextColor(getResources().getColor(R.color.thirdDeserto));
                titoloImmagine2.setTextColor(getResources().getColor(R.color.thirdDeserto));
                cardViewImmagine1.setCardBackgroundColor(getResources().getColor(R.color.primaryDeserto));
                cardViewImmagine2.setCardBackgroundColor(getResources().getColor(R.color.primaryDeserto));
                riproduciAudio.setTextColor(getResources().getColor(R.color.thirdDeserto));
                coloreSfondoPopup = R.color.secondaryDeserto;
                coloreTestoPopup = R.color.primaryDeserto;
                break;
            case 1:
                layout.setBackgroundResource(R.drawable.antartide);
                esercizioGiocoTipologia3.setTextColor(getResources().getColor(R.color.secondaryAntartide));
                titoloImmagine1.setTextColor(getResources().getColor(R.color.thirdAntartide));
                titoloImmagine2.setTextColor(getResources().getColor(R.color.thirdAntartide));
                cardViewImmagine1.setCardBackgroundColor(getResources().getColor(R.color.primaryAntartide));
                cardViewImmagine2.setCardBackgroundColor(getResources().getColor(R.color.primaryAntartide));
                riproduciAudio.setTextColor(getResources().getColor(R.color.primaryAntartide));
                coloreSfondoPopup = R.color.secondaryAntartide;
                coloreTestoPopup = R.color.primaryAntartide;
                break;
            case 2:
                layout.setBackgroundResource(R.drawable.giungla);
                esercizioGiocoTipologia3.setTextColor(getResources().getColor(R.color.secondaryGiungla));
                titoloImmagine1.setTextColor(getResources().getColor(R.color.thirdGiungla));
                titoloImmagine2.setTextColor(getResources().getColor(R.color.thirdGiungla));
                cardViewImmagine1.setCardBackgroundColor(getResources().getColor(R.color.primaryGiungla));
                cardViewImmagine2.setCardBackgroundColor(getResources().getColor(R.color.primaryGiungla));
                riproduciAudio.setTextColor(getResources().getColor(R.color.thirdGiungla));
                coloreSfondoPopup = R.color.secondaryGiungla;
                coloreTestoPopup = R.color.primaryGiungla;
                break;
        }
        //Recupero della data
        Scheda scheda = giocoActivity.scheda;
        ArrayList<ArrayList<String>> esercizi = scheda.getEsercizi();
        for(int i = 0; i < esercizi.size(); i++){
            if(esercizi.get(i).get(0).equals(esercizioTipologia3.getEsercizioId())){
                dataEsercizio = esercizi.get(i).get(2);
            }
        }
        dataEsercizioTextView.setText(dataEsercizioTextView.getText() + " " + dataEsercizio);

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
                                                punteggioEsercizio = calcolaPunteggio(esercizioTipologia3.correzioneEsercizio(immagineScelta), dataEsercizio);
                                                giocoActivity.figlio.setPunteggioGioco(punteggioEsercizio);

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
                                                                                        aggiornaScheda(true);
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
                                                        aggiornaScheda(false);
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

    public static int calcolaPunteggio(boolean corretto, String dataEsercizio) {
        int punteggio = 10; // Initial score

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date exerciseDateObj = formatter.parse(dataEsercizio);
            Date oggi = Calendar.getInstance().getTime();
            oggi = formatter.parse(formatter.format(oggi));

            if (corretto) {
                // If the exercise date is in the past, deduct additional points
                if (exerciseDateObj.before(oggi)) {
                    punteggio -= 2;
                }
            }
            else {
                punteggio = 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return punteggio;
    }

    private void mostraPopupEsercizioCorretto(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

        LayoutInflater inflater = giocoActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_esercizio_completato, null);

        view.findViewById(R.id.card_view_esercizio_completato).setBackgroundColor(getResources().getColor(coloreSfondoPopup));

        TextView testo = view.findViewById(R.id.txt_popup__esercizio_completato);
        testo.setTextColor(getResources().getColor(coloreTestoPopup));

        TextView txtPunteggio = view.findViewById(R.id.txt_punteggio_esercizio_completato);
        txtPunteggio.setTextColor(getResources().getColor(coloreTestoPopup));
        txtPunteggio.setText(String.valueOf(punteggioEsercizio));
        TextView labelPiu = view.findViewById(R.id.txt_piu_esercizio_completato);
        labelPiu.setTextColor(getResources().getColor(coloreTestoPopup));

        Button btnContinua = view.findViewById(R.id.btn_continua_esercizio_completato);
        btnContinua.setBackgroundColor(getResources().getColor(coloreTestoPopup));
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

        view.findViewById(R.id.card_view_esercizio_sbagliato).setBackgroundColor(getResources().getColor(coloreSfondoPopup));

        TextView testo = view.findViewById(R.id.txt_popup__esercizio_sbagliato);
        testo.setTextColor(getResources().getColor(coloreTestoPopup));

        Button btnContinua = view.findViewById(R.id.btn_continua_esercizio_sbagliato);
        btnContinua.setBackgroundColor(getResources().getColor(coloreTestoPopup));
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

    private void aggiornaScheda(Boolean completato){
        int posizioneEsercizio = -1;
        for(int i = 0; i < giocoActivity.scheda.getEsercizi().size(); i++){
            if(giocoActivity.scheda.getEsercizi().get(i).get(0).equals(esercizioTipologia3.getEsercizioId())){
                giocoActivity.scheda.getEsercizi().get(i).set(1, "completato");
                posizioneEsercizio = i;
            }
        }
        String nomeCampoEsercizio = "esercizio" + posizioneEsercizio;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("schede")
                .document(giocoActivity.scheda.getUid())
                .update(nomeCampoEsercizio, giocoActivity.scheda.getEsercizi().get(posizioneEsercizio))
                .addOnSuccessListener(aVoid -> {
                    if(completato){
                        mostraPopupEsercizioCorretto();
                    }else{
                        mostraPopupEsercizioSbagliato();
                    }
                })
                .addOnFailureListener(e -> {
                    if(completato){
                        mostraPopupEsercizioCorretto();
                    }else{
                        mostraPopupEsercizioSbagliato();
                    }
                });
    }
}
