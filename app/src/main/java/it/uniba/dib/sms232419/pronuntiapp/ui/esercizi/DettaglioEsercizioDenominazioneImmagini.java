package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;


public class DettaglioEsercizioDenominazioneImmagini extends Fragment {
    private EsercizioTipologia1 esercizio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            //recupero il figlio dal bundle passato al fragment
            esercizio = getArguments().getParcelable("esercizio");

            Log.d("DettaglioEsercizio", "Esercizio recuperato: "+esercizio.getNome());
        }else{
            Log.d("DettaglioEsercizio", "Bundle null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dettaglio_esercizio1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recupero il nome dell'esercizio
        MaterialTextView nome_esercizio = view.findViewById(R.id.nome_esercizio1_dettaglio);
        nome_esercizio.setText(esercizio.getNome());

        //recupero la descrizione dell'immagine
        EditText descrizione_immagine = view.findViewById(R.id.descrizione_immagine_esercizio1_dettaglio);
        descrizione_immagine.setText(esercizio.getDescrizione_immagine());

        //recupero l'immagine da firebase storage
        ImageView immagine = view.findViewById(R.id.image_view_dettaglio_esercizio1);
        String pathImmagine = esercizio.getImmagine();

        FirebaseHelper.downloadImmagine(immagine, pathImmagine);

        //recupero l'audio 1 da firebase storage
        if(!Objects.equals(esercizio.getAudio1(), "null")){
            String pathAudio1 = esercizio.getAudio1();
            FloatingActionButton playButton1 = view.findViewById(R.id.play_audio1_button); // Replace with your Button ID
            FirebaseHelper.playAudioFromFirebaseStorage(pathAudio1, playButton1);
        } else {
            FloatingActionButton playButton1 = view.findViewById(R.id.play_audio1_button);
            playButton1.setVisibility(View.GONE);
            TextView testo1 = view.findViewById(R.id.audio1_testo);
            testo1.setText(R.string.audio_1_non_disponibile);
        }



        //recupero l'audio 2 da firebase storage
        if(!Objects.equals(esercizio.getAudio2(), "null")){
            String pathAudio2 = esercizio.getAudio2();
            FloatingActionButton playButton2 = view.findViewById(R.id.play_audio2_button); // Replace with your Button ID
            FirebaseHelper.playAudioFromFirebaseStorage(pathAudio2, playButton2);
        } else {
            FloatingActionButton playButton2 = view.findViewById(R.id.play_audio2_button);
            playButton2.setVisibility(View.GONE);
            TextView testo2 = view.findViewById(R.id.audio2_testo);
            testo2.setText(R.string.audio_2_non_disponibile);
        }



        //recupero l'audio 3 da firebase storage
        if(!Objects.equals(esercizio.getAudio3(), "null")){
            String pathAudio3 = esercizio.getAudio3();
            FloatingActionButton playButton3 = view.findViewById(R.id.play_audio3_button); // Replace with your Button ID
            FirebaseHelper.playAudioFromFirebaseStorage(pathAudio3, playButton3);
        } else {
            FloatingActionButton playButton3 = view.findViewById(R.id.play_audio3_button);
            playButton3.setVisibility(View.GONE);
            TextView testo3 = view.findViewById(R.id.audio3_testo);
            testo3.setText(R.string.audio_3_non_disponibile);
        }


    }

}
