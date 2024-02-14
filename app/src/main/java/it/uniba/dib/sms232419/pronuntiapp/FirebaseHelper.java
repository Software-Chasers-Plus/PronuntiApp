package it.uniba.dib.sms232419.pronuntiapp;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import android.media.MediaPlayer;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
}
