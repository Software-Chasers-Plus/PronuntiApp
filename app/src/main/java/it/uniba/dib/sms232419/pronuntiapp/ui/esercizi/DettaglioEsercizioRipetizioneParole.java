package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia2;


public class DettaglioEsercizioRipetizioneParole extends Fragment {
    private EsercizioTipologia2 esercizio;

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
        return inflater.inflate(R.layout.dettaglio_esercizio2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recupero il nome dell'esercizio
        MaterialTextView nome_esercizio = view.findViewById(R.id.nome_esercizio2_dettaglio);
        nome_esercizio.setText(esercizio.getNome());

        //recupero l'audio da firebase storage
        String pathAudio = esercizio.getAudio().toString();
        FloatingActionButton playButton = view.findViewById(R.id.play_audio1_button); // Replace with your Button ID
        FirebaseHelper.playAudioFromFirebaseStorage(pathAudio, playButton);

        //recupero la trascrizione dell'audio
        EditText trascrizione_audio = view.findViewById(R.id.trascrizione_audio_esercizio2_dettaglio);
        trascrizione_audio.setText(esercizio.getTrascrizione_audio());
    }

}
