package it.uniba.dib.sms232419.pronuntiapp.Gioco;


import androidx.appcompat.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.PermissionManager;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.RecordAudio;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia2;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class EsercizioGiocoFragmentTipologia2 extends Fragment {

    int punteggioEsercizio;
    private EsercizioTipologia2 esercizioTipologia2;
    private GiocoActivity giocoActivity;
    private LinearLayout layout,layoutCorrezione;
    private FirebaseStorage storage;
    private static final int STOP_AUDIO_AIUTO = 0;
    private boolean audioInRiproduzione = false;
    private MediaPlayer mediaPlayerEsercizio;
    private String pathAudio;
    private StorageReference audioRef;
    private String audioRispostaName, audioRispostaPathFirebase;
    private Uri audioUriRisposta;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private String audioDownloadUrl;
    private AlertDialog dialogPopupRisultatoEsercizio;
    private int coloreSfondoPopup, coloreTestoPopup;
    private CircularProgressIndicator progressBar;
    private TextView textCorrezione;
    private ConstraintLayout layoutEsercizio;
    String dataEsercizio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pausa la riproduzione dell'audio di sottofondo
        giocoActivity = (GiocoActivity) getActivity();
        giocoActivity.mediaPlayer.pause();

        // Recupera la scheda selezionata dall'utente
        if(getArguments() != null && getArguments().getParcelable("esercizio") != null) {
            esercizioTipologia2 = getArguments().getParcelable("esercizio");
            Log.d("EsercizioGiocoFragmentTipologia2", "Esercizio recuperato: " + esercizioTipologia2.getNome());
        }

        giocoActivity.dialogPopupCaricamentoEsercizio.dismiss();
        Log.d("PopUp_Fragmnet", "PopUp Fragmnet svisulizzato");
        Log.d("PopUp_Fragmnet", "Popup: " + giocoActivity.dialogPopupCaricamentoEsercizio);

        mediaPlayerEsercizio = new MediaPlayer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_esercizio_gioco_tipologia2, container, false);

        layout = view.findViewById(R.id.esercizio_tipologia2);

        layoutCorrezione = view.findViewById(R.id.linear_layout_correzione_esercizio_tipologia2);
        progressBar = view.findViewById(R.id.progressIndicator_esercizio_tipologia2);
        textCorrezione = view.findViewById(R.id.txt_correzione_esercizio_tipologia2);

        layoutEsercizio = view.findViewById(R.id.esercizio_tipologia2_constraint_layout);
        TextView titoloEsercizio = view.findViewById(R.id.esercizioGiocoTipologia2);
        TextView rispostaLabel = view.findViewById(R.id.risposta_lable_tipologia2);
        TextView riproduciAudioLabel = view.findViewById(R.id.riproduci_audio_lable_tipologia2);
        TextView dataEsercizioTextView = view.findViewById(R.id.dataEsercizioTipologia2);

        storage = FirebaseStorage.getInstance();

        // Inizializza il layout con l'immagine di sfondo selezionata in base a  public Integer sfondoSelezionato di GiocoActivity
        switch (giocoActivity.sfondoSelezionato) {
            case 0:
                layout.setBackgroundResource(R.drawable.deserto);
                progressBar.setIndicatorColor(getResources().getColor(R.color.secondaryDeserto));
                textCorrezione.setTextColor(getResources().getColor(R.color.thirdDeserto));


                titoloEsercizio.setTextColor(getResources().getColor(R.color.secondaryDeserto));
                rispostaLabel.setTextColor(getResources().getColor(R.color.thirdDeserto));
                riproduciAudioLabel.setTextColor(getResources().getColor(R.color.thirdDeserto));

                coloreSfondoPopup = R.color.secondaryDeserto;
                coloreTestoPopup = R.color.primaryDeserto;
                break;
            case 1:
                layout.setBackgroundResource(R.drawable.antartide);
                progressBar.setIndicatorColor(getResources().getColor(R.color.secondaryAntartide));
                textCorrezione.setTextColor(getResources().getColor(R.color.secondaryAntartide));


                titoloEsercizio.setTextColor(getResources().getColor(R.color.secondaryAntartide));
                rispostaLabel.setTextColor(getResources().getColor(R.color.secondaryAntartide));
                riproduciAudioLabel.setTextColor(getResources().getColor(R.color.secondaryAntartide));

                coloreSfondoPopup = R.color.secondaryAntartide;
                coloreTestoPopup = R.color.primaryAntartide;
                break;
            case 2:
                layout.setBackgroundResource(R.drawable.giungla);
                progressBar.setIndicatorColor(getResources().getColor(R.color.secondaryGiungla));
                textCorrezione.setTextColor(getResources().getColor(R.color.thirdGiungla));

                titoloEsercizio.setTextColor(getResources().getColor(R.color.secondaryGiungla));
                rispostaLabel.setTextColor(getResources().getColor(R.color.thirdGiungla));
                riproduciAudioLabel.setTextColor(getResources().getColor(R.color.thirdGiungla));

                coloreSfondoPopup = R.color.secondaryGiungla;
                coloreTestoPopup = R.color.primaryGiungla;
                break;
        }
        //Recupero della data
        Scheda scheda = giocoActivity.scheda;
        ArrayList<ArrayList<String>> esercizi = scheda.getEsercizi();
        for(int i = 0; i < esercizi.size(); i++){
            if(esercizi.get(i).get(0).equals(esercizioTipologia2.getEsercizioId())){
                dataEsercizio = esercizi.get(i).get(2);
            }
        }
        dataEsercizioTextView.setText(dataEsercizioTextView.getText() + " " + dataEsercizio);

        layoutCorrezione.setVisibility(View.GONE);
        layoutEsercizio.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recupera gli audio dell'esercizio
        pathAudio = esercizioTipologia2.getAudio();

        mediaPlayerEsercizio = giocoActivity.mediaPlayer;

        FloatingActionButton playButton = view.findViewById(R.id.audio1);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia la riproduzione dell'audio associato al pulsante
                audioRef = storage.getReference().child(pathAudio);
                FirebaseHelper.startAudioPlayback(audioRef, playButton);
            }
        });

        audioRispostaName = getActivity().getExternalCacheDir().getAbsolutePath();
        audioRispostaName += "/auudioRispostaEsercizio2"+giocoActivity.scheda.getNome()+".mp3";

        // Registrazione risposta
        FloatingActionButton rispostaButton = view.findViewById(R.id.risposta);
        rispostaButton.setOnClickListener(v -> {
            PermissionManager.requestPermissions(EsercizioGiocoFragmentTipologia2.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, new PermissionManager.PermissionListener() {
                @Override
                public void onPermissionsGranted() {
                    audioUriRisposta = recordAudio(rispostaButton, audioRispostaName);
                }

                @Override
                public void onPermissionsDenied() {
                    // Permesso non concesso, mostra un dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Permesso negato")
                            .setMessage("Per favore, fornisci il permesso per registrare l'audio.")
                            .setPositiveButton("Impostazioni", (dialog, which) -> {
                                // Aprire le impostazioni
                                openAppSettings();
                            })
                            .show();
                }
            });
        });

        FloatingActionButton playRispostaButton = view.findViewById(R.id.play_risposta_button);
        playRispostaButton.setOnClickListener(v -> {
            if(audioUriRisposta == null) {
                Toasty.error(getContext(), R.string.registra_la_risposta_prima_di_riprodurla, Toast.LENGTH_SHORT, true).show();
            }else{
                RecordAudio.onPlay(mStartPlaying , audioRispostaName);
                if (mStartPlaying) {
                    playRispostaButton.setImageResource(R.drawable.pause_icon_white_24);
                } else {
                    playRispostaButton.setImageResource(R.drawable.baseline_play_arrow_24);
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        audioRispostaPathFirebase = "risposteEsercizio/"+giocoActivity.figlio.getCodiceFiscale() +"_"+giocoActivity.scheda.getNome() +"_"+esercizioTipologia2.getNome()+"_audioRisposta.mp3";
        Button confermaButton = view.findViewById(R.id.conferma_risposta);
        confermaButton.setOnClickListener(v -> {
            if (audioUriRisposta != null) {
                // Invia l'audio registrato al server
                uploadFileToFirebaseStorage(audioUriRisposta, audioRispostaPathFirebase, (success) -> {
                    if (success) {
                        new Thread(new Runnable() {
                            public void run() {
                                String risposta = FirebaseHelper.audioToText(audioDownloadUrl).orElse("Default");
                                Log.d("EsercizioGiocoFragmentTipologia2", "Risposta: " + risposta);

                                // Calcola il punteggio dell'esercizio
                                if(esercizioTipologia2.correzioneEsercizio(risposta)){
                                    // Calcola il punteggio dell'esercizio
                                    punteggioEsercizio = calcolaPunteggio(esercizioTipologia2.correzioneEsercizio(risposta), dataEsercizio);
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
                                                                    Log.d("EsercizioGiocoFragmentTipologia2", "Punteggio aggiornato con successo: " + giocoActivity.figlio.getPunteggioGioco());

                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            // Il tuo codice che interagisce con la UI qui
                                                                            layoutCorrezione.setVisibility(View.GONE);
                                                                            aggiornaScheda(true);
                                                                        }
                                                                    });

                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Log.e("EsercizioGiocoFragmentTipologia2", "Errore nell'aggiornare il punteggio: " + e.getMessage());

                                                                });
                                                    }
                                                } else {
                                                    Log.e("EsercizioGiocoFragmentTipologia2", "Errore nell'ottenere i documenti: ", task.getException());
                                                }
                                            });
                                }else{
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Il tuo codice che interagisce con la UI qui
                                            layoutCorrezione.setVisibility(View.GONE);
                                            aggiornaScheda(false);
                                        }
                                    });
                                }
                            }
                        }).start();


                    }
                });
                Toasty.success(getContext(), R.string.risposta_inviata_con_successo, Toast.LENGTH_SHORT, true).show();
                layoutEsercizio.setVisibility(View.GONE);
                layoutCorrezione.setVisibility(View.VISIBLE);
            } else {
                Toasty.error(getContext(), R.string.registra_la_risposta_prima_di_inviarla, Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayerEsercizio != null) {
            mediaPlayerEsercizio.release();
            mediaPlayerEsercizio = null;
        }
        //distruggo l'audio di risposta
        File fileAudio = new File(audioRispostaName);
        fileAudio.delete();
    }

    // Interfaccia per il callback quando il caricamento è completato
    interface OnUploadCompleteListener {
        void onUploadComplete(boolean success);
    }

    private Uri recordAudio(ImageButton record_audio_button, String audioName) {
        Uri audioUri = null;

        RecordAudio.onRecord(mStartRecording, audioName);
        if (mStartRecording) {
            record_audio_button.setImageResource(R.drawable.stop_icon_24);
            Toasty.success(getContext(), "Registrazione in corso", Toast.LENGTH_SHORT, true).show();
        } else {
            File fileAudio = new File(audioName);
            audioUri = Uri.fromFile(fileAudio);
            record_audio_button.setImageResource(R.drawable.mic_fill0_wght400_grad0_opsz24);
            Toasty.success(getContext(), "Registrazione interrotta", Toast.LENGTH_SHORT, true).show();
        }
        mStartRecording = !mStartRecording;

        return audioUri;
    }

    // Metodo per aprire le impostazioni dell'applicazione
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    // Metodo per caricare un file su Firebase Storage
    private void uploadFileToFirebaseStorage(Uri fileUri, String path, EsercizioGiocoFragmentTipologia1.OnUploadCompleteListener callback) {
        // Ottieni un riferimento al Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Crea un riferimento al file su Firebase Storage
        StorageReference fileRef = storageRef.child(path);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Caricamento in corso...");
        progressDialog.setCancelable(false); // Impedisci all'utente di chiudere la finestra di dialogo
        progressDialog.show();

        // Carica il file su Firebase Storage
        if(fileUri == null) {
            progressDialog.dismiss();
            callback.onUploadComplete(true);
            return;
        }
        fileRef.putFile(fileUri)
                .addOnProgressListener(snapshot -> {
                    // Aggiorna la percentuale di completamento
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Caricamento in corso... " + (int) progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    // Ottieni l'URI del file caricato
                    fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Ottieni l'URI del file caricato
                                audioDownloadUrl = uri.toString();
                                Log.d("EsercizioDenominazioneImmagine", "File caricato con successo: " + audioDownloadUrl);
                                progressDialog.dismiss();
                                callback.onUploadComplete(true); // Notifica il chiamante che il caricamento è completato con successo
                            })
                            .addOnFailureListener(e -> {
                                // Gestisci l'errore
                                Log.e("EsercizioDenominazioneImmagine", "Errore nel caricare il file: " + e.getMessage());
                                progressDialog.dismiss();
                                callback.onUploadComplete(false); // Notifica il chiamante che si è verificato un errore durante il caricamento
                            });
                })
                .addOnFailureListener(e -> {
                    // Gestisci l'errore
                    Log.e("EsercizioDenominazioneImmagine", "Errore nel caricare il file: " + e.getMessage());
                    progressDialog.dismiss();
                    callback.onUploadComplete(false); // Notifica il chiamante che si è verificato un errore durante il caricamento
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
            if(giocoActivity.scheda.getEsercizi().get(i).get(0).equals(esercizioTipologia2.getEsercizioId())){
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
