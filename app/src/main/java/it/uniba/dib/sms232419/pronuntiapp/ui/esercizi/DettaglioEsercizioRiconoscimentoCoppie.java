package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia3;


public class DettaglioEsercizioRiconoscimentoCoppie extends Fragment {
    private EsercizioTipologia3 esercizio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            //recupero il figlio dal bundle passato al fragment
            esercizio = getArguments().getParcelable("esercizio");

            Log.d("DettaglioEsercizio", "Esercizio recuperato: " + esercizio.getNome());
        }else{
            Log.d("DettaglioEsercizio", "Bundle null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dettaglio_esercizio3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recupero il nome dell'esercizio
        MaterialTextView nome_esercizio = view.findViewById(R.id.nome_esercizio3_dettaglio);
        nome_esercizio.setText(esercizio.getNome());


        //recupero l'immagine da firebase storage
        ImageView immagine1 = view.findViewById(R.id.image1_view_dettaglio_esercizio3);
        String pathImmagine1 = esercizio.getImmagine1();

        FirebaseHelper.downloadImmagine(immagine1, pathImmagine1);

        //recupero l'immagine2 da firebase storage
        ImageView immagine2 = view.findViewById(R.id.image2_view_dettaglio_esercizio3);
        String pathImmagine2 = esercizio.getImmagine2();

        FirebaseHelper.downloadImmagine(immagine2, pathImmagine2);

        //recupero l'audio da firebase storage
        String pathAudio = esercizio.getAudio();
        FloatingActionButton playButton = view.findViewById(R.id.play_audio_button); // Replace with your Button ID
        FirebaseHelper.playAudioFromFirebaseStorage(pathAudio, playButton);

        //recupero l'immaigne corretta
        TextView immagineCorretta = view.findViewById(R.id.immagine_corretta);
        immagineCorretta.setText(immagineCorretta.getText() + " " + esercizio.getImmagineCorretta());

    }
}
