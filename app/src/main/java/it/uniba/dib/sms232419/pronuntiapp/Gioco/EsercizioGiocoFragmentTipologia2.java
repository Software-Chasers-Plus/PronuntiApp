package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.PermissionManager;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.RecordAudio;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia2;

public class EsercizioGiocoFragmentTipologia2 extends Fragment {

    private EsercizioTipologia2 esercizioTipologia2;
    private GiocoActivity giocoActivity;
    private ConstraintLayout layout;
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
                                Log.d("EsercizioGiocoFragmentTipologia2", "Trascrizione: " + esercizioTipologia2.getTrascrizione_audio());
                                Log.d("EsercizioGiocoFragmentTipologia2", "Corretto: " + esercizioTipologia2.correzioneEsercizio(risposta));

                                // Calcola il punteggio dell'esercizio
                                giocoActivity.figlio.setPunteggioGioco(calcolaPunteggio(esercizioTipologia2.correzioneEsercizio(risposta)));
                            }
                        }).start();


                    }
                });
                Toasty.success(getContext(), R.string.risposta_inviata_con_successo, Toast.LENGTH_SHORT, true).show();
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

    public static int calcolaPunteggio(boolean corretto) {
        int punteggio = 8;

        if(corretto) {
            punteggio = 1;
        }

        return punteggio;
    }

}
