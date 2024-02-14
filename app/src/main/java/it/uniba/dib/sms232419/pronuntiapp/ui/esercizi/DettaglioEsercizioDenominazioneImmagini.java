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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        String pathImmagine = esercizio.getImmagine().toString();

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(pathImmagine);

        // Download directly from StorageReference using Glide
        final long MAX_DOWNLOAD_SIZE = 2048 * 2048; // 1MB max download size
        storageRef.getBytes(MAX_DOWNLOAD_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Decode the byte array into a Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Set the Bitmap to the ImageView
                immagine.setImageBitmap(bitmap);
            }
        });

        //recupero l'audio 1 da firebase storage
        String pathAudio1 = esercizio.getAudio1().toString();
        FloatingActionButton playButton1 = view.findViewById(R.id.play_audio1_button); // Replace with your Button ID
        FirebaseHelper.playAudioFromFirebaseStorage(pathAudio1, playButton1);

        //recupero l'audio 2 da firebase storage
        String pathAudio2 = esercizio.getAudio2().toString();
        FloatingActionButton playButton2 = view.findViewById(R.id.play_audio2_button); // Replace with your Button ID
        FirebaseHelper.playAudioFromFirebaseStorage(pathAudio2, playButton2);

        //recupero l'audio 3 da firebase storage
        String pathAudio3 = esercizio.getAudio3().toString();
        FloatingActionButton playButton3 = view.findViewById(R.id.play_audio3_button); // Replace with your Button ID
        FirebaseHelper.playAudioFromFirebaseStorage(pathAudio3, playButton3);
    }

}