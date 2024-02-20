package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;

public class EsercizioGiocoFragmentTipologia1 extends Fragment {

    private String uidEsercizio;
    private EsercizioTipologia1 esercizioTipologia1;
    private int sfondoSelezionato = 0;
    private ConstraintLayout layout;
    private GiocoActivity giocoActivity;
    private ArrayList<String> pathAiuti = new ArrayList<>();
    private int countAiuto = 0;
    private MediaPlayer mediaPlayer;
    private FirebaseStorage storage;
    private StorageReference audioRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recupera l'esercizio selezionato dall'utente
        if (getArguments() != null && getArguments().getString("esercizio") != null) {
            uidEsercizio = getArguments().getString("esercizio");
            Log.d("EsercizioGiocoFragmentTipologia1", "Esercizio recuperato: " + uidEsercizio);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_esercizio_gioco_tipologia1, container, false);

        layout = view.findViewById(R.id.esercizio_tipologia1);
        TextView esercizioGiocoTipologia1 = view.findViewById(R.id.esercizioGiocoTipologia1);
        CardView cardView = view.findViewById(R.id.cardViewEsercizioTipologia1);
        TextView titoloEsercizioTipologia1 = view.findViewById(R.id.titoloEsercizioTipologia1);
        TextView aiutiUtilizzati = view.findViewById(R.id.aiutiUtilizzati);
        TextView risposta_lable = view.findViewById(R.id.risposta_lable);

        storage = FirebaseStorage.getInstance();

        giocoActivity = (GiocoActivity) getActivity();

        // Inizializza il layout con l'immagine di sfondo selezionata in base a  public Integer sfondoSelezionato di GiocoActivity
        switch (giocoActivity.sfondoSelezionato) {
            case 0:
                layout.setBackgroundResource(R.drawable.deserto);
                esercizioGiocoTipologia1.setTextColor(getResources().getColor(R.color.primaryDeserto));
                cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryDeserto));
                titoloEsercizioTipologia1.setTextColor(getResources().getColor(R.color.thirdDeserto));
                aiutiUtilizzati.setTextColor(getResources().getColor(R.color.thirdDeserto));
                risposta_lable.setTextColor(getResources().getColor(R.color.thirdDeserto));
                break;
            case 1:
                layout.setBackgroundResource(R.drawable.antartide);
                esercizioGiocoTipologia1.setTextColor(getResources().getColor(R.color.secondaryAntartide));
                cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryAntartide));
                titoloEsercizioTipologia1.setTextColor(getResources().getColor(R.color.thirdAntartide));
                aiutiUtilizzati.setTextColor(getResources().getColor(R.color.thirdAntartide));
                risposta_lable.setTextColor(getResources().getColor(R.color.thirdAntartide));
                break;
            case 2:
                layout.setBackgroundResource(R.drawable.giungla);
                esercizioGiocoTipologia1.setTextColor(getResources().getColor(R.color.secondaryGiungla));
                cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryGiungla));
                titoloEsercizioTipologia1.setTextColor(getResources().getColor(R.color.thirdGiungla));
                aiutiUtilizzati.setTextColor(getResources().getColor(R.color.thirdGiungla));
                risposta_lable.setTextColor(getResources().getColor(R.color.thirdGiungla));
                break;
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Recupera l'oggetto EsercizioTipologia1
        db.collection("esercizi")
                .document(uidEsercizio)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> esercizio = document.getData();
                            esercizioTipologia1 = new EsercizioTipologia1(esercizio.get("tipologia").toString(),
                                    esercizio.get("nome").toString(),
                                    esercizio.get("logopedista").toString(),
                                    esercizio.get("tipologia").toString(),
                                    esercizio.get("immagine").toString(),
                                    esercizio.get("descrizioneImmagine").toString(),
                                    esercizio.get("audio1").toString(),
                                    esercizio.get("audio2").toString(),
                                    esercizio.get("audio3").toString());

                            // Recupera l'immagine dell'esercizio
                            ImageView immagine = view.findViewById(R.id.immagineEsercizioTipologia1);
                            String pathImmagine = esercizioTipologia1.getImmagine();
                            FirebaseHelper.downloadImmagine(immagine, pathImmagine);

                            // Retrive dell'esercizio tipologia 1
                            retriveEsercizioTipologia1(view);
                        }
                    }
                });

        // Registrazione risposta
        FloatingActionButton rispostaButton = view.findViewById(R.id.risposta);
        rispostaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia la registrazione della risposta
            }
        });


    }

    private void retriveEsercizioTipologia1(View view) {

        if (esercizioTipologia1 != null) {

            // Recupera gli audio dell'esercizio
            pathAiuti.add(esercizioTipologia1.getAudio1());
            pathAiuti.add(esercizioTipologia1.getAudio2());
            pathAiuti.add(esercizioTipologia1.getAudio3());

            giocoActivity = (GiocoActivity) getActivity();
            mediaPlayer = giocoActivity.mediaPlayer;

            FloatingActionButton playButton1 = view.findViewById(R.id.aiuto1);
            FloatingActionButton playButton2 = view.findViewById(R.id.aiuto2);
            FloatingActionButton playButton3 = view.findViewById(R.id.aiuto3);
            TextView aiutiUtilizzati = view.findViewById(R.id.numero_aiuti_utilizzati);

            // Conta quanti aiuti sono disponibili
            int aiutiDisponibili = 0;
            for (int i = 0; i < 3; i++) {
                if (pathAiuti.get(i) != null && !pathAiuti.get(i).equals("null")) {
                    aiutiDisponibili++;
                }
            }

            // PlayButton1
            if (pathAiuti.get(0) == null || pathAiuti.get(0).equals("null")) {
                playButton1.setVisibility(View.GONE);
            } else {
                playButton1.setVisibility(View.VISIBLE);
                playButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                            }
                            // Avvia la riproduzione dell'audio associato al pulsante
                            audioRef = storage.getReference().child(pathAiuti.get(0));
                            FirebaseHelper.startAudioPlayback(audioRef, playButton1);
                            countAiuto++;
                            aiutiUtilizzati.setText("" + (countAiuto));
                        }
                });
            }


            // PlayButton2
            if (pathAiuti.get(1) == null || pathAiuti.get(1).equals("null")) {
                playButton2.setVisibility(View.GONE);
            } else {
                playButton2.setVisibility(View.VISIBLE);
                playButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                        // Avvia la riproduzione dell'audio associato al pulsante
                        audioRef = storage.getReference().child(pathAiuti.get(1));
                        FirebaseHelper.startAudioPlayback(audioRef, playButton2);
                        countAiuto++;
                        aiutiUtilizzati.setText("" + (countAiuto));
                    }
                });
            }

            // PlayButton3
            if (pathAiuti.get(2) == null || pathAiuti.get(2).equals("null")) {
                playButton3.setVisibility(View.GONE);
            } else {
                playButton3.setVisibility(View.VISIBLE);
                playButton3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                            }
                            // Avvia la riproduzione dell'audio associato al pulsante
                            audioRef = storage.getReference().child(pathAiuti.get(2));
                            FirebaseHelper.startAudioPlayback(audioRef, playButton3);
                            countAiuto++;
                            aiutiUtilizzati.setText("" + (countAiuto));
                    }
                });
            }
        }
    }
}
