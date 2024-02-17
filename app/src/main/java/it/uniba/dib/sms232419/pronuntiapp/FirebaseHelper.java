package it.uniba.dib.sms232419.pronuntiapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

import android.widget.ImageView;

import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class FirebaseHelper {
    private static MediaPlayer mediaPlayer;

    public static void playAudioFromFirebaseStorage(String path, FloatingActionButton button) {
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(path);

        // Set an OnClickListener to the button
        button.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                // If audio is currently playing, stop it
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                button.setImageResource(R.drawable.baseline_play_arrow_24);
            } else {
                // Otherwise, start playing the audio
                startAudioPlayback(storageRef, button);
            }
        });
    }

    private static void startAudioPlayback(StorageReference storageRef, FloatingActionButton button) {
        // Download the audio file
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    // Create a MediaPlayer instance
                    mediaPlayer = new MediaPlayer();

                    // Set the data source to the URI of the downloaded audio file
                    mediaPlayer.setDataSource(uri.toString());

                    // Prepare the MediaPlayer asynchronously
                    mediaPlayer.prepareAsync();

                    // Set up a listener for when the MediaPlayer is prepared
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            // Start playing the audio
                            mp.start();
                            button.setImageResource(R.drawable.pause_icon_white_24);                        }
                    });
                } catch (IOException e) {
                    // Handle any errors
                    e.printStackTrace();
                }
            }
        });
    }

    public static void downloadImmagine(ImageView immagine, String pathImmagine) {
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(pathImmagine);

        // Download directly from StorageReference using Glide
        final long MAX_DOWNLOAD_SIZE = 2048 * 2048; // 1MB max download size
        storageRef.getBytes(MAX_DOWNLOAD_SIZE).addOnSuccessListener(bytes -> {
            // Decode the byte array into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            // Set the Bitmap to the ImageView
            immagine.setImageBitmap(bitmap);
        });
    }

    public static ArrayList<ArrayList<String>> creaArrayListEsercizi(QueryDocumentSnapshot document){
        ArrayList<ArrayList<String>> esercizi = new ArrayList<>();

        for (int i = 0; i < 7; i++){
            ArrayList<String> esercizio;

            esercizio = (ArrayList<String>) document.get("esercizio" + i);
            if(esercizio != null){
                esercizi.add(esercizio);
            }
        }

        return esercizi;

    }

    public static Scheda creazioneScheda(QueryDocumentSnapshot document) {
        Scheda scheda = new Scheda(document.getString("nomeScheda"),
                document.getString("logopedista"),
                document.getString("figlio"),
                FirebaseHelper.creaArrayListEsercizi(document));

        return scheda;
    }
}
